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
import com.adr.helloiot.device.DeviceNumber;
import com.adr.helloiot.device.format.MiniVar;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class ViewNumberProgress extends Tile {

    private Label level;
    private ProgressBar progress;

    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);    

    @Override
    protected Node constructContent() {
        VBox vboxroot = new VBox();
        vboxroot.setSpacing(10.0);        
        
        level = new Label();
        level.setAlignment(Pos.CENTER_RIGHT);
        level.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        level.getStyleClass().add("levelbase");
        
        // Get all data

        progress = new ProgressBar();
        progress.setFocusTraversable(false);
        progress.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(progress, Pos.BOTTOM_CENTER);          

        StackPane stack = new StackPane(progress);
        VBox.setVgrow(stack, Priority.SOMETIMES);   
        vboxroot.getChildren().addAll(level, stack);
        
        initialize();
        return vboxroot;
    }

    public void initialize() {
        level.setText(null);
        progress.setProgress(0.0);       
    }

    private void updateStatus(byte[] newstatus) {
        MiniVar val = device.getFormat().value(newstatus);
        level.setText(device.getFormat().format(val));      
        progress.setProgress((val.asDouble() - device.getLevelMin()) / (device.getLevelMax() - device.getLevelMin()));
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        super.construct(app);       
        device.subscribeStatus(messageHandler);
    }

    @Override
    public void destroy() {
        super.destroy();
        device.unsubscribeStatus(messageHandler);
    }

    public void setDevice(DeviceNumber device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
    }

    public DeviceNumber getDevice() {
        return device;
    }
}
