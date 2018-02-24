
package com.adr.helloiot.tradfri;

import com.adr.helloiot.GroupManagers;
import com.adr.helloiot.ManagerProtocol;
import static com.adr.helloiot.tradfri.TradfriConstants.COLOR;
import static com.adr.helloiot.tradfri.TradfriConstants.COLOR_COLD;
import static com.adr.helloiot.tradfri.TradfriConstants.COLOR_NORMAL;
import static com.adr.helloiot.tradfri.TradfriConstants.COLOR_WARM;
import static com.adr.helloiot.tradfri.TradfriConstants.DEVICES;
import static com.adr.helloiot.tradfri.TradfriConstants.DIMMER;
import static com.adr.helloiot.tradfri.TradfriConstants.DIMMER_MAX;
import static com.adr.helloiot.tradfri.TradfriConstants.DIMMER_MIN;
import static com.adr.helloiot.tradfri.TradfriConstants.GROUPS;
import static com.adr.helloiot.tradfri.TradfriConstants.HS_ACCESSORY_LINK;
import static com.adr.helloiot.tradfri.TradfriConstants.INSTANCE_ID;
import static com.adr.helloiot.tradfri.TradfriConstants.LIGHT;
import static com.adr.helloiot.tradfri.TradfriConstants.NAME;
import static com.adr.helloiot.tradfri.TradfriConstants.ONOFF;
import static com.adr.helloiot.tradfri.TradfriConstants.TRANSITION_TIME;
import static com.adr.helloiot.tradfri.TradfriConstants.TYPE;
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
import org.eclipse.californium.core.CaliforniumLogger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

/**
 *
 * @author adrian
 */
public class ManagerTradfri implements ManagerProtocol {
    

    private final static Logger LOGGER = Logger.getLogger(ManagerTradfri.class.getName());
    private final JsonParser gsonparser = new JsonParser();
       
    static {
        CaliforniumLogger.disableLogging();
        ScandiumLogger.disable();
//		ScandiumLogger.initialize();
//		ScandiumLogger.setLevel(Level.FINE);
    }
    
    // Manager
    private GroupManagers group;   
    private Consumer<Throwable> lost;    
    // COAP
    private final String coapIP;
    private final String psk;
    private CoapEndpoint coapEndPoint = null;

    private final Map<String, Integer> name2id = new HashMap<>();
    private final List<CoapObserveRelation> watching = new ArrayList<>();
    
    public ManagerTradfri(String ip, String psk) {        
        // Create COAP connection
        this.coapIP = ip;
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
        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));
        builder.setPskStore(new StaticPskStore("", psk.getBytes()));
        DTLSConnector dtlsConnector = new DTLSConnector(builder.build());
        coapEndPoint = new CoapEndpoint(dtlsConnector, NetworkConfig.getStandard());  
        
        try {
            String response = requestCOAP(TradfriConstants.DEVICES);
            JsonArray devices = gsonparser.parse(response).getAsJsonArray();
            for (JsonElement d : devices) {
                subscribeCOAP(DEVICES + "/" + d.getAsInt());
            }
            response = requestCOAP(GROUPS);
            JsonArray groups = gsonparser.parse(response).getAsJsonArray();
            for (JsonElement g : groups) {
                subscribeCOAP(GROUPS + "/" + g.getAsInt());
            }
        } catch (TradfriException | JsonParseException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }   
    }   
    
    @Override
    public void disconnect() {
        if (coapEndPoint != null) {
            coapEndPoint.destroy();
            coapEndPoint = null;  
        }
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
        
        Integer id =  name2id.get(device);
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
            json.add(LIGHT, array);
            if (command.equals("dim")) {
                settings.addProperty(DIMMER, Math.min(DIMMER_MAX, Math.max(DIMMER_MIN, parseDim(payload))));
                settings.addProperty(TRANSITION_TIME, 3);	// transition in seconds
            } else if (command.equals("temperature")) {
                // not sure what the COLOR_X and COLOR_Y values do, it works without them...
                switch (payload) {
                case "cold":
                    settings.addProperty(COLOR, COLOR_COLD);
                    break;
                case "normal":
                    settings.addProperty(COLOR, COLOR_NORMAL);
                    break;
                case "warm":
                    settings.addProperty(COLOR, COLOR_WARM);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Invalid temperature supplied: {0}", payload);
                    return;
                }
            } else if (command.equals("on")) {
                settings.addProperty(ONOFF, payload.equals("0") ? 0 : 1);
            } else {
                LOGGER.log(Level.WARNING, "Command not supported: {0}", command);
                return;                  
            }
            sendCOAP(DEVICES + "/" + id, json.toString());
        } else if ("group".equals(type)) { // whole group
            if (command.equals("dim")) {
                json.addProperty(DIMMER, parseDim(payload));
                json.addProperty(TRANSITION_TIME, 3);
            } else if (command.equals("on")) {
                json.addProperty(ONOFF, payload.equals("0") ? 0 : 1);
            } else {
                LOGGER.log(Level.WARNING, "Command not supported: {0}", command);
                return;                
            }
            sendCOAP(GROUPS + "/" + id, json.toString());
        } else {
            LOGGER.log(Level.WARNING, "Type not supported: {0}", type);          
        }        
    }
    
    private void sendCOAP(String topic, String payload) {
        LOGGER.log(Level.FINE, "Sending COAP: {0}, {1}", new Object[]{topic, payload});
        try {
            URI uri = new URI("coaps://" + coapIP + "//" + topic);
            CoapClient client = new CoapClient(uri);
            client.setEndpoint(coapEndPoint);
            CoapResponse response = client.put(payload, MediaTypeRegistry.TEXT_PLAIN);
            if (response == null || !response.isSuccess()) {
                LOGGER.log(Level.WARNING, "COAP SEND error: {0}", response.getResponseText());
            }
            client.shutdown();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "COAP SEND error: {0}", ex.getMessage());
        }
    } 
    
    private String requestCOAP(String topic) throws TradfriException {
        try {
            URI uri = new URI("coaps://" + coapIP + "//" + topic);
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
            URI uri = new URI("coaps://" + coapIP + "//" + topic);

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
    
    private void receivedCOAP(String topic, String payload) {
        LOGGER.log(Level.FINE, "Receiving COAP: {0}, {1}", new Object[]{topic, payload});

        JsonObject json = gsonparser.parse(payload).getAsJsonObject();
        
        String name = json.get(NAME).getAsString();
        boolean register;
        if (!name2id.containsKey(name)) {
            name2id.put(name, json.get(INSTANCE_ID).getAsInt());
            register = true;                    
        } else {
            register = false;
        }

        //TODO change this test to something based on TYPE(5750) values
        // 2 = light?
        // 0 = remote/dimmer?
        if (json.has(LIGHT) && (json.has(TYPE) && json.get(TYPE).getAsInt() == 2)) { // single bulb

            JsonObject light = json.getAsJsonArray(LIGHT).get(0).getAsJsonObject();

            if (!light.has(ONOFF)) {
                LOGGER.log(Level.WARNING, "Bulb '{0}' has no On/Off value (probably no power on lightbulb socket)", json.get(NAME).getAsString());
                return; // skip this lamp for now
            }

            if (register) {
                group.distributeMessage("TRÅDFRI/registry", ("TRÅDFRI/bulb/" + name + "/on").getBytes(StandardCharsets.UTF_8));
            }            
            group.distributeMessage("TRÅDFRI/bulb/" + name + "/on", Integer.toString(light.get(ONOFF).getAsInt()).getBytes(StandardCharsets.UTF_8));
            if (light.has(DIMMER)) {
                if (register) {
                    group.distributeMessage("TRÅDFRI/registry", ("TRÅDFRI/bulb/" + name + "/dim").getBytes(StandardCharsets.UTF_8));
                }                
                group.distributeMessage("TRÅDFRI/bulb/" + name + "/dim", Integer.toString(light.get(DIMMER).getAsInt()).getBytes(StandardCharsets.UTF_8));
            }
            if (light.has(COLOR)) {
                if (register) {
                    group.distributeMessage("TRÅDFRI/registry", ("TRÅDFRI/bulb/" + name + "/temperature").getBytes(StandardCharsets.UTF_8));
                }                
                group.distributeMessage("TRÅDFRI/bulb/" + name + "/temperature",  light.get(COLOR).getAsString().getBytes(StandardCharsets.UTF_8));
            }
        } else if (json.has(HS_ACCESSORY_LINK)) { // groups have this entry
            if (register) {
                group.distributeMessage("TRÅDFRI/registry", ("TRÅDFRI/group/" + name + "/on").getBytes(StandardCharsets.UTF_8));
            }            
            group.distributeMessage("TRÅDFRI/group/" + name + "/on",  Integer.toString(json.get(ONOFF).getAsInt()).getBytes(StandardCharsets.UTF_8));
            if (json.has(DIMMER)) {
                if (register) {
                    group.distributeMessage("TRÅDFRI/registry", ("TRÅDFRI/group/" + name + "/dim").getBytes(StandardCharsets.UTF_8));
                }                  
                group.distributeMessage("TRÅDFRI/group/" + name + "/dim",  Integer.toString(json.get(DIMMER).getAsInt()).getBytes(StandardCharsets.UTF_8));
            }
        } else {
            LOGGER.log(Level.WARNING, "COAP reponse not supported: {0}", json.toString());
        }
    }
    
    private static int parseDim(String value) {
        Double d = Double.parseDouble(value);
        return d.intValue();
    }
}
