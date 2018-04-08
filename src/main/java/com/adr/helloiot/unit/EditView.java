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

import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceSubscribe;
import com.google.common.base.Strings;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public class EditView extends Tile {

    protected TextInputControl statusview;

    private DeviceSubscribe device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);      

    @Override
    public Node constructContent() {
        
        HBox hboxroot = new HBox();
        hboxroot.setSpacing(6.0);
        
        statusview = new TextField();
        statusview.setEditable(false);
        statusview.setFocusTraversable(false);
        statusview.getStyleClass().add("noneditable");
        HBox.setHgrow(statusview, Priority.SOMETIMES);
        
        hboxroot.getChildren().add(statusview);
        
        initialize();
        return hboxroot;
    }


    protected void initialize() {
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

    public void setDevice(DeviceSubscribe device) {
        this.device = device;
        if (Strings.isNullOrEmpty(getLabel())) {
            setLabel(device.getProperties().getProperty("label"));
        }
        if (device.getFormat().alignment().getHpos() == HPos.RIGHT) {
            statusview.getStyleClass().add("textinput-right");
        } else {
            statusview.getStyleClass().remove("textinput-right");
        }
    }

    public DeviceSubscribe getDevice() {
        return device;
    }
}
