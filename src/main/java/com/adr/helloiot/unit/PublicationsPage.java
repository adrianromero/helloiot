//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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
package com.adr.helloiot.unit;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.device.TreePublish;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiotlib.app.TopicManager;
import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatBase64;
import com.adr.helloiotlib.format.StringFormatDecimal;
import com.adr.helloiotlib.format.StringFormatHex;
import com.adr.helloiotlib.format.StringFormatIdentity;
import com.adr.helloiotlib.format.StringFormatJSONPretty;
import com.adr.helloiotlib.unit.Unit;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class PublicationsPage extends VBox implements Unit {
    
    protected TopicManager manager;
    private final ResourceBundle resources; 

    private TreePublish device = null;
    
    private ToolBar toolbar;
    private HBox topiccontainer;
    private Label title;
    private String label = null;
    private Separator titlesep;    
    private ComboBox<String> topic;
    private TextField delay;
    private Button sendmessage;
    private TextArea payload;
    
    private ToggleButton persistent;

    private ToggleGroup qosgroup;
    private RadioButton qos0;
    private RadioButton qos1;
    private RadioButton qos2;
    
    
    private ToggleGroup formatsgroup;
    private RadioButton formatplain;
    private RadioButton formatjson;
    private RadioButton formathex;
    private RadioButton formatbase64;

    public PublicationsPage() {
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/publications");
        load();
    }

    public void load() {
        
        HBox.setHgrow(this, Priority.ALWAYS);
        
        title = new Label();
        title.getStyleClass().add("messagestitle");
        
        titlesep = new Separator();
        titlesep.setOrientation(Orientation.VERTICAL);
        titlesep.setFocusTraversable(false);        
        
        topic = new ComboBox<String>();
        topic.setPromptText(resources.getString("input.topic"));
        topic.setEditable(true);
        topic.setPrefWidth(300.0);
        topic.getStyleClass().add("comboinput");
        
        delay = new TextField();
        delay.setPromptText(resources.getString("input.delay"));
        delay.setEditable(true);
        delay.setPrefWidth(100.0);
        delay.setMinWidth(50.0);
        delay.getStyleClass().add("unitinput");   
        
        sendmessage = new Button();
        sendmessage.setMnemonicParsing(false);
        sendmessage.setFocusTraversable(false);
        sendmessage.getStyleClass().add("unitbutton");
        sendmessage.setGraphic(IconBuilder.create(FontAwesome.FA_SEND, 18.0).styleClass("icon-fill").build());
        sendmessage.setOnAction(this::actionSendMessage);
        
        topiccontainer = new HBox(topic, delay, sendmessage);    
        topiccontainer.setPadding(new Insets(5.0));
        topiccontainer.setSpacing(5.0);
        topiccontainer.setAlignment(Pos.TOP_LEFT);

        persistent = new ToggleButton(resources.getString("label.persistent"));
        persistent.setMnemonicParsing(false);
        persistent.setFocusTraversable(false);
        persistent.getStyleClass().add("unittogglebutton");  

        qosgroup = new ToggleGroup();
        
        qos0 = new RadioButton(resources.getString("label.qos0"));
        qos0.setUserData(0);
        qos0.setMnemonicParsing(false);
        qos0.setFocusTraversable(false);
        qos0.setToggleGroup(qosgroup);
        qos0.getStyleClass().remove("radio-button");
        qos0.getStyleClass().addAll("toggle-button", "unittogglebutton");  
        qos0.setSelected(true);
        
        qos1 = new RadioButton(resources.getString("label.qos1"));
        qos1.setUserData(1);
        qos1.setMnemonicParsing(false);
        qos1.setFocusTraversable(false);
        qos1.setToggleGroup(qosgroup);
        qos1.getStyleClass().remove("radio-button");
        qos1.getStyleClass().addAll("toggle-button", "unittogglebutton");  
        
        qos2 = new RadioButton(resources.getString("label.qos2"));
        qos2.setUserData(2);
        qos2.setMnemonicParsing(false);
        qos2.setFocusTraversable(false);
        qos2.setToggleGroup(qosgroup);
        qos2.getStyleClass().remove("radio-button");
        qos2.getStyleClass().addAll("toggle-button", "unittogglebutton");      
        
        Separator formatsep = new Separator();
        formatsep.setOrientation(Orientation.VERTICAL);
        formatsep.setFocusTraversable(false);        
        
        formatsgroup = new ToggleGroup();
        formatsgroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_val, Toggle new_val) -> {
            // printPayload();
        });
        
        formatplain = new RadioButton(resources.getString("label.plain"));
        formatplain.setMnemonicParsing(false);
        formatplain.setFocusTraversable(false);
        formatplain.setToggleGroup(formatsgroup);
        formatplain.getStyleClass().remove("radio-button");
        formatplain.getStyleClass().addAll("toggle-button", "unittogglebutton");  
        formatplain.setUserData(StringFormatIdentity.INSTANCE);
        formatplain.setSelected(true);
        
        formatjson = new RadioButton(resources.getString("label.json"));
        formatjson.setMnemonicParsing(false);
        formatjson.setFocusTraversable(false);
        formatjson.setToggleGroup(formatsgroup);
        formatjson.getStyleClass().remove("radio-button");
        formatjson.getStyleClass().addAll("toggle-button", "unittogglebutton");  
        formatjson.setUserData(StringFormatJSONPretty.INSTANCE);
        
        formathex = new RadioButton(resources.getString("label.hex"));
        formathex.setMnemonicParsing(false);
        formathex.setFocusTraversable(false);
        formathex.setToggleGroup(formatsgroup);
        formathex.getStyleClass().remove("radio-button");
        formathex.getStyleClass().addAll("toggle-button", "unittogglebutton");  
        formathex.setUserData(StringFormatHex.INSTANCE);        
     
        
        formatbase64 = new RadioButton(resources.getString("label.base64"));
        formatbase64.setMnemonicParsing(false);
        formatbase64.setFocusTraversable(false);
        formatbase64.setToggleGroup(formatsgroup);
        formatbase64.getStyleClass().remove("radio-button");
        formatbase64.getStyleClass().addAll("toggle-button", "unittogglebutton");  
        formatbase64.setUserData(StringFormatBase64.INSTANCE);        
        
        toolbar = new ToolBar();
        BorderPane.setAlignment(toolbar, Pos.CENTER);
        toolbar.getStyleClass().add("messagestoolbar");
        toolbar.getItems().addAll(persistent, qos0, qos1, qos2, formatsep, formatplain, formatjson, formathex, formatbase64);
        
        payload = new TextArea();
        payload.setPromptText(resources.getString("input.message"));
        payload.setEditable(true);
        payload.setFocusTraversable(false);
        payload.getStyleClass().add("unitinput");
        VBox.setVgrow(payload, Priority.ALWAYS);
        BorderPane.setAlignment(payload, Pos.CENTER);   
        
        getChildren().addAll(toolbar, topiccontainer, payload);
    }
    
    @Override
    public void construct(IoTApp app) {
    }
    
    @Override
    public void destroy() {
    }

    @Override
    public Node getNode() {
        return this;
    }
    
    public void setLabel(String label) {
        if (getLabel() != null && !getLabel().isEmpty()) {
            toolbar.getItems().removeAll(title, titlesep);
        }
        
        this.label = label;
        title.setText(label + "/");
        
        if (label != null && !label.isEmpty()) {
            toolbar.getItems().addAll(0, Arrays.asList(title, titlesep));
        }
    }

    public String getLabel() {
        return label;
    }    
    
    public void setDevice(TreePublish device) {
        this.device = device;
        
        if (getLabel() == null || getLabel().isEmpty()) {
            String proplabel = device.getProperties().getProperty("label");
            setLabel(proplabel == null || proplabel.isEmpty() ? device.getTopic() : proplabel);
        }                 
    }
    
    public TreePublish getDevice() {
        return device;
    }
    
    private void actionSendMessage(ActionEvent ev) {
        MiniVar delayvalue;
        try {
            delayvalue = StringFormatDecimal.INTEGER.parse(delay.getText());
        } catch (IllegalArgumentException ex) {
            MessageUtils.showException(MessageUtils.getRoot(this), resources.getString("label.sendmessage"), resources.getString("message.delayerror"), ex);
            return;
        }
        
        if (delayvalue.asInt() < 0) {
            MessageUtils.showError(MessageUtils.getRoot(this), resources.getString("label.sendmessage"), resources.getString("message.delayerror"));
            return;                
        }        
        
        try {
            MQTTProperty.setQos(device, (int) qosgroup.getSelectedToggle().getUserData());
            MQTTProperty.setRetained(device, persistent.isSelected());

            StringFormat format = (StringFormat) formatsgroup.getSelectedToggle().getUserData();
            device.setFormat(format);
            if (delayvalue.asInt() > 0) {
                device.sendMessage(topic.getEditor().getText(), format.parse(payload.getText()), delayvalue.asInt());
            } else {
                device.sendMessage(topic.getEditor().getText(), format.parse(payload.getText()));
            }          
        } catch (IllegalArgumentException ex) {
            MessageUtils.showException(MessageUtils.getRoot(this), resources.getString("label.sendmessage"), resources.getString("message.messageerror"), ex);
        }
        
        if (!topic.getItems().contains(topic.getEditor().getText())) {
           topic.getItems().add(0, topic.getEditor().getText());
           if (topic.getItems().size() > 20) {
               topic.getItems().remove(topic.getItems().size() - 1);
           }
        }  
    }
}
