//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.device.DeviceBase;
import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.graphic.IconNull;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class ViewSimple extends Tile implements Unit {

//    private final Label title;
    private Label content;

    private final static IconStatus ICONNULL = new IconNull();
    private IconStatus iconbuilder = ICONNULL;

    private DeviceBase device = null;

    @Override
    protected Node constructContent() {
        content = new Label(null);
        content.setContentDisplay(ContentDisplay.TOP);
        content.setAlignment(Pos.CENTER);
        content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(content, Priority.SOMETIMES);
        return content;
    }

    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));
    }

    private void updateStatus(byte[] status) {
        content.setGraphic(iconbuilder.buildIcon(device.getFormat().format(status)));
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        Unit.super.construct(app);
        device.subscribeStatus(this);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        Unit.super.destroy();
        device.unsubscribeStatus(this);
    }

    public void setDevice(DeviceBase device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
        if (getIconStatus() == ICONNULL) {
            setIconStatus(device.getIconStatus());
        }
    }

    public DeviceBase getDevice() {
        return device;
    }

    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
    }

    public IconStatus getIconStatus() {
        return iconbuilder;
    }
}
