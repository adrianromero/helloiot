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
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.unit.StartFlow;
import com.adr.helloiot.unit.UnitPage;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    private File configfile;
    private StackPane root = null;

    private void showLogin() {
        
        ConfigProperties configprops = new ConfigProperties();            
        try {
            configprops.load(() -> new FileInputStream(configfile));
        } catch (IOException ex) {
            // No properties file found, then use defaults and continue
            LOGGER.log(Level.WARNING, "No properties file found, then use defaults and continue.", ex);
        }        

        clientlogin = new ClientLoginNode();
        clientlogin.setHost(configprops.getProperty("mqtt.host", "localhost"));
        clientlogin.setPort(Integer.parseInt(configprops.getProperty("mqtt.port", "1883")));
        clientlogin.setSSL(Boolean.parseBoolean(configprops.getProperty("mqtt.ssl", "false")));
        clientlogin.setWebSockets(Boolean.parseBoolean(configprops.getProperty("mqtt.websockets", "false")));
        clientlogin.setUserName(configprops.getProperty("mqtt.username", ""));
        clientlogin.setPassword(configprops.getProperty("mqtt.password", ""));
        clientlogin.setClientID(configprops.getProperty("mqtt.clientid", ""));
        clientlogin.setConnectionTimeout(Integer.parseInt(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT))));
        clientlogin.setKeepAliveInterval(Integer.parseInt(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT))));
        clientlogin.setDefaultQoS(Integer.parseInt(configprops.getProperty("mqtt.defaultqos", "1")));
        clientlogin.setVersion(Integer.parseInt(configprops.getProperty("mqtt.version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT))));
        clientlogin.setCleanSession(Boolean.parseBoolean(configprops.getProperty("mqtt.cleansession", Boolean.toString(MqttConnectOptions.CLEAN_SESSION_DEFAULT))));
        
        clientlogin.setTradfriHost(configprops.getProperty("tradfri.host", ""));
        clientlogin.setTradfriPsk(configprops.getProperty("tradfri.psk", ""));
        
        clientlogin.setTopicApp(configprops.getProperty("client.topicapp", "_LOCAL_/mainapp"));
        clientlogin.setTopicSys(configprops.getProperty("client.topicsys", "system"));
        clientlogin.setBrokerPane(Integer.parseInt(configprops.getProperty("client.broker", "0"))); //none

        int i = 0;
        List<TopicInfo> topicinfolist = new ArrayList<>();
        TopicInfoBuilder topicinfobuilder = new TopicInfoBuilder();
        int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
        while (i++ < topicinfosize) {
            topicinfolist.add(topicinfobuilder.fromProperties(new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(i))));
        }
        clientlogin.setTopicInfoList(topicinfobuilder, FXCollections.observableList(topicinfolist));

        clientlogin.setOnNextAction(e -> {           
            try {
                showApplication();
                hideLogin();
            } catch (HelloIoTException ex) {
                ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
                MessageUtils.showError(MessageUtils.getRoot(root), resources.getString("exception.topicinfotitle"), ex.getLocalizedMessage());
            }
            
        });
        root.getChildren().add(clientlogin.getNode());
    }

    private void hideLogin() {
        if (clientlogin != null) {
            root.getChildren().remove(clientlogin.getNode());
            clientlogin = null;
        }
    }

    private void showApplication() throws HelloIoTException {

        ConfigProperties configprops = new ConfigProperties();           
        try {
            configprops.load(() -> new FileInputStream(configfile));
        } catch (IOException ex) {
            // No properties file found, then use defaults and continue
            LOGGER.log(Level.WARNING, "No properties file found, then use defaults and continue.", ex);
        }   
        
        configprops.setProperty("mqtt.host", clientlogin.getHost());
        configprops.setProperty("mqtt.port", Integer.toString(clientlogin.getPort()));
        configprops.setProperty("mqtt.ssl", Boolean.toString(clientlogin.isSSL()));
        configprops.setProperty("mqtt.websockets", Boolean.toString(clientlogin.isWebSockets()));
        configprops.setProperty("mqtt.clientid", clientlogin.getClientID());
        configprops.setProperty("mqtt.connectiontimeout", Integer.toString(clientlogin.getConnectionTimeout()));
        configprops.setProperty("mqtt.keepaliveinterval", Integer.toString(clientlogin.getKeepAliveInterval()));
        configprops.setProperty("mqtt.defaultqos", Integer.toString(clientlogin.getDefaultQoS()));
        configprops.setProperty("mqtt.version", Integer.toString(clientlogin.getVersion()));
        configprops.setProperty("mqtt.cleansession", Boolean.toString(clientlogin.isCleanSession()));
        
        configprops.setProperty("tradfri.host", clientlogin.getTradfriHost());
        
        configprops.setProperty("client.topicapp", clientlogin.getTopicApp());
        configprops.setProperty("client.topicsys", clientlogin.getTopicSys());
        configprops.setProperty("client.broker", Integer.toString(clientlogin.getBrokerPane()));

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

        ApplicationConfig config = new ApplicationConfig();
        config.mqtt_host = clientlogin.getHost();
        config.mqtt_port = clientlogin.getPort();
        config.mqtt_ssl = clientlogin.isSSL();
        config.mqtt_websockets = clientlogin.isWebSockets();
        config.mqtt_username = clientlogin.getUserName();
        config.mqtt_password = clientlogin.getPassword();
        config.mqtt_clientid = clientlogin.getClientID();
        config.mqtt_connectiontimeout = clientlogin.getConnectionTimeout();
        config.mqtt_keepaliveinterval = clientlogin.getKeepAliveInterval();
        config.mqtt_defaultqos = clientlogin.getDefaultQoS();
        config.mqtt_version = clientlogin.getVersion();
        config.mqtt_cleansession = clientlogin.isCleanSession();
        
        config.tradfri_host = clientlogin.getTradfriHost();
        config.tradfri_psk = clientlogin.getTradfriPsk();
        
        config.topicapp = clientlogin.getTopicApp();
        config.topicsys = clientlogin.getTopicSys();

        config.app_clock = true;
        config.app_exitbutton = false;
        config.app_retryconnection = false;
        
        // Clear sensitive info
        clientlogin.setUserName("");
        clientlogin.setPassword("");
        clientlogin.setTradfriPsk("");

        helloiotapp = new HelloIoTApp(config);

        try {
            // add sample panes
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

            if (clientlogin.getBrokerPane() == 1) {
                UnitPage info = new UnitPage("info", IconBuilder.create(FontAwesome.FA_INFO, 24.0).styleClass("icon-fill").build(), resources.getString("page.info"));
                helloiotapp.addUnitPages(Arrays.asList(info));
                helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/mosquitto");
            }

            helloiotapp.addDevicesUnits(Collections.emptyList(), Collections.singletonList(new StartFlow()));
            TopicStatus ts;
            for (TopicInfo topicinfo : topicinfolist) {            
                ts = topicinfo.getTopicStatus();
                helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
            }        

            helloiotapp.addUnitPages(Arrays.asList(
                    new UnitPage("Lights", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 24.0).styleClass("icon-fill").build(), resources.getString("page.lights")))
            );
            helloiotapp.addUnitPages(Arrays.asList(
                    new UnitPage("Temperature", IconBuilder.create(FontAwesome.FA_DASHBOARD, 24.0).styleClass("icon-fill").build(), resources.getString("page.temperature")))
            );        
    //        helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/samplelights");
    //        helloiotapp.addUnitPages(Arrays.asList(
    //                new UnitPage("temperature", IconBuilder.create(FontAwesome.FA_DASHBOARD, 24.0).styleClass("icon-fill").build(), resources.getString("page.temperature")))
    //        );
    //        helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/sampletemperature");
    //
    //        ts = TopicStatus.buildTopicPublishSubscription("sample/topic1", 0, StringFormat.valueOf("DOUBLE"), true);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
    //        
    //        ts = TopicStatus.buildTopicPublishSubscription("sample/topic1", 0, StringFormat.valueOf("HEXADECIMAL"), false);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
    //        
    //        ts = TopicStatus.buildTopicPublish("sample/topic1", -1, StringFormat.valueOf("DECIMAL"), true);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
    //        
    //        ts = TopicStatus.buildTopicPublish("sample/topic1", -1, StringFormat.valueOf("BASE64"), false);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
    //        
    //        ts = TopicStatus.buildTopicSubscription("sample/topic1", 1, StringFormat.valueOf("DEGREES"), true);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
    //        
    //        ts = TopicStatus.buildTopicSubscription("sample/topic1", 1, StringFormat.valueOf("DECIMAL"), false);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
    //        
    //        ts = TopicStatus.buildTopicSubscription("$SYS/broker/uptime", -1, StringFormatIdentity.INSTANCE, false);
    //        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());  

            EventHandler<ActionEvent> showloginevent = (event -> {
                showLogin();
                hideApplication();
            });
            helloiotapp.setOnDisconnectAction(showloginevent);
            helloiotapp.getMainNode().setToolbarButton(showloginevent, IconBuilder.create(FontAwesome.FA_SIGN_OUT, 18.0).styleClass("icon-fill").build(), resources.getString("label.disconnect"));
        } catch (HelloIoTException ex) {
            helloiotapp = null;
            throw ex;
        }               
          
        // ALL the job is done
        root.getChildren().add(helloiotapp.getMainNode().getNode());
        helloiotapp.startAndConstruct();
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
        showLogin();
    }

    @Override
    public void destroy() {
        hideLogin();
        hideApplication();
    }
}
