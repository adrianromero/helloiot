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
import javafx.scene.control.RadioButton;
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
    private ToggleButton playpause;
    private ToggleButton showdetails;
    private ToggleGroup formatsgroup;
    private RadioButton formatplain;
    private RadioButton formatjson;
    private RadioButton formathex;
    private RadioButton formatbase64;
    
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
        
        titlesep = new Separator();
        titlesep.setOrientation(Orientation.VERTICAL);
        titlesep.setFocusTraversable(false);

        playpause = new ToggleButton();
        playpause.setMnemonicParsing(false);
        playpause.setFocusTraversable(false);
        playpause.getStyleClass().add("unittogglebutton");
        playpause.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_PLAY, 18.0).styleClass("icon-fill").build());
        playpause.setSelected(true);
        playpause.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            playpause.setGraphic(IconBuilder.create(new_val ? IconFontGlyph.FA_SOLID_PLAY : IconFontGlyph.FA_SOLID_PAUSE, 18.0).styleClass("icon-fill").build());
        });        
        
        showdetails = new ToggleButton(resources.getString("label.details"));
        showdetails.setMnemonicParsing(false);
        showdetails.setFocusTraversable(false);
        showdetails.getStyleClass().add("unittogglebutton");
        showdetails.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_PLUS, 18.0).styleClass("icon-fill").build());
        showdetails.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            displayPayload(new_val);
        });
        
        Separator formatsep = new Separator();
        formatsep.setOrientation(Orientation.VERTICAL);
        formatsep.setFocusTraversable(false);        
                
        formatsgroup = new ToggleGroup();
        formatsgroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_val, Toggle new_val) -> {
            printPayload();
        });
        
        formatplain = new RadioButton(resources.getString("label.plain"));
        formatplain.setMnemonicParsing(false);
        formatplain.setFocusTraversable(false);
        formatplain.setToggleGroup(formatsgroup);
        formatplain.getStyleClass().remove("radio-button");
        formatplain.getStyleClass().addAll("toggle-button", "unittogglebutton");        
        formatplain.setUserData(StringFormatIdentity.INSTANCE);
        formatplain.setDisable(true);
        
        formatjson = new RadioButton(resources.getString("label.json"));
        formatjson.setMnemonicParsing(false);
        formatjson.setFocusTraversable(false);
        formatjson.setToggleGroup(formatsgroup);
        formatjson.getStyleClass().remove("radio-button");
        formatjson.getStyleClass().addAll("toggle-button", "unittogglebutton");           
        formatjson.setUserData(StringFormatJSONPretty.INSTANCE);
        formatjson.setDisable(true);
        
        formathex = new RadioButton(resources.getString("label.hex"));
        formathex.setMnemonicParsing(false);
        formathex.setFocusTraversable(false);
        formathex.setToggleGroup(formatsgroup);
        formathex.getStyleClass().remove("radio-button");
        formathex.getStyleClass().addAll("toggle-button", "unittogglebutton");        
        formathex.setUserData(StringFormatHex.INSTANCE);        
        formathex.setDisable(true);
        
        formatbase64 = new RadioButton(resources.getString("label.base64"));
        formatbase64.setMnemonicParsing(false);
        formatbase64.setFocusTraversable(false);
        formatbase64.setToggleGroup(formatsgroup);
        formatbase64.getStyleClass().remove("radio-button");
        formatbase64.getStyleClass().addAll("toggle-button", "unittogglebutton");        
        formatbase64.setUserData(StringFormatBase64.INSTANCE);        
        formatbase64.setDisable(true);
        
        Separator trashsep = new Separator();
        trashsep.setOrientation(Orientation.VERTICAL);
        trashsep.setFocusTraversable(false);   

        deletemessages = new Button();
        deletemessages.setMnemonicParsing(false);
        deletemessages.setFocusTraversable(false);
        deletemessages.getStyleClass().add("unitbutton");
        deletemessages.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_TRASH_ALT, 18.0).styleClass("icon-fill").build());
        deletemessages.setOnAction(this::actionDelete);

        toolbar = new ToolBar();
        BorderPane.setAlignment(toolbar, Pos.CENTER);
        toolbar.getStyleClass().add("messagestoolbar");
        toolbar.getItems().addAll(playpause, showdetails, formatsep, formatplain, formatjson, formathex, formatbase64, trashsep, deletemessages);
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
        
        if (!playpause.isSelected()) {
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
