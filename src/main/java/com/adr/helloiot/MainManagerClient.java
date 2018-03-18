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
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.device.format.MiniVarBoolean;
import com.adr.helloiot.device.format.MiniVarString;
import com.adr.helloiot.mqtt.ConnectMQTT;
import com.adr.helloiot.tradfri.ConnectTradfri;
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
import javafx.scene.layout.StackPane;

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
        
        ConfigProperties configprops = new ConfigProperties();            
        try {
            configprops.load(() -> new FileInputStream(configfile));
        } catch (IOException ex) {
            // No properties file found, then use defaults and continue
            LOGGER.log(Level.WARNING, "No properties file found, then use defaults and continue.", ex);
        }        
        
//        Style.changeStyle(root, "/com/adr/helloiot/styles/empty");
//        Style.changeStyle(root, "/com/adr/helloiot/styles/main");
//        Style.changeStyle(root, "/com/adr/helloiot/styles/main-dark");
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.LIGHT.name())));        

        clientlogin = new ClientLoginNode();
        
        clientmqtt = new ConnectMQTT();
        clientlogin.appendConnectNode(clientmqtt.getNode());
        clienttradfri = new ConnectTradfri();
        clientlogin.appendConnectNode(clienttradfri.getNode()); 
        
        clientmqtt.loadConfig(configprops);
        clienttradfri.loadConfig(configprops);
        
        clientlogin.setTopicApp(configprops.getProperty("client.topicapp", "_LOCAL_/mainapp"));
        clientlogin.setTopicSys(configprops.getProperty("client.topicsys", "system"));
        clientlogin.setStyle(Style.valueOf(configprops.getProperty("app.style", Style.LIGHT.name()))); 
        clientlogin.setClock(Boolean.parseBoolean(configprops.getProperty("app.clock", "true")));

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
        
        clientmqtt.saveConfig(configprops);
        clienttradfri.saveConfig(configprops);
        
        configprops.setProperty("client.topicapp", clientlogin.getTopicApp());
        configprops.setProperty("client.topicsys", clientlogin.getTopicSys());
        configprops.setProperty("app.style", clientlogin.getStyle().name());
        configprops.setProperty("app.clock", Boolean.toString(clientlogin.isClock()));

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

        Map<String, MiniVar> config = new HashMap<>();
        clientmqtt.applyConfig(config);
        clienttradfri.applyConfig(config);
        
        config.put("client.topicapp", new MiniVarString(clientlogin.getTopicApp()));
        config.put("client.topicsys", new MiniVarString(clientlogin.getTopicSys()));

        config.put("app.clock", new MiniVarBoolean(clientlogin.isClock()));
        config.put("app.exitbutton", MiniVarBoolean.FALSE);
        config.put("app.retryconnection", MiniVarBoolean.FALSE);

        helloiotapp = new HelloIoTApp(config);

        try {
            // add sample panes
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

            if (config.get("client.broker").asInt() == 1) {
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
