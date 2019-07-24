//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
import com.adr.fonticon.IconFontGlyph;
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
import java.util.Arrays;
import java.util.ResourceBundle;
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
    
    private ToolBar toolbar;
    private Label title;
    private Separator titlesep;
    private Button deletemessages;
    private Button playpause;
    private boolean isplay;
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
    }

    private void load() {

        HBox.setHgrow(this, Priority.ALWAYS);
        
        title = new Label();
        title.getStyleClass().add("messagestitle");
        
        titlesep = new Separator();
        titlesep.setOrientation(Orientation.VERTICAL);
        titlesep.setFocusTraversable(false);

        isplay = true;
        playpause = new Button();
        playpause.setMnemonicParsing(false);
        playpause.setFocusTraversable(false);
        playpause.getStyleClass().add("unitbutton");
        playpause.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_PAUSE, 18.0).styleClass("icon-fill").build());
        playpause.setOnAction(this::actionPlayPause);      
        
        deletemessages = new Button();
        deletemessages.setMnemonicParsing(false);
        deletemessages.setFocusTraversable(false);
        deletemessages.getStyleClass().add("unitbutton");
        deletemessages.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_TRASH_ALT, 18.0).styleClass("icon-fill").build());
        deletemessages.setOnAction(this::actionDelete);
        
        Separator formatsep = new Separator();
        formatsep.setOrientation(Orientation.VERTICAL);
        formatsep.setFocusTraversable(false);        
                
        formatsgroup = new ToggleGroup();
        formatsgroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_val, Toggle new_val) -> {
            printPayload(new_val);
        });
        
        formatplain = new ToggleButton(resources.getString("label.plain"));
        formatplain.setMnemonicParsing(false);
        formatplain.setFocusTraversable(false);
        formatplain.setToggleGroup(formatsgroup);
        formatplain.getStyleClass().addAll("unittogglebutton");        
        formatplain.setUserData(StringFormatIdentity.INSTANCE);
        
        formatjson = new ToggleButton(resources.getString("label.json"));
        formatjson.setMnemonicParsing(false);
        formatjson.setFocusTraversable(false);
        formatjson.setToggleGroup(formatsgroup);
        formatjson.getStyleClass().addAll("unittogglebutton");           
        formatjson.setUserData(StringFormatJSONPretty.INSTANCE);
        
        formathex = new ToggleButton(resources.getString("label.hex"));
        formathex.setMnemonicParsing(false);
        formathex.setFocusTraversable(false);
        formathex.setToggleGroup(formatsgroup);
        formathex.getStyleClass().addAll("unittogglebutton");        
        formathex.setUserData(StringFormatHex.INSTANCE);        
        
        formatbase64 = new ToggleButton(resources.getString("label.base64"));
        formatbase64.setMnemonicParsing(false);
        formatbase64.setFocusTraversable(false);
        formatbase64.setToggleGroup(formatsgroup);
        formatbase64.getStyleClass().addAll("unittogglebutton");        
        formatbase64.setUserData(StringFormatBase64.INSTANCE);        

        toolbar = new ToolBar();
        BorderPane.setAlignment(toolbar, Pos.CENTER);
        toolbar.getStyleClass().add("unittoolbar");
        toolbar.getItems().addAll(playpause, deletemessages, formatsep, formatplain, formatjson, formathex, formatbase64);
        setTop(toolbar);

        eventmessageslist = new ListView<>();

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
        
        if (!isplay) {
            return; // subscription paused
        }

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
        if (getLabel() != null && !getLabel().isEmpty()) {
            toolbar.getItems().removeAll(title, titlesep);
        }
        
        title.setText(label);
        
        if (label != null && !label.isEmpty()) {
            toolbar.getItems().addAll(0, Arrays.asList(title, titlesep));
        }
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
    
    private void actionPlayPause(ActionEvent ev) {
        isplay = !isplay;
        playpause.setGraphic(IconBuilder.create(isplay ? IconFontGlyph.FA_SOLID_PAUSE : IconFontGlyph.FA_SOLID_PLAY, 18.0).styleClass("icon-fill").build());
    }

    private void selectPayload(EventMessage message) {
        currentmessage = message;
        printPayload(formatsgroup.getSelectedToggle());      
    }
    
    private void printPayload(Toggle t) {
        
        Node n = getBottom();
        if (n == null && t != null) {
            setBottom(payloadcontainer);
        } else if (n!= null && t == null) {
            setBottom(null);
        }
        
        
        if (currentmessage == null || t == null) {
            topic.setText(null);
            topic.setVisible(false);
            payload.setText(null);
            payload.setVisible(false);
        } else {
            topic.setText(currentmessage.getTopic());
            topic.setVisible(true);
            StringFormat format = (StringFormat) t.getUserData();
            String txt = format.format(format.value(currentmessage.getMessage()));
            payload.setText(txt); 
            payload.setVisible(true);
        }
    }
}
