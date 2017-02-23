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

package com.adr.helloiot;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.client.TopicStatus;
import com.adr.helloiot.unit.StartFlow;
import com.adr.helloiot.unit.UnitPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application.Parameters;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class MainManagerClient implements MainManager {
    
    private static final Logger LOGGER = Logger.getLogger(MainManagerClient.class.getName());

    private HelloIoTApp helloiotapp = null;
    private ClientLoginNode clientlogin = null;
    
    private ConfigProperties configprops = null;
    private StackPane root = null;
    
    private void showLogin() {     

        clientlogin = new ClientLoginNode();
        clientlogin.setURL(configprops.getProperty("mqtt.url", "tcp://localhost:1883"));
        clientlogin.setUserName(configprops.getProperty("mqtt.username", ""));
        clientlogin.setClientID(configprops.getProperty("mqtt.clientid", ""));
        clientlogin.setConnectionTimeout(Integer.parseInt(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT))));
        clientlogin.setKeepAliveInterval(Integer.parseInt(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT))));
        clientlogin.setDefaultQoS(Integer.parseInt(configprops.getProperty("mqtt.defaultqos", "1")));
        clientlogin.setVersion(Integer.parseInt(configprops.getProperty("mqtt.version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT))));
        clientlogin.setCleanSession(Boolean.parseBoolean(configprops.getProperty("mqtt.cleansession", Boolean.toString(MqttConnectOptions.CLEAN_SESSION_DEFAULT))));
        clientlogin.setTopicPrefix(configprops.getProperty("mqtt.topicprefix", ""));
        clientlogin.setTopicApp(configprops.getProperty("mqtt.topicapp", "_LOCAL_/_sys_helloIoT/mainapp"));

        clientlogin.setBrokerPane(Integer.parseInt(configprops.getProperty("client.broker", "0"))); //none
        
        int i = 0;
        List<TopicInfo> topicinfolist = new ArrayList<>();
        int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
        while (i++ < topicinfosize) {
            TopicInfo topicinfo = new TopicInfo();
            topicinfo.setTopic(configprops.getProperty("topicinfo" + Integer.toString(i) + ".topic", null));
            topicinfo.setTopicpub(configprops.getProperty("topicinfo" + Integer.toString(i) + ".topicpub", null));
            topicinfo.setType(configprops.getProperty("topicinfo" + Integer.toString(i) + ".type", "Publication/Subscription"));
            topicinfo.setFormat(configprops.getProperty("topicinfo" + Integer.toString(i) + ".format", "STRING"));
            topicinfo.setJsonpath(configprops.getProperty("topicinfo" + Integer.toString(i) + ".jsonpath", null));
            topicinfo.setMultiline(Boolean.parseBoolean(configprops.getProperty("topicinfo" + Integer.toString(i) + ".multiline", "false")));
            topicinfo.setQos(Integer.parseInt(configprops.getProperty("topicinfo" + Integer.toString(i) + ".qos", "-1")));
            topicinfo.setRetained(Integer.parseInt(configprops.getProperty("topicinfo" + Integer.toString(i) + ".retained", "-1")));
            String c = configprops.getProperty("topicinfo" + Integer.toString(i) + ".color", null);
            topicinfo.setColor(c == null ? null : Color.valueOf(c));
            c = configprops.getProperty("topicinfo" + Integer.toString(i) + ".background", null);
            topicinfo.setBackground(c == null ? null : Color.valueOf(c));
            topicinfolist.add(topicinfo);
        }
        clientlogin.setTopicInfoList(FXCollections.observableList(topicinfolist));

        clientlogin.setOnNextAction(e -> {                
            showApplication();
            hideLogin(); 
        });
        root.getChildren().add(clientlogin);        
    }
    
    private void hideLogin() {
        if (clientlogin != null) {
            root.getChildren().remove(clientlogin);
            clientlogin = null;
        }        
    }
    
    private void showApplication() {
        
        configprops.clear();
        
        configprops.setProperty("mqtt.url", clientlogin.getURL());
        configprops.setProperty("mqtt.username", clientlogin.getUserName());
        configprops.setProperty("mqtt.clientid", clientlogin.getClientID());
        configprops.setProperty("mqtt.connectiontimeout", Integer.toString(clientlogin.getConnectionTimeout()));
        configprops.setProperty("mqtt.keepaliveinterval", Integer.toString(clientlogin.getKeepAliveInterval()));
        configprops.setProperty("mqtt.defaultqos", Integer.toString(clientlogin.getDefaultQoS()));
        configprops.setProperty("mqtt.version", Integer.toString(clientlogin.getVersion()));
        configprops.setProperty("mqtt.cleansession", Boolean.toString(clientlogin.isCleanSession()));
        configprops.setProperty("mqtt.topicprefix", clientlogin.getTopicPrefix());
        configprops.setProperty("mqtt.topicapp", clientlogin.getTopicApp());
        
        configprops.setProperty("client.broker", Integer.toString(clientlogin.getBrokerPane()));
        
        List<TopicInfo> topicinfolist = clientlogin.getTopicInfoList();
        configprops.setProperty("topicinfo.size", Integer.toString(topicinfolist.size()));
        int i = 0;
        for (TopicInfo topicinfo : topicinfolist) {
            configprops.setProperty("topicinfo" + Integer.toString(++i) + ".topic", topicinfo.getTopic());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".topicpub", topicinfo.getTopicpub());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".type", topicinfo.getType());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".format", topicinfo.getFormat());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".jsonpath", topicinfo.getJsonpath());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".multiline", Boolean.toString(topicinfo.isMultiline()));
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".color", topicinfo.getColor() == null ? null : topicinfo.getColor().toString());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".background", topicinfo.getBackground() == null ? null : topicinfo.getBackground().toString());
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".qos", Integer.toString(topicinfo.getQos()));
            configprops.setProperty("topicinfo" + Integer.toString(i) + ".retained", Integer.toString(topicinfo.getRetained()));
        }
        
        try {
            configprops.save();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Cannot save configuration properties.", ex);
        }

        ApplicationConfig config = new ApplicationConfig();
        config.mqtt_url = clientlogin.getURL();
        config.mqtt_username = clientlogin.getUserName();
        config.mqtt_password = clientlogin.getPassword();
        config.mqtt_clientid = clientlogin.getClientID();
        config.mqtt_connectiontimeout = clientlogin.getConnectionTimeout();
        config.mqtt_keepaliveinterval = clientlogin.getKeepAliveInterval();
        config.mqtt_defaultqos = clientlogin.getDefaultQoS();
        config.mqtt_version = clientlogin.getVersion();
        config.mqtt_cleansession = clientlogin.isCleanSession();
        config.mqtt_topicprefix = configprops.getProperty("mqtt.topicprefix", "");
        config.mqtt_topicapp = configprops.getProperty("mqtt.topicapp", "_LOCAL_/_sys_helloIoT/mainapp");
        
        config.app_clock = true;
        config.app_exitbutton = false;
        config.app_retryconnection = false;
        
        helloiotapp = new HelloIoTApp(config);
                
        // add sample panes
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");   
        
        if (clientlogin.getBrokerPane() == 1) {
            UnitPage info = new UnitPage("info", IconBuilder.create(FontAwesome.FA_INFO, 24.0).build(), resources.getString("page.info"));
            helloiotapp.addUnitPages(Arrays.asList(info));            
            helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/mosquitto");
        }
  
//        if (clientlogin.isLightsPane()) {
//            helloiotapp.addUnitPages(Arrays.asList(
//                new UnitPage("light", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 24.0).build(), resources.getString("page.light")))
//            );
//            helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/samplelights");
//        }
//        
//        if (clientlogin.isGaugesPane()) {
//            helloiotapp.addUnitPages(Arrays.asList(
//                new UnitPage("temperature", IconBuilder.create(FontAwesome.FA_DASHBOARD, 24.0).build(), resources.getString("page.temperature")))
//            );
//            helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/sampletemperature");
//        }
        
        helloiotapp.addDevicesUnits(Collections.emptyList(), Collections.singletonList(new StartFlow()));

        TopicStatus ts;
        
        for (TopicInfo topicinfo: topicinfolist) {
            if ("Subscription".equals(topicinfo.getType())) {
                ts = TopicStatus.buildTopicSubscription(topicinfo);
            } else if ("Publication".equals(topicinfo.getType())) {
                ts = TopicStatus.buildTopicPublish(topicinfo);
            } else { // "Publication/Subscription"
                ts = TopicStatus.buildTopicPublishSubscription(topicinfo);
            }
            StringBuilder style = new StringBuilder();
            if (topicinfo.getColor() != null) {
                style.append("-fx-unit-fill: ").append(webColor(topicinfo.getColor())).append(";");
            }
            if (topicinfo.getBackground() != null) {
                style.append("-fx-background-color: ").append(webColor(topicinfo.getBackground())).append(";");
            }
            ts.getUnits().get(0).getNode().setStyle(style.toString());
            
            helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());            
        }
        
//        ts = TopicStatus.buildTopicPublishSubscription("hello/test1", 0, StringFormat.valueOf("DOUBLE"), true);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        
//        ts = TopicStatus.buildTopicPublishSubscription("hello/test1", 0, StringFormat.valueOf("HEXADECIMAL"), false);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        
//        ts = TopicStatus.buildTopicPublish("hello/test1", -1, StringFormat.valueOf("DECIMAL"), true);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        
//        ts = TopicStatus.buildTopicPublish("hello/test1", -1, StringFormat.valueOf("BASE64"), false);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        
//        ts = TopicStatus.buildTopicSubscription("hello/test1", 1, StringFormat.valueOf("DEGREES"), true);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        
//        ts = TopicStatus.buildTopicSubscription("hello/test1", 1, StringFormat.valueOf("DECIMAL"), false);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        
//        ts = TopicStatus.buildTopicSubscription("$SYS/broker/uptime", -1, StringFormatIdentity.INSTANCE, false);
//        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());

        
        EventHandler<ActionEvent> showloginevent = (event -> {
            showLogin();            
            hideApplication();            
        });
        helloiotapp.setOnDisconnectAction(showloginevent);
        helloiotapp.getMQTTNode().setToolbarButton(showloginevent, IconBuilder.create(FontAwesome.FA_SIGN_OUT, 18.0).build(), resources.getString("label.disconnect"));

        root.getChildren().add(helloiotapp.getMQTTNode());
        helloiotapp.startAndConstruct();        
    }
    private String webColor(Color color) {
        return String.format( "#%02X%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255),
            (int)(color.getOpacity() * 255));        
    }
    
    private void hideApplication() {
        if (helloiotapp != null) {
            helloiotapp.stopAndDestroy();
            root.getChildren().remove(helloiotapp.getMQTTNode());
            helloiotapp = null;     
        }        
    }
    
    @Override
    public void construct(StackPane root, Parameters params) {        
        this.configprops = new ConfigProperties(params);
        this.root = root;
        try {
            this.configprops.load();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        showLogin();
    }
    
    @Override
    public void destroy() {
        hideLogin();
        hideApplication();
    }
}
