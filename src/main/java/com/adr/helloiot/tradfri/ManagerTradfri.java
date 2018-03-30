package com.adr.helloiot.tradfri;

import com.adr.helloiot.GroupManagers;
import com.adr.helloiot.ManagerProtocol;
import com.adr.helloiot.util.HTTPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

/**
 *
 * @author adrian
 */
public class ManagerTradfri implements ManagerProtocol {

    private final static Logger LOGGER = Logger.getLogger(ManagerTradfri.class.getName());
    private final JsonParser gsonparser = new JsonParser();

//    static {
//        CaliforniumLogger.disableLogging();
//        ScandiumLogger.disable();
////		ScandiumLogger.initialize();
////		ScandiumLogger.setLevel(Level.FINE);
//    }
    // Manager
    private GroupManagers group;
    private Consumer<Throwable> lost;
    // COAP
    private final String host; 
    private final String coapIP;
    private final String identity;
    private final String psk;
    private CoapEndpoint coapEndPoint = null;

    private final Map<String, Integer> name2id = new HashMap<>();
    private final List<CoapObserveRelation> watching = new ArrayList<>();

    public ManagerTradfri(String host, String identity, String psk) {
        // Create COAP connection
        this.host = host;       
        this.coapIP = HTTPUtils.getAddress(host);
        this.identity = identity;
        this.psk = psk;
    }

    @Override
    public void registerTopicsManager(GroupManagers group, Consumer<Throwable> lost) {
        this.group = group;
        this.lost = lost;
    }

    @Override
    public void registerSubscription(String topic, int qos) {
        // DO NOTHING
    }

    @Override
    public void connect() {

        connectBridge();

        try {
            String response = requestGetCOAP(TradfriConstants.DEVICES);
            JsonArray devices = gsonparser.parse(response).getAsJsonArray();
            for (JsonElement d : devices) {
                subscribeCOAP(TradfriConstants.DEVICES + "/" + d.getAsInt());
            }
            response = requestGetCOAP(TradfriConstants.GROUPS);
            JsonArray groups = gsonparser.parse(response).getAsJsonArray();
            for (JsonElement g : groups) {
                subscribeCOAP(TradfriConstants.GROUPS + "/" + g.getAsInt());
            }
        } catch (TradfriException | JsonParseException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void disconnect() {
        disconnectBridge();
    }

    @Override
    public void publish(String topic, int qos, byte[] message, boolean isRetained) {

        LOGGER.log(Level.FINE, "Publishing: {0}, {1}", new Object[]{topic, message});

        String[] parts = topic.split("/");
        if (parts.length < 4) {
            LOGGER.log(Level.WARNING, "Topic not valid for Tradfri: {0}", topic);
            return;
        }

        String type = parts[1];
        String device = parts[2];
        String command = parts[3];

        Integer id = name2id.get(device);
        if (id == null) {
            LOGGER.log(Level.WARNING, "ID not registered for name: {0}", parts[2]);
            return;
        }

        String payload = new String(message, StandardCharsets.UTF_8);
        JsonObject json = new JsonObject();
        if ("bulb".equals(type)) { // single bulb
            JsonObject settings = new JsonObject();
            JsonArray array = new JsonArray();
            array.add(settings);
            json.add(TradfriConstants.LIGHT, array);
            if (command.equals("dim")) {
                settings.addProperty(TradfriConstants.DIMMER, Math.min(TradfriConstants.DIMMER_MAX, Math.max(TradfriConstants.DIMMER_MIN, parseDim(payload))));
                settings.addProperty(TradfriConstants.TRANSITION_TIME, 3);	// transition in seconds
            } else if (command.equals("temperature")) {
                // not sure what the COLOR_X and COLOR_Y values do, it works without them...
                switch (payload) {
                case "cold":
                    settings.addProperty(TradfriConstants.COLOR, TradfriConstants.COLOR_COLD);
                    break;
                case "normal":
                    settings.addProperty(TradfriConstants.COLOR, TradfriConstants.COLOR_NORMAL);
                    break;
                case "warm":
                    settings.addProperty(TradfriConstants.COLOR, TradfriConstants.COLOR_WARM);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Invalid temperature supplied: {0}", payload);
                    return;
                }
            } else if (command.equals("on")) {
                settings.addProperty(TradfriConstants.ONOFF, payload.equals("0") ? 0 : 1);
            } else {
                LOGGER.log(Level.WARNING, "Command not supported: {0}", command);
                return;
            }
            requestPutCOAP(TradfriConstants.DEVICES + "/" + id, json.toString());
        } else if ("group".equals(type)) { // whole group
            if (command.equals("dim")) {
                json.addProperty(TradfriConstants.DIMMER, parseDim(payload));
                json.addProperty(TradfriConstants.TRANSITION_TIME, 3);
            } else if (command.equals("on")) {
                json.addProperty(TradfriConstants.ONOFF, payload.equals("0") ? 0 : 1);
            } else {
                LOGGER.log(Level.WARNING, "Command not supported: {0}", command);
                return;
            }
            requestPutCOAP(TradfriConstants.GROUPS + "/" + id, json.toString());
        } else {
            LOGGER.log(Level.WARNING, "Type not supported: {0}", type);
        }
    }

    void connectBridge() {

        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
        builder.setAddress(new InetSocketAddress(0));
        builder.setPskStore(new StaticPskStore(identity, psk.getBytes()));
        DTLSConnector dtlsConnector = new DTLSConnector(builder.build());
        CoapEndpoint.CoapEndpointBuilder coapbuilder = new CoapEndpoint.CoapEndpointBuilder();
        coapbuilder.setConnector(dtlsConnector);
        coapbuilder.setNetworkConfig(NetworkConfig.getStandard());
        coapEndPoint = coapbuilder.build();       
    }

    void disconnectBridge() {
        if (coapEndPoint != null) {
            coapEndPoint.destroy();
            coapEndPoint = null;
        }
    }

    void requestPutCOAP(String topic, String payload) {
        LOGGER.log(Level.FINE, "requext PUT COAP: {0}, {1}", new Object[]{topic, payload});
        try {
            URI uri = new URI("coaps://" + coapIP + "/" + topic);
            CoapClient client = new CoapClient(uri);
            client.setEndpoint(coapEndPoint);
            CoapResponse response = client.put(payload, MediaTypeRegistry.TEXT_PLAIN);
            if (response == null || !response.isSuccess()) {
                LOGGER.log(Level.WARNING, "COAP PUT error: {0}", response.getResponseText());
            }
            client.shutdown();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "COAP PUT error: {0}", ex.getMessage());
        }
    }

    String requestPostCOAP(String topic, String payload) throws TradfriException {
        LOGGER.log(Level.FINE, "request POST COAP: {0}, {1}", new Object[]{topic, payload});
        try {
            URI uri = new URI("coaps://" + coapIP + "/" + topic);
            CoapClient client = new CoapClient(uri);
            client.setEndpoint(coapEndPoint);
            CoapResponse response = client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
            if (response == null || !response.isSuccess()) {
                LOGGER.log(Level.WARNING, "COAP GET error: Response error.");
                throw new TradfriException("Connection to gateway failed. Check host and PSK.");
            }
            String result = response.getResponseText();
            client.shutdown();
            return result;
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "COAP GET error: {0}", ex.getMessage());
            throw new TradfriException(ex);
        }
    }

    String requestGetCOAP(String topic) throws TradfriException {
        LOGGER.log(Level.FINE, "requext GET COAP: {0}", new Object[]{topic});
        try {
            URI uri = new URI("coaps://" + coapIP + "/" + topic);
            CoapClient client = new CoapClient(uri);
            client.setEndpoint(coapEndPoint);
            CoapResponse response = client.get();
            if (response == null || !response.isSuccess()) {
                LOGGER.log(Level.WARNING, "COAP GET error: Response error.");
                throw new TradfriException("Connection to gateway failed. Check host and PSK.");
            }
            String result = response.getResponseText();
            client.shutdown();
            return result;
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "COAP GET error: {0}", ex.getMessage());
            throw new TradfriException(ex);
        }
    }

    private void subscribeCOAP(String topic) throws TradfriException {

        try {
            URI uri = new URI("coaps://" + coapIP + "/" + topic);

            CoapClient client = new CoapClient(uri);
            client.setEndpoint(coapEndPoint);
            CoapHandler handler = new CoapHandler() {
                @Override
                public void onLoad(CoapResponse response) {
                    receivedCOAP(topic, response.getResponseText());
                }

                @Override
                public void onError() {
                    LOGGER.log(Level.WARNING, "COAP subscription error");
                }
            };
            watching.add(client.observe(handler));
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "COAP SEND error: {0}", ex.getMessage());
            throw new TradfriException(ex);
        }
    }

    void receivedCOAP(String topic, String payload) {
        LOGGER.log(Level.FINE, "Receiving COAP: {0}, {1}", new Object[]{topic, payload});

        JsonObject json = gsonparser.parse(payload).getAsJsonObject();

        String name = json.get(TradfriConstants.NAME).getAsString();
        boolean register;
        JsonObject jsonregistry = new JsonObject();
        jsonregistry.addProperty("name", name);
        if (!name2id.containsKey(name)) {
            name2id.put(name, json.get(TradfriConstants.INSTANCE_ID).getAsInt());
            register = true;
        } else {
            register = false;
        }

        //TODO change this test to something based on TYPE(5750) values
        // 2 = light?
        // 0 = remote/dimmer?
        if (json.has(TradfriConstants.LIGHT) && (json.has(TradfriConstants.TYPE) && json.get(TradfriConstants.TYPE).getAsInt() == 2)) { // single bulb

            JsonObject light = json.getAsJsonArray(TradfriConstants.LIGHT).get(0).getAsJsonObject();

            if (!light.has(TradfriConstants.ONOFF)) {
                LOGGER.log(Level.WARNING, "Bulb '{0}' has no On/Off value (probably no power on lightbulb socket)", json.get(TradfriConstants.NAME).getAsString());
                return; // skip this lamp for now
            }

            group.distributeMessage("TRÅDFRI/bulb/" + name + "/on", Integer.toString(light.get(TradfriConstants.ONOFF).getAsInt()).getBytes(StandardCharsets.UTF_8));
            jsonregistry.addProperty("type", "bulb");
            if (light.has(TradfriConstants.DIMMER)) {
                group.distributeMessage("TRÅDFRI/bulb/" + name + "/dim", Integer.toString(light.get(TradfriConstants.DIMMER).getAsInt()).getBytes(StandardCharsets.UTF_8));
            }
            jsonregistry.addProperty("dim", light.has(TradfriConstants.DIMMER));
            if (light.has(TradfriConstants.COLOR)) {
                group.distributeMessage("TRÅDFRI/bulb/" + name + "/temperature", light.get(TradfriConstants.COLOR).getAsString().getBytes(StandardCharsets.UTF_8));
            }
            jsonregistry.addProperty("temperature", light.has(TradfriConstants.COLOR));
        } else if (json.has(TradfriConstants.HS_ACCESSORY_LINK)) { // groups have this entry
            group.distributeMessage("TRÅDFRI/group/" + name + "/on", Integer.toString(json.get(TradfriConstants.ONOFF).getAsInt()).getBytes(StandardCharsets.UTF_8));
            jsonregistry.addProperty("type", "group");      
            if (json.has(TradfriConstants.DIMMER)) {
                group.distributeMessage("TRÅDFRI/group/" + name + "/dim", Integer.toString(json.get(TradfriConstants.DIMMER).getAsInt()).getBytes(StandardCharsets.UTF_8));
            }
            jsonregistry.addProperty("dim", json.has(TradfriConstants.DIMMER));
        } else {
            jsonregistry.addProperty("type", "unknown");    
            LOGGER.log(Level.WARNING, "COAP reponse not supported: {0}", json.toString());
        }
        
        if (register) {
            group.distributeMessage("TRÅDFRI/registry", jsonregistry.toString().getBytes(StandardCharsets.UTF_8));
        }        
    }

    private static int parseDim(String value) {
        Double d = Double.parseDouble(value);
        return d.intValue();
    }
}
