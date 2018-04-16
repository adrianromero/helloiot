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
import com.adr.helloiot.device.TransmitterSimple;
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

/**
 *
 * @author adrian
 */
public class EditEvent extends Tile {

    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic");

    protected TextInputControl payload;
    protected Button fireaction;

    private String defaultValue = null;
    private boolean deleteSent = false;
    private TransmitterSimple device;

    @Override
    public Node constructContent() {
        HBox hboxroot = new HBox();
        hboxroot.setSpacing(6.0);
        
        payload = new TextField();
        payload.getStyleClass().add("unitinput");
        HBox.setHgrow(payload, Priority.SOMETIMES);
        ((TextField)payload).setOnAction(this::onEnterEvent);
        
        fireaction = new Button();
        fireaction.setFocusTraversable(false);
        fireaction.setMnemonicParsing(false);
        fireaction.getStyleClass().add("unitbutton");
        fireaction.setOnAction(this::onSendEvent);
        
        hboxroot.getChildren().addAll(payload, fireaction);
        
        initialize();
        return hboxroot;
    }

    public void initialize() {
        fireaction.setGraphic(IconBuilder.create(FontAwesome.FA_SEND, 16).styleClass("icon-fill").build());

        setDisable(true);
    }

    public void setDevice(TransmitterSimple device) {
        this.device = device;
        if (Strings.isNullOrEmpty(getLabel())) {
            setLabel(device.getProperties().getProperty("label"));
        }
        if (device.getFormat().alignment().getHpos() == HPos.RIGHT) {
            payload.getStyleClass().add("textinput-right");
        } else {
            payload.getStyleClass().remove("textinput-right");
        }
    }

    public TransmitterSimple getDevice() {
        return device;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
        payload.setText(value);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDeleteSent(boolean deleteSent) {
        this.deleteSent = deleteSent;
    }

    public boolean isDeleteSent() {
        return deleteSent;
    }

    protected void onSendEvent(ActionEvent event) {

        try {
            device.sendStatus(device.getFormat().parse(payload.getText()));
            if (deleteSent) {
                payload.setText(defaultValue);
            }
            payload.selectAll();
            payload.requestFocus();
        } catch (IllegalArgumentException ex) {
            MessageUtils.showException(MessageUtils.getRoot(this), resources.getString("label.sendevent"), resources.getString("message.valueerror"), ex);
        }
    }

    protected void onEnterEvent(ActionEvent event) {
        onSendEvent(event);
    }
}
