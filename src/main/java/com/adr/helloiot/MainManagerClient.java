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

import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.local.BridgeLocal;
import com.adr.helloiot.mqtt.BridgeMQTT;
import com.adr.helloiot.properties.VarProperties;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.MiniVarString;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.Dialogs;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application.Parameters;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

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

        clientlogin.addToolbarButton(createTemplatesButton());
        // TODO: Implement Tradfri Button
        // clientlogin.addToolbarButton(createTradfriButton());
        
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

            TopicInfoBuilder topicinfobuilder = new TopicInfoBuilder();
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
        
    private Button createTemplatesButton() {
        Button b = new Button(resources.getString("title.templates"), IconBuilder.create(IconFontGlyph.FA_SOLID_FOLDER_OPEN, 18.0).styleClass("icon-fill").build());
        b.setFocusTraversable(false);
        b.setMnemonicParsing(false);
        b.getStyleClass().add("unitbutton");
        b.setOnAction(evAction -> {
            
            DialogView dialog = new DialogView();
            ListView<TemplateInfo>list = new ListView<>();
            
            list.getStyleClass().add("unitlistview");
            list.setCellFactory(l -> new TemplatesListCell());
            list.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    addTemplateToUnits(list.getSelectionModel().getSelectedItem());
                    dialog.dispose();
                }
            });

            dialog.setTitle(resources.getString("title.templates"));
            dialog.setContent(list);
            dialog.addButtons(dialog.createCancelButton(), dialog.createOKButton());
            dialog.show(MessageUtils.getRoot(root));              
            dialog.setActionOK(evOK -> {
                addTemplateToUnits(list.getSelectionModel().getSelectedItem());        
            });
                 
            // Load list of templates
            DialogView loading2 = Dialogs.createLoading();
            loading2.show(MessageUtils.getRoot(root));             
            CompletableAsync.handle(
                loadTemplatesList(),
                templateslist -> {
                    loading2.dispose();
                    list.setItems(FXCollections.observableList(Arrays.asList(templateslist)));
                    list.getSelectionModel().selectFirst();
                },
                ex -> {
                    loading2.dispose();
                    MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("title.templates"),  resources.getString("exception.cannotloadtemplateslist"), ex);             
                });
        });
        return b;
    }
    
    private void addTemplateToUnits(TemplateInfo template) {
        String fxml = "https://raw.githubusercontent.com/adrianromero/helloiot-units/master/" +
                template.file +
                (HelloPlatform.getInstance().isPhone() ? "_mobile.fxml" : ".fxml");

        // Load template code
        DialogView loading3 = Dialogs.createLoading();
        loading3.show(MessageUtils.getRoot(root));             
        CompletableAsync.handle(
                loadTemplate(fxml),
                result -> {
                    loading3.dispose();
                    clientlogin.addCodeUnit(template.name, result);
                }, 
                ex -> {
                    loading3.dispose();
                    MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("title.templates"),  resources.getString("exception.cannotloadtemplatecode"), ex);
                });         
    }
    
    private ListenableFuture<String> loadTemplate(String url) {
         return CompletableAsync.supplyAsync(() -> {
            try {
                return Resources.toString(new URL(url), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });       
    }
    
    private ListenableFuture<TemplateInfo[]> loadTemplatesList() {   
        return CompletableAsync.supplyAsync(() -> {
            try {
                String out = new Scanner(new URL("https://raw.githubusercontent.com/adrianromero/helloiot-units/master/units.json").openStream(), "UTF-8").useDelimiter("\\A").next();
                Gson gson = new Gson();
                return gson.fromJson(out, TemplateInfo[].class);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    
//    private Button createTradfriButton() {
//
//        Button b = new Button(resources.getString("button.tradfri"), IconBuilder.create(IconFontGlyph.FA_SOLID_SEARCH, 18.0).styleClass("icon-fill").build());       
//        b.setFocusTraversable(false);
//        b.setMnemonicParsing(false);
//        b.getStyleClass().add("unitbutton");       
//        b.setOnAction(e -> {
//            ConfigProperties tempconfig = new ConfigProperties();
//            clienttradfri.saveConfig(new ConfigSubProperties(tempconfig, "tradfri."));
//            
//            if (HTTPUtils.getAddress(tempconfig.getProperty("tradfri.host", "")) == null) {
//                MessageUtils.showWarning(MessageUtils.getRoot(root), resources.getString("title.tradfridiscovery"), resources.getString("message.notradfriconnection"));                
//                return;
//            }
//
//            DialogView loading2 = Dialogs.createLoading();
//            loading2.show(MessageUtils.getRoot(root));    
//
//            CompletableAsync.handle(clienttradfri.requestSample(
//                    tempconfig.getProperty("tradfri.host"), 
//                    tempconfig.getProperty("tradfri.identity"), 
//                    tempconfig.getProperty("tradfri.psk")), 
//                units -> {
//                    loading2.dispose();
//                    for(Map.Entry<String, String> entry: units.entrySet()) {
//                        clientlogin.addCodeUnit(entry.getKey(), entry.getValue());
//                    }
//                },
//                ex -> {                             
//                    loading2.dispose();
//                    MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("title.tradfridiscovery"), ex.getLocalizedMessage(), ex);
//                });  
//        });
//        return b;
//    }  

    private class TemplatesListCell extends ListCell<TemplateInfo> {
        @Override
        public void updateItem(TemplateInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setGraphic(null);
                setText(null);
            } else {               
                Text t = IconBuilder.create(IconFontGlyph.valueOf(item.icon), 18.0).styleClass("icon-fill").build();
                TextFlow tf = new TextFlow(t);
                tf.setTextAlignment(TextAlignment.CENTER);
                tf.setPadding(new Insets(2, 5, 2, 5));
                tf.setPrefWidth(36.0);           
                setGraphic(tf);
                setText(item.name);
            }
        }        
    }   
    
    private static class TemplateInfo {
        public final String name;
        public final String icon;
        public final String file;
        public TemplateInfo(String name, String icon, String file) {
            this.name = name;
            this.icon = icon;
            this.file = file;
        }
    }
}
