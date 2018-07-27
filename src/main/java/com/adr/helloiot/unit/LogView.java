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
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.device.DeviceSubscribe;
import com.google.common.base.Strings;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class LogView extends Tile {

    private TextInputControl statusview;

    private DeviceSubscribe device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);      

    @Override
    public Node constructContent() {
        StackPane stackpaneroot = new StackPane();
        stackpaneroot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        BorderPane borderpane = new BorderPane();
        
        statusview = new TextArea();
        statusview.setEditable(false);
        statusview.setFocusTraversable(false);
        statusview.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        statusview.setPrefHeight(100.0);
        statusview.getStyleClass().add("unitinputview");
        BorderPane.setAlignment(statusview, Pos.CENTER);
        
        borderpane.setCenter(statusview);
        
        stackpaneroot.getChildren().add(borderpane);
        
        initialize();
        return stackpaneroot;
    }

    protected void initialize() {
    }

    private void updateStatus(byte[] status) {
        statusview.appendText(device.getFormat().format(device.getFormat().value(status)));
        statusview.appendText("\n");
    }

    @Override
    public void construct(IoTApp app) {
        super.construct(app);
        device.subscribeStatus(messageHandler);
        // updateStatus(null);
        statusview.setText("");
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
