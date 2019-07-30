//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018-2019 Adrián Romero Corchado.
//
//    This file is part of HelloIot.
//
//    HelloIot is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    HelloIot is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with HelloIot.  If not, see <http://www.gnu.org/licenses/>.
//
package com.adr.helloiot.tradfri;

import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.ConnectUI;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiot.SubProperties;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.Dialogs;
import com.adr.helloiot.util.FXMLNames;
import com.adr.helloiot.util.HTTPUtils;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 *
 * @author adrian
 */
public class ConnectTradfri implements ConnectUI {
    
    @FXML private GridPane root;
    @FXML private Label labeltradfihost;
    @FXML private TextField tradfrihost;
    @FXML private Label labeltradfriidentity;
    @FXML private TextField tradfriidentity;
    @FXML private Button identity;
    private String tradfripsk;
    
    @FXML private ResourceBundle resources;
    
    public ConnectTradfri() {
        FXMLNames.load(this, "com/adr/helloiot/fxml/connecttradfri");
    }    
    
    @FXML
    public void initialize() {
        tradfrihost.textProperty().addListener((ov, old_val, new_val) ->  disableTradfri(HTTPUtils.getAddress(new_val) == null));
        disableTradfri(HTTPUtils.getAddress(tradfrihost.getText()) == null);        
    }  
    
    @Override
    public Node getNode() {
        return root;
    }   
    
    @Override
    public void requestFocus() {
        tradfrihost.requestFocus();
    }
    
    @Override
    public void loadConfig(SubProperties configprops) {
        tradfrihost.setText(configprops.getProperty("host", ""));
        tradfriidentity.setText(configprops.getProperty("identity", ""));
        tradfripsk = configprops.getProperty("psk", "");  
    }
    
    @Override
    public void saveConfig(SubProperties configprops) {
        configprops.setProperty("host", tradfrihost.getText());     
        configprops.setProperty("identity", tradfriidentity.getText());
        configprops.setProperty("psk", tradfripsk);        
    }
        
    private void disableTradfri(boolean value) {
        labeltradfriidentity.setDisable(value);
        identity.setDisable(value);
    }  
    

    @FXML
    void findTradfriBridge(ActionEvent event) {
        DialogView loading = Dialogs.createLoading();
        loading.show(MessageUtils.getRoot(root));
        CompletableAsync.handle(findBridge(),
            v -> {
                loading.dispose();
                if (v.length > 0) {       
                        tradfrihost.setText(v[0]);   
                        generateIdentity(v[0]);   
                    } else {
                    MessageUtils.showWarning(MessageUtils.getRoot(root), resources.getString("label.tradfrigateway"), resources.getString("message.cannotfindtradfri"));
                }
            },
            ex -> {
                loading.dispose();
                MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("label.tradfrigateway"), resources.getString("message.cannotfindtradfri"), ex);
            });
    }    
    

    @FXML
    void newIdentity(ActionEvent event) {
        generateIdentity(tradfrihost.getText());
    }  
    
    private void generateIdentity(String host) {
        DialogView dialog = new DialogView();
        dialog.setTitle(resources.getString("label.tradfrigateway"));
        FindTradfri contentex = new FindTradfri(host);
        dialog.setContent(contentex.getNode());     
        dialog.addButtons(dialog.createCancelButton(), dialog.createOKButton());
        dialog.setActionOK(ev -> {
            DialogView loading2 = Dialogs.createLoading();
            loading2.show(MessageUtils.getRoot(root));    
            CompletableAsync.handle(requestSharedKey(host, contentex.getPSK()), 
                key -> {
                    loading2.dispose();
                    tradfriidentity.setText(key.getKey());
                    tradfripsk = key.getValue();
                },
                ex -> {                               
                    loading2.dispose();
                    MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("label.tradfrigateway"), resources.getString("message.cannotgenerateidentity"), ex);
                });                        
        });
        dialog.show(MessageUtils.getRoot(root));          
    }

    private ListenableFuture<Pair<String, String>> requestSharedKey(String server, String psk) {       
        return CompletableAsync.supplyAsync(() -> {
            try {
                ManagerTradfri tradfri = new ManagerTradfri(server, "Client_identity", psk);
                tradfri.connectBridge();             
                String newidentity = UUID.randomUUID().toString();  
                JsonParser gsonparser = new JsonParser();
                String result = tradfri.requestPostCOAP("15011/9063", "{\"9090\":\"" + newidentity + "\"}");
                JsonObject json = gsonparser.parse(result).getAsJsonObject();
                String version = json.get("9029").getAsString();
                String newsharedkey = json.get("9091").getAsString();
                tradfri.disconnectBridge();
                
                return new Pair<>(newidentity, newsharedkey);
            } catch (TradfriException ex) {
                throw new RuntimeException(ex);
            }
        });           
    }
    
    private ListenableFuture<String[]> findBridge() { 
        return CompletableAsync.supplyAsync(() -> {
            try (JmDNS jmdns = JmDNS.create()) {
                ServiceInfo[] services  = jmdns.list("_coap._udp.local.", 5000); 
                String [] servers = new String[services.length];

                for (int i = 0; i < services.length; i++) {    
                    servers[i] = services[i].getHostAddress() + " " + services[i].getServer();
                }
                return servers;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }             
        });        
    }  
    
    public ListenableFuture<Map<String, String>> requestSample(String server, String identity, String sharedkey) {
        return CompletableAsync.supplyAsync(() -> {
            try {
                JsonParser gsonparser = new JsonParser();
                RegistryConsumerEventMessage registryconsumer = new RegistryConsumerEventMessage();
                ManagerTradfri tradfri = new ManagerTradfri(server, identity, sharedkey);
                tradfri.registerTopicsManager(registryconsumer, null);
                tradfri.connectBridge();    
                
                String response = tradfri.requestGetCOAP(TradfriConstants.DEVICES);
                JsonArray devices = gsonparser.parse(response).getAsJsonArray();
                for (JsonElement d : devices) {
                    String topic = TradfriConstants.DEVICES + "/" + d.getAsInt();
                    response = tradfri.requestGetCOAP(topic);
                    tradfri.receivedCOAP(topic, response);
                }              
                
                tradfri.disconnectBridge();
                
                return registryconsumer.getUnitsMap();
            } catch (TradfriException ex) {
                throw new RuntimeException(ex);
            }
        });          
    }   
    
    private static class RegistryConsumerEventMessage implements Consumer<EventMessage> {
        
        private Map<String, String> units = new HashMap<>();
        
        public RegistryConsumerEventMessage() {
            try {   
                units.put("Switch off", Resources.toString(getClass().getResource("/com/adr/helloiot/samples/bulball.unit"), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void accept(EventMessage message) {
            if ("TRÅDFRI/registry".equals(message.getTopic())) {
                JsonParser gsonparser = new JsonParser();
                JsonObject device = gsonparser.parse(new String(message.getMessage(), StandardCharsets.UTF_8)).getAsJsonObject();
                if ("bulb".equals(device.get("type").getAsString())) {
                    String template;
                    String name = device.get("name").getAsString();
                    String nameshort = name.replaceAll("\\s", "_");
                    if (device.get("dim").getAsBoolean()) {
                        if (device.get("temperature").getAsBoolean()) {
                            template = "/com/adr/helloiot/samples/bulbdimtemperature.unit";
                        } else {
                            template = "/com/adr/helloiot/samples/bulbdim.unit";
                        }
                    } else {
                        template = "/com/adr/helloiot/samples/bulb.unit";
                    }
                    
                    try {
                        String code = Resources.toString(getClass().getResource(template), StandardCharsets.UTF_8);
                        units.put(name, code.replaceAll("\\$\\{name\\}", name).replaceAll("\\$\\{nameshort\\}", nameshort));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } 
                }
            }
        }
        
        public Map<String, String> getUnitsMap() {
            return units;
        }   
    }
}
