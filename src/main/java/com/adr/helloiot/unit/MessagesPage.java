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

import com.adr.helloiotlib.unit.Units;
import com.adr.helloiotlib.unit.Unit;
import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.device.DeviceSubscribe;
import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatBase64;
import com.adr.helloiotlib.format.StringFormatHex;
import com.adr.helloiotlib.format.StringFormatIdentity;
import com.adr.helloiotlib.format.StringFormatJSONPretty;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
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
public class MessagesPage extends BorderPane implements Unit {

    private final ResourceBundle resources;
    
    private ListView<EventMessage> eventmessageslist;
    private ObservableList<EventMessage> eventmessagesitems;
    
    private VBox payloadcontainer;
    private Label topic;
    private TextArea payload;
    private EventMessage currentmessage = null;
    
    private Label title;
    private Button deletemessages;
    private ToggleButton showdetails;
    private ToggleGroup formatsgroup;
    private ToggleButton formatplain;
    private ToggleButton formatjson;
    private ToggleButton formathex;
    private ToggleButton formatbase64;

    private DeviceSubscribe device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);

    public MessagesPage() {
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/messages");
        load();
        
        formatplain.setSelected(true);
    }

    private void load() {

        HBox.setHgrow(this, Priority.ALWAYS);
        
        title = new Label();
        title.getStyleClass().add("messagestitle");
        
        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        sep.setFocusTraversable(false);

        deletemessages = new Button();
        deletemessages.setMnemonicParsing(false);
        deletemessages.setFocusTraversable(false);
        deletemessages.getStyleClass().add("unitbutton");
        deletemessages.setGraphic(IconBuilder.create(FontAwesome.FA_TRASH, 18.0).styleClass("icon-fill").build());
        deletemessages.setOnAction(this::actionDelete);
        
        showdetails = new ToggleButton("Details");
        showdetails.setMnemonicParsing(false);
        showdetails.setFocusTraversable(false);
        showdetails.getStyleClass().add("unittogglebutton");
        showdetails.setGraphic(IconBuilder.create(FontAwesome.FA_PLUS, 18.0).styleClass("icon-fill").build());
        showdetails.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            displayPayload(new_val);
        });
        
        
        formatsgroup = new ToggleGroup();
        formatsgroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_val, Toggle new_val) -> {
            printPayload();
        });
        
        formatplain = new ToggleButton("Plain");
        formatplain.setMnemonicParsing(false);
        formatplain.setFocusTraversable(false);
        formatplain.setToggleGroup(formatsgroup);
        formatplain.getStyleClass().add("unittogglebutton");
        formatplain.setUserData(StringFormatIdentity.INSTANCE);
        formatplain.setDisable(true);
        
        formatjson = new ToggleButton("JSON");
        formatjson.setMnemonicParsing(false);
        formatjson.setFocusTraversable(false);
        formatjson.setToggleGroup(formatsgroup);
        formatjson.getStyleClass().add("unittogglebutton");    
        formatjson.setUserData(StringFormatJSONPretty.INSTANCE);
        formatjson.setDisable(true);
        
        formathex = new ToggleButton("Hex");
        formathex.setMnemonicParsing(false);
        formathex.setFocusTraversable(false);
        formathex.setToggleGroup(formatsgroup);
        formathex.getStyleClass().add("unittogglebutton");
        formathex.setUserData(StringFormatHex.INSTANCE);        
        formathex.setDisable(true);
        
        formatbase64 = new ToggleButton("Base64");
        formatbase64.setMnemonicParsing(false);
        formatbase64.setFocusTraversable(false);
        formatbase64.setToggleGroup(formatsgroup);
        formatbase64.getStyleClass().add("unittogglebutton");  
        formatbase64.setUserData(StringFormatBase64.INSTANCE);        
        formatbase64.setDisable(true);
        

        ToolBar toolbar = new ToolBar();
        BorderPane.setAlignment(toolbar, Pos.CENTER);
        toolbar.getStyleClass().add("messagestoolbar");
        toolbar.getItems().addAll(title, sep, deletemessages, showdetails, formatplain, formatjson, formathex, formatbase64);
        setTop(toolbar);

        eventmessageslist = new ListView<>();

        eventmessageslist.setFocusTraversable(false);
        eventmessageslist.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        eventmessageslist.setCellFactory((ListView<EventMessage> list) -> new MessageCell());
        eventmessageslist.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends EventMessage> ov, EventMessage old_val, EventMessage new_val) -> {
            selectPayload(new_val);
        });
    
        eventmessagesitems = FXCollections.observableArrayList();
        eventmessageslist.setItems(eventmessagesitems);        
        BorderPane.setAlignment(eventmessageslist, Pos.CENTER);

        setCenter(eventmessageslist);
        
        payload = new TextArea();
        payload.setEditable(false);
        payload.setFocusTraversable(false);
        payload.getStyleClass().add("messageview");
        BorderPane.setAlignment(payload, Pos.CENTER);    
        
        topic = new Label();
        topic.getStyleClass().add("messagefooter");
        
        payloadcontainer = new VBox(payload, topic);
        payloadcontainer.getStyleClass().add("message");
    }

    @Override
    public void construct(IoTApp app) {
        device.subscribeStatus(messageHandler);
        eventmessagesitems.clear();
    }

    private void updateStatus(EventMessage message) {

        eventmessagesitems.add(message);
        
        int index = eventmessageslist.getSelectionModel().getSelectedIndex();
        if (index < 0 || index == eventmessagesitems.size() - 2) { 
            eventmessageslist.getSelectionModel().select(message);
            eventmessageslist.scrollTo(message);
        }
    }

    @Override
    public void destroy() {
        device.unsubscribeStatus(messageHandler);
    }

    @Override
    public Node getNode() {
        return this;
    }
    
    public void setLabel(String label) {
        title.setText(label);
    }

    public String getLabel() {
        return title.getText();
    }
    
    public void setDevice(DeviceSubscribe device) {
        this.device = device;
        if (getLabel() == null || getLabel().isEmpty()) {
            String proplabel = device.getProperties().getProperty("label");
            setLabel(proplabel == null || proplabel.isEmpty() ? device.getTopic() : proplabel);
        }        
    }

    public DeviceSubscribe getDevice() {
        return device;
    }
    
    private void actionDelete(ActionEvent ev) {
        MessageUtils.showConfirm(MessageUtils.getRoot(this), resources.getString("title.deletemessages"), resources.getString("body.deletemessages"), e -> {
            eventmessagesitems.clear();
        });     
    }

    private void selectPayload(EventMessage message) {
        currentmessage = message;
        printPayload();      
    }
    
    private void printPayload() {
        
        if (currentmessage == null) {
            topic.setText(null);
            topic.setVisible(false);
            payload.setText(null);
            payload.setVisible(false);
        } else {
            topic.setText(currentmessage.getTopic());
            topic.setVisible(true);

            StringFormat format = (StringFormat) formatsgroup.getSelectedToggle().getUserData();
            String txt = format.format(format.value(currentmessage.getMessage()));
            payload.setText(txt); 
            payload.setVisible(true);
        }
    }
    
    private void displayPayload(Boolean b) {
        setBottom(b ? payloadcontainer : null); 
        formatplain.setDisable(!b);
        formatjson.setDisable(!b);
        formathex.setDisable(!b);
        formatbase64.setDisable(!b);      
    }
}
