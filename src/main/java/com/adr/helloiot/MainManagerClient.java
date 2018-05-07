//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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
package com.adr.helloiot;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.device.format.MiniVarBoolean;
import com.adr.helloiot.device.format.MiniVarInt;
import com.adr.helloiot.device.format.MiniVarString;
import com.adr.helloiot.mqtt.ConnectMQTT;
import com.adr.helloiot.tradfri.ConnectTradfri;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.Dialogs;
import com.adr.helloiot.util.HTTPUtils;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application.Parameters;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class MainManagerClient implements MainManager {

    private static final Logger LOGGER = Logger.getLogger(MainManagerClient.class.getName());
    private static final String CONFIG_PROPERTIES = ".helloiot-config.properties";
    
    private HelloIoTApp helloiotapp = null;
    
    private ClientLoginNode clientlogin = null;
    private ConnectTradfri clienttradfri = null;
    private ConnectMQTT clientmqtt = null;

    private File configfile;
    private StackPane root = null;

    private void showLogin() {

        clientlogin = new ClientLoginNode();
        
        clientmqtt = new ConnectMQTT();
        clientlogin.appendConnectNode(clientmqtt.getNode());
        clienttradfri = new ConnectTradfri();
        clientlogin.appendConnectNode(clienttradfri.getNode());     
        clientlogin.addToolbarButton(createTradfriButton());

        ConfigProperties configprops = new ConfigProperties();            
        try {
            configprops.load(() -> new FileInputStream(configfile));
        } catch (IOException ex) {        
            LOGGER.log(Level.WARNING, String.format("Using defaults. Properties file not found: %s.", configfile));            
        }        
               
        clientmqtt.loadConfig(configprops);
        clienttradfri.loadConfig(configprops);
        
        clientlogin.setTopicApp(configprops.getProperty("client.topicapp", "_LOCAL_/mainapp"));
        clientlogin.setTopicSys(configprops.getProperty("client.topicsys", "system"));
        
        clientlogin.setClock(Boolean.parseBoolean(configprops.getProperty("app.clock", "false")));
        // "app.exitbutton"
        // "app.retryconnection"
        clientlogin.setStyle(Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name()))); 
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name())));        
        
        // Now the units
        int i = 0;
        List<TopicInfo> topicinfolist = new ArrayList<>();
        TopicInfoBuilder topicinfobuilder = new TopicInfoBuilder();
        int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
        while (i++ < topicinfosize) {
            topicinfolist.add(topicinfobuilder.fromProperties(new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(i))));
        }
        clientlogin.setTopicInfoList(topicinfobuilder, FXCollections.observableList(topicinfolist));

        clientlogin.setOnNextAction(e -> {    
            hideLogin();
            try {                
                showApplication();      
            } catch (HelloIoTException ex) {
                ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
                MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("exception.topicinfotitle"), ex, ev -> {
                    showLogin();
                });
                
            }
        });
        root.getChildren().add(clientlogin.getNode());
                     
        HelloPlatform.getInstance().setProperty("window.status", Boolean.toString(false));
        HelloPlatform.getInstance().saveAppProperties();
    }

    private void hideLogin() {
        if (clientlogin != null) {
            
            ConfigProperties configprops = new ConfigProperties();           
            
            clientmqtt.saveConfig(configprops);
            clienttradfri.saveConfig(configprops);     
            
            configprops.setProperty("client.topicapp", clientlogin.getTopicApp());
            configprops.setProperty("client.topicsys", clientlogin.getTopicSys());
            
            configprops.setProperty("app.clock", Boolean.toString(clientlogin.isClock())); 
            // "app.exitbutton"
            // "app.retryconnection"
            configprops.setProperty("app.style", clientlogin.getStyle().name());
            
            // Now the units
            List<TopicInfo> topicinfolist = clientlogin.getTopicInfoList();
            configprops.setProperty("topicinfo.size", Integer.toString(topicinfolist.size()));
            int i = 0;
            for (TopicInfo topicinfo : topicinfolist) {       
                SubProperties subproperties = new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(++i));
                subproperties.setProperty(".type", topicinfo.getType());
                topicinfo.store(subproperties);
            }

            try {
                configprops.save(() -> new FileOutputStream(configfile));
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Cannot save configuration properties.", ex);
            }
        
            // And destroy
            root.getChildren().remove(clientlogin.getNode());
            clientmqtt = null;
            clienttradfri = null;
            clientlogin = null;
        }
    }

    private void showApplication() throws HelloIoTException {

        ConfigProperties configprops = new ConfigProperties();           
        try {
            configprops.load(() -> new FileInputStream(configfile));
        } catch (IOException ex) {
            // No properties file found, then use defaults and continue
            LOGGER.log(Level.WARNING, () -> String.format("Using defaults. Properties file not found: %s.", configfile));
        }         

        Map<String, MiniVar> config = new HashMap<>();
        config.put("mqtt.host", new MiniVarString(configprops.getProperty("mqtt.host", "localhost")));
        config.put("mqtt.port", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.port", "1883"))));
        config.put("mqtt.ssl", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("mqtt.ssl", "false"))));
        config.put("mqtt.websockets", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("mqtt.websockets", "false"))));
        config.put("mqtt.protocol", new MiniVarString(SSLProtocol.valueOfDefault(configprops.getProperty("mqtt.protocol", "TLSv12")).getDisplayName()));
        config.put("mqtt.keystore", new MiniVarString(configprops.getProperty("mqtt.keystore", "")));
        config.put("mqtt.keystorepassword", new MiniVarString(configprops.getProperty("mqtt.keystorepassword", "")));
        config.put("mqtt.truststore", new MiniVarString(configprops.getProperty("mqtt.truststore", "")));
        config.put("mqtt.truststorepassword", new MiniVarString(configprops.getProperty("mqtt.truststorepassword")));
        config.put("mqtt.username", new MiniVarString(configprops.getProperty("mqtt.username", "")));
        config.put("mqtt.password", new MiniVarString(configprops.getProperty("mqtt.password", "")));
        config.put("mqtt.clientid", new MiniVarString(configprops.getProperty("mqtt.clientid", "")));
        config.put("mqtt.connectiontimeout", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT)))));
        config.put("mqtt.keepaliveinterval", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT)))));
        config.put("mqtt.maxinflight", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.maxinflight", Integer.toString(MqttConnectOptions.MAX_INFLIGHT_DEFAULT)))));
        config.put("mqtt.defaultqos", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.defaultqos", "1"))));
        config.put("mqtt.version", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT))))); // MQTT_VERSION_DEFAULT = 0; MQTT_VERSION_3_1 = 3; MQTT_VERSION_3_1_1 = 4;
        config.put("client.broker", new MiniVarString(configprops.getProperty("client.broker", "0")));
        
        config.put("tradfri.host", new MiniVarString(configprops.getProperty("tradfri.host", "")));
        config.put("tradfri.identity", new MiniVarString(configprops.getProperty("tradfri.identity", "")));
        config.put("tradfri.psk", new MiniVarString(configprops.getProperty("tradfri.psk", "")));
        
        config.put("client.topicapp", new MiniVarString(configprops.getProperty("client.topicapp", "_LOCAL_/mainapp")));
        config.put("client.topicsys", new MiniVarString(configprops.getProperty("client.topicsys", "system")));

        config.put("app.clock", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("app.clock", "false"))));
        config.put("app.exitbutton", MiniVarBoolean.FALSE);
        config.put("app.retryconnection", MiniVarBoolean.FALSE);
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name())));  

        helloiotapp = new HelloIoTApp(config);

        try {
            // add sample panes
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

            if ("1".equals(config.get("client.broker").asString())) {
                UnitPage info = new UnitPage("info", IconBuilder.create(FontAwesome.FA_INFO, 24.0).styleClass("icon-fill").build(), resources.getString("page.info"));
                helloiotapp.addUnitPages(Arrays.asList(info));
                helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/mosquitto");
            }

            TopicInfoBuilder topicinfobuilder = new TopicInfoBuilder();
            int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
            int i = 0;
            while (i++ < topicinfosize) {
                TopicInfo topicinfo = topicinfobuilder.fromProperties(new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(i)));
                TopicStatus ts = topicinfo.getTopicStatus();
                helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());               
            }            
            
            if (helloiotapp.getUnits().isEmpty()) {
                throw new HelloIoTException(resources.getString("exception.emptyunits"));
           }

            helloiotapp.addUnitPages(Arrays.asList(
                    new UnitPage("Lights", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 24.0).styleClass("icon-fill").build(), resources.getString("page.lights")))
            );
            helloiotapp.addUnitPages(Arrays.asList(
                    new UnitPage("Temperature", IconBuilder.create(FontAwesome.FA_DASHBOARD, 24.0).styleClass("icon-fill").build(), resources.getString("page.temperature")))
            );        

            EventHandler<ActionEvent> showloginevent = (event -> {
                hideApplication();
                showLogin();           
            });
            helloiotapp.setOnDisconnectAction(showloginevent);
            helloiotapp.getMainNode().setToolbarButton(showloginevent, IconBuilder.create(FontAwesome.FA_SIGN_OUT, 24.0).styleClass("icon-fill").build(), resources.getString("label.disconnect"));
        } catch (HelloIoTException ex) {
            helloiotapp = null;
            throw ex;
        }               
          
        // ALL the job is done
        root.getChildren().add(helloiotapp.getMainNode().getNode());
        helloiotapp.startAndConstruct();
        
        HelloPlatform.getInstance().setProperty("window.status", Boolean.toString(true));
        HelloPlatform.getInstance().saveAppProperties();        
    }

    private void hideApplication() {
        if (helloiotapp != null) {
            helloiotapp.stopAndDestroy();
            root.getChildren().remove(helloiotapp.getMainNode().getNode());
            helloiotapp = null;
        }
    }

    @Override
    public void construct(StackPane root, Parameters params) {
        this.root = root;
        List<String> unnamed = params.getUnnamed();
        if (unnamed.isEmpty()) {
            configfile = HelloPlatform.getInstance().getFile(CONFIG_PROPERTIES);
        } else {
            String param = unnamed.get(0);
            if (Strings.isNullOrEmpty(param)) {
                configfile = HelloPlatform.getInstance().getFile(CONFIG_PROPERTIES);
            } else {
                configfile = new File(param);
            }
        }       
        
        boolean status = Boolean.parseBoolean(HelloPlatform.getInstance().getProperty("window.status", "false"));
        if (status) {
            try {                
                showApplication();      
            } catch (HelloIoTException ex) {
                ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
                MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("exception.topicinfotitle"), ex, ev -> {
                    showLogin();
                });
            }
        } else {
            showLogin();
        }
    }

    @Override
    public void destroy() {       
        hideLogin();
        hideApplication();
    }  
    
    
    private MenuItem createTradfriButton() {
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

        MenuItem b = new MenuItem(resources.getString("button.tradfri"), IconBuilder.create(FontAwesome.FA_SEARCH, 18.0).styleClass("icon-fill").build());       
        b.setOnAction(e -> {
            ConfigProperties tempconfig = new ConfigProperties();
            clienttradfri.saveConfig(tempconfig);      
            
            if (HTTPUtils.getAddress(tempconfig.getProperty("tradfri.host", "")) == null) {
                MessageUtils.showWarning(MessageUtils.getRoot(root), resources.getString("title.tradfridiscovery"), resources.getString("message.notradfriconnection"));                
                return;
            }

            DialogView loading2 = Dialogs.createLoading(resources.getString("title.tradfridiscovery"));
            loading2.show(MessageUtils.getRoot(root));    

            Futures.addCallback(clienttradfri.requestSample(
                    tempconfig.getProperty("tradfri.host"), 
                    tempconfig.getProperty("tradfri.identity"), 
                    tempconfig.getProperty("tradfri.psk")), new FutureCallback<Map<String, String>>() {
                @Override
                public void onSuccess(Map<String, String> units) {    
                    loading2.dispose();
                    for(Map.Entry<String, String> entry: units.entrySet()) {
                        clientlogin.addCodeUnit(entry.getKey(), entry.getValue());
                    }
                }                    
                @Override
                public void onFailure(Throwable ex) {                               
                    loading2.dispose();
                    MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("title.tradfridiscovery"), ex.getLocalizedMessage(), ex);
                }
            }, CompletableAsync.fxThread());  
        });
        return b;
    }      
}
