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

import com.adr.helloiot.device.DeviceNumber;
import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.format.MiniVarDouble;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 *
 * @author adrian
 */
public class SliderSimple extends Tile {

    @FXML
    private Slider slider;
    @FXML
    private Label level;

    private boolean levelupdating = false;
    private DeviceNumber device = null;

    @Override
    public Node constructContent() {
        return loadFXML("/com/adr/helloiot/fxml/slidersimple.fxml");
    }

    @FXML
    public void initialize() {
        slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if (!levelupdating) {
                device.sendStatus(device.getFormat().devalue(new MiniVarDouble(device.adjustLevel(new_val.doubleValue()))));
            }
        });
        level.setText(null);
    }

    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));
    }

    private void updateStatus(byte[] status) {
        levelupdating = true;
        level.setText(device.getFormat().format(status));
        slider.setValue(device.getFormat().value(status).asDouble());
        levelupdating = false;
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        super.construct(app);
        device.subscribeStatus(this);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        super.destroy();
        device.unsubscribeStatus(this);
    }

    public void setDevice(DeviceNumber device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
        levelupdating = true;
        slider.setBlockIncrement(device.getIncrement());
        slider.setMax(device.getLevelMax());
        slider.setMin(device.getLevelMin());
        levelupdating = false;
    }

    public DeviceNumber getDevice() {
        return device;
    }
}
