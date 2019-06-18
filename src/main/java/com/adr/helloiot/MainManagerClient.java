//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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

import com.adr.helloiot.topicinfo.TopicInfo;
import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.local.BridgeLocal;
import com.adr.helloiot.mqtt.BridgeMQTT;
import com.adr.helloiot.properties.VarProperties;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.MiniVarString;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    
    private final ResourceBundle resources;
    private final BridgeConfig[] bridgeconfigs;
    private HelloIoTApp helloiotapp = null;
    
    private ClientLoginNode clientlogin = null;
    private ConnectUI[] connectuis = null;

    private File configfile;
    private StackPane root = null;
    
    public MainManagerClient() {
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
        bridgeconfigs = new BridgeConfig[] {
                // new BridgeConfig(new BridgeTradfri(), "TRÅDFRI/", "tradfri."),
                new BridgeConfig(new BridgeLocal(), "_LOCAL_/mainapp/", "local."),
                new BridgeConfig(new BridgeMQTT(), "", "mqtt.")}; 
    }

    private void showLogin() {

        clientlogin = new ClientLoginNode();
        
        ConfigProperties configprops = new ConfigProperties();            
        try {
            configprops.load(() -> new FileInputStream(configfile));
        } catch (IOException ex) {        
            LOGGER.log(Level.WARNING, String.format("Using defaults. Properties file not found: %s.", configfile));            
        }        
        
        connectuis = new ConnectUI[bridgeconfigs.length];
        for (int i = 0; i < bridgeconfigs.length; i++) {
            connectuis[i] = bridgeconfigs[i].getBridge().createConnectUI();
            if (connectuis[i] != null) {
                clientlogin.appendConnectNode(connectuis[i].getNode()); 
                connectuis[i].loadConfig(new ConfigSubProperties(configprops, bridgeconfigs[i].getPrefix()));
            }
        }
        
        clientlogin.setTopicSys(configprops.getProperty("app.topicsys", "system/"));      
        clientlogin.setTopicApp(configprops.getProperty("app.topicapp", "_LOCAL_/mainapp/"));      
        clientlogin.setClock(Boolean.parseBoolean(configprops.getProperty("app.clock", "false")));
        // "app.exitbutton"
        // "app.retryconnection"
        clientlogin.setStyle(Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name()))); 
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name())));        
        
        // Now the units
        int i = 0;
        List<TopicInfo> topicinfolist = new ArrayList<>();
        TopicInfoBuilder topicinfobuilder = TopicInfoBuilder.INSTANCE;
        int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
        while (i++ < topicinfosize) {
            topicinfolist.add(topicinfobuilder.fromProperties(new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(i))));
        }
        clientlogin.setTopicInfoList(FXCollections.observableList(topicinfolist));

        clientlogin.setOnNextAction(e -> {    
            hideLogin();
            try {                
                showApplication();      
            } catch (HelloIoTException ex) {
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
            
            for (int i = 0; i < bridgeconfigs.length; i++) {
                if (connectuis[i] != null) {
                    connectuis[i].saveConfig(new ConfigSubProperties(configprops, bridgeconfigs[i].getPrefix()));
                }
            }            
            
            configprops.setProperty("app.topicsys", clientlogin.getTopicSys());    
            configprops.setProperty("app.topicapp", clientlogin.getTopicApp());
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
                subproperties.setProperty(".type", topicinfo.getFactory().getType());
                topicinfo.store(subproperties);
            }

            try {
                configprops.save(() -> new FileOutputStream(configfile));
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Cannot save configuration properties.", ex);
            }
        
            // And destroy
            root.getChildren().remove(clientlogin.getNode());
            
            connectuis = null;
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

        VarProperties config = new VarProperties();
        
        for (BridgeConfig bc : bridgeconfigs) {
            config.readConfiguration(bc, configprops);
        }  
        
        config.put("app.topicsys", new MiniVarString(configprops.getProperty("app.topicsys", "system/")));
        config.put("app.topicapp", new MiniVarString(configprops.getProperty("app.topicapp", "_LOCAL_/mainapp/")));
        config.put("app.clock", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("app.clock", "false"))));
        config.put("app.exitbutton", MiniVarBoolean.FALSE);
        config.put("app.retryconnection", MiniVarBoolean.FALSE);
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name())));  

        helloiotapp = new HelloIoTApp(bridgeconfigs, config);
        try {         

            TopicInfoBuilder topicinfobuilder = TopicInfoBuilder.INSTANCE;
            int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
            int i = 0;
            while (i++ < topicinfosize) {
                TopicInfo topicinfo = topicinfobuilder.fromProperties(new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(i)));
                DevicesUnits ts = topicinfo.getDevicesUnits();
                helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());               
            }            
            
            if (helloiotapp.getUnits().isEmpty()) {
                throw new HelloIoTException(resources.getString("exception.emptyunits"));
           }

            // Add all devices and units
            helloiotapp.addServiceDevicesUnits();
        
            EventHandler<ActionEvent> showloginevent = (event -> {
                hideApplication();
                showLogin();           
            });
            helloiotapp.setOnDisconnectAction(showloginevent);
            helloiotapp.getMainNode().setToolbarButton(showloginevent, IconBuilder.create(IconFontGlyph.FA_SOLID_SIGN_OUT_ALT, 24.0).styleClass("icon-fill").build(), resources.getString("label.disconnect"));
        } catch (HelloIoTException ex) {
            helloiotapp = null;
            throw ex;            
        } catch (Exception ex2) {
            helloiotapp = null;
            throw new HelloIoTException(resources.getString("exception.unexpected"), ex2);
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
            if (param == null || param.isEmpty()) {
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
}
