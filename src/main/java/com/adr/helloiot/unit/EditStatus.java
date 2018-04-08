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
package com.adr.helloiot.unit;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceSimple;
import com.google.common.base.Strings;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class EditStatus extends Tile {

    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic");

    protected TextInputControl statusview;
    protected Button editaction;
    protected TextInputControl statusedit;
    protected Button okaction;
    protected Button cancelaction;
    protected HBox boxview;
    protected HBox boxedit;

    private DeviceSimple device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);    

    @Override
    public Node constructContent() {
        
        StackPane stackpaneroot = new StackPane();
        
        boxview = new HBox();
        boxview.setSpacing(6.0);
        
        statusview = new TextField();
        statusview.setEditable(false);
        statusview.setFocusTraversable(false);
        statusview.getStyleClass().add("noneditable");
        HBox.setHgrow(statusview, Priority.SOMETIMES);
        
        editaction = new Button();
        editaction.setFocusTraversable(false);
        editaction.setMnemonicParsing(false);
        editaction.getStyleClass().add("unitbutton");
        editaction.setOnAction(this::onEditEvent);
        
        boxview.getChildren().addAll(statusview, editaction);
        
        boxedit = new HBox();
        boxedit.setSpacing(6.0);
        boxedit.setVisible(false);
        
        statusedit = new TextField();
        statusedit.getStyleClass().add("fieldtextbox");
        HBox.setHgrow(statusedit, Priority.SOMETIMES);
        ((TextField) statusedit).setOnAction(this::onEnterEvent);
        
        okaction = new Button();
        okaction.setFocusTraversable(false);
        okaction.setMnemonicParsing(false);
        okaction.getStyleClass().add("unitbutton");
        okaction.setOnAction(this::onOkEvent);
        
        cancelaction = new Button();
        cancelaction.setFocusTraversable(false);
        cancelaction.setMnemonicParsing(false);
        cancelaction.getStyleClass().add("unitbutton");
        cancelaction.setOnAction(this::onCancelEvent);
        
        boxedit.getChildren().addAll(statusedit, okaction, cancelaction);
        
        stackpaneroot.getChildren().addAll(boxview, boxedit);

        initialize();
        return stackpaneroot;
    }

    public void initialize() {
        editaction.setGraphic(IconBuilder.create(FontAwesome.FA_EDIT, 16).styleClass("icon-fill").build());
        okaction.setGraphic(IconBuilder.create(FontAwesome.FA_CHECK, 16).styleClass("icon-fill").build());
        cancelaction.setGraphic(IconBuilder.create(FontAwesome.FA_REMOVE, 16).styleClass("icon-fill").build());
        setDisable(true);
    }

    private void updateStatus(byte[] status) {
        statusview.setText(device.getFormat().format(device.getFormat().value(status)));
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        super.construct(app);
        device.subscribeStatus(messageHandler);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        super.destroy();
        device.unsubscribeStatus(messageHandler);
    }

    public void setDevice(DeviceSimple device) {
        this.device = device;
        if (Strings.isNullOrEmpty(getLabel())) {
            setLabel(device.getProperties().getProperty("label"));
        }
        if (device.getFormat().alignment().getHpos() == HPos.RIGHT) {
            statusview.getStyleClass().add("textinput-right");
            statusedit.getStyleClass().add("textinput-right");
        } else {
            statusview.getStyleClass().remove("textinput-right");
            statusedit.getStyleClass().remove("textinput-right");
        }
    }

    public DeviceSimple getDevice() {
        return device;
    }

    public void setReadOnly(boolean value) {
        editaction.setVisible(!value);
    }

    public boolean isReadOnly() {
        return !editaction.isVisible();
    }

    protected void onCancelEvent(ActionEvent event) {
        boxedit.setVisible(false);
        boxview.setVisible(true);
    }

    protected void onEditEvent(ActionEvent event) {
        boxview.setVisible(false);
        boxedit.setVisible(true);
        statusedit.setText(device.formatStatus());
        statusedit.selectAll();
        statusedit.requestFocus();
    }

    protected void onOkEvent(ActionEvent event) {
        try {
            boxedit.setVisible(false);
            boxview.setVisible(true);
            device.sendStatus(device.getFormat().parse(statusedit.getText()));
        } catch (IllegalArgumentException ex) {
            MessageUtils.showException(MessageUtils.getRoot(this), resources.getString("label.sendstatus"), resources.getString("message.valueerror"), ex);
        }
    }

    protected void onEnterEvent(ActionEvent event) {
        onOkEvent(event);
    }
}
