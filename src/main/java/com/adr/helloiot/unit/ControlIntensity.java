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
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.format.MiniVarDouble;
import com.adr.helloiot.graphic.IconNull;
import com.adr.helloiot.graphic.IconStatus;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Slider;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class ControlIntensity extends Tile {
    
    private final static IconStatus ICONNULL = new IconNull();
    private IconStatus iconbuilder = ICONNULL;   
    
    private Button action;
    private Slider slider;
    private double slidervalue;
    private boolean sliderpending = false;
    private boolean sliderupdating = false;    
    
    private DeviceSimple deviceon = null;
    private final Object messageonHandler = Units.messageHandler(this::updateStatusOn);
    
    private DeviceNumber devicedim = null;
    private final Object messagedimHandler = Units.messageHandler(this::updateStatusDim);
    
    @Override
    public Node constructContent() {
        VBox vboxroot = new VBox();
        vboxroot.setSpacing(10.0);
        
        action = new Button();
        action.setContentDisplay(ContentDisplay.TOP);
        action.setFocusTraversable(false);
        action.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        action.setMnemonicParsing(false);
        action.getStyleClass().add("buttonbase");
        VBox.setVgrow(action, Priority.SOMETIMES);
        action.setOnAction(this::onAction);
        
        slider = new Slider();
        slider.setFocusTraversable(false);
        slider.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        slider.setPrefWidth(20.0);
        
        vboxroot.getChildren().addAll(action, slider);
        
        initialize();
        return vboxroot;
    }
    
    private void initialize() {
        slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if (!sliderupdating) {
                devicedim.sendStatus(new MiniVarDouble(devicedim.adjustLevel(new_val.doubleValue())));
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
    } 
    
    private void updateStatusDim(byte[] status) {
//        level.setText(device.getFormat().format(status));
        if (slider.isValueChanging()) {
            slidervalue = devicedim.getFormat().value(status).asDouble();
            sliderpending = true;
        } else {
            sliderupdating = true;
            slider.setValue(devicedim.getFormat().value(status).asDouble());
            sliderupdating = false;
        }
    }
    
    private void updateStatusOn(byte[] status) {
        action.setGraphic(iconbuilder.buildIcon(deviceon.getFormat().getValueFormat(status)));
    }
    
    @Override
    public void construct(HelloIoTAppPublic app) {
        super.construct(app);
        devicedim.subscribeStatus(messagedimHandler);
        updateStatusDim(null);
        deviceon.subscribeStatus(messageonHandler);
        updateStatusOn(null);        
    }
    
    @Override
    public void destroy() {
        super.destroy();
        devicedim.unsubscribeStatus(messagedimHandler);
        deviceon.unsubscribeStatus(messageonHandler);
    }
    
    public void setDeviceDim(DeviceNumber device) {
        this.devicedim = device;
        sliderupdating = true;
        slider.setBlockIncrement(device.getIncrement());
        slider.setMax(device.getLevelMax());
        slider.setMin(device.getLevelMin());
        sliderupdating = false;
    }

    public DeviceNumber getDeviceDim() {
        return devicedim;
    }

    public void setDeviceOn(DeviceSimple device) {
        this.deviceon = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
        if (getIconStatus() == ICONNULL) {
            setIconStatus(device.getIconStatus());
        }
    }

    public DeviceSimple getDeviceOn() {
        return deviceon;
    }

    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
    }

    public IconStatus getIconStatus() {
        return iconbuilder;
    }
    
    public void setText(String text) {
        action.setText(text);
    }
    
    public String getText() {
        return action.getText();
    }
    
    private void onAction(ActionEvent event) {
        deviceon.sendStatus(deviceon.rollNextStatus());
    }    
}
