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

import com.adr.helloiotlib.unit.Units;
import com.adr.helloiot.device.DeviceNumber;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiotlib.format.MiniVarDouble;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class SliderSimple extends Tile {

    private Slider slider;
    private double slidervalue;
    private boolean sliderpending = false;  
    private boolean sliderupdating = false;
    private Label level;

    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);

    @Override
    public Node constructContent() {
        
        VBox vboxroot = new VBox();
        vboxroot.setSpacing(10.0);
        
        level = new Label();
        level.setAlignment(Pos.CENTER_RIGHT);
        level.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        level.getStyleClass().add("unitmaintext");
        
        slider = new Slider();
        slider.setFocusTraversable(false);
        StackPane.setAlignment(slider, Pos.BOTTOM_CENTER);        
        
        StackPane stack = new StackPane(slider);
        VBox.setVgrow(stack, Priority.SOMETIMES);
        vboxroot.getChildren().addAll(level, stack);
        
        initialize();
        
        return vboxroot;
    }

    public void initialize() {
        slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if (!sliderupdating) {
                device.sendStatus(new MiniVarDouble(device.adjustLevel(new_val.doubleValue())));
            }
        });
        slider.valueChangingProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
           if (!new_val && sliderpending) {
               sliderpending = false;
               sliderupdating = true;
               slider.setValue(slidervalue);
               sliderupdating = false;
           }
        });        
        level.setText(null);                 
    }

    private void updateStatus(byte[] status) {       
        level.setText(device.getFormat().format(device.getFormat().value(status)));
        if (slider.isValueChanging()) {
            slidervalue = device.getFormat().value(status).asDouble();
            sliderpending = true;
        } else {
            sliderupdating = true;
            slider.setValue(device.getFormat().value(status).asDouble());
            sliderupdating = false;
        }       
    }

    @Override
    public void construct(IoTApp app) {
        super.construct(app);
        device.subscribeStatus(messageHandler);
        updateStatus(null);
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
        sliderupdating = true;
        slider.setBlockIncrement(device.getIncrement());
        slider.setMax(device.getLevelMax());
        slider.setMin(device.getLevelMin());
        sliderupdating = false;
    }

    public DeviceNumber getDevice() {
        return device;
    }
}
