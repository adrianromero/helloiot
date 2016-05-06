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

package com.adr.helloiot.unit;

import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceNumber;
import com.adr.helloiot.device.StatusNumber;
import com.google.common.eventbus.Subscribe;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class ViewGauge extends StackPane implements Unit  {
  
    private DeviceNumber device = null;
    
    private Gauge gauge = null;
    private String label = null;
    private GaugeType type = GaugeType.DASHBOARD;
        
    public ViewGauge() { 
        getStyleClass().add("unitbase");
        this.setPadding(new Insets(4));
        GridPane.setVgrow(this, Priority.SOMETIMES);
        GridPane.setHgrow(this, Priority.SOMETIMES);
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setDisable(true);
    }
    
    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));  
    }
    
    private void updateStatus(byte[] status) {
        
        if (gauge == null) {
            return;
        }
        
        double newvalue = StatusNumber.getFromBytes(status);
        if (newvalue < gauge.getMinValue()) {
            newvalue = gauge.getMinValue();
        }
        if (newvalue > gauge.getMaxValue()) {
            newvalue = gauge.getMaxValue();
        }
        gauge.setValue(newvalue);
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
        
    @Override
    public void start() {
        setDisable(false);
    }

    @Override
    public void stop() {
        setDisable(true);
    }

    @Override
    public Node getNode() {
        return this;
    }
    
    public void setDevice(DeviceNumber device) {
        this.device = device;       
        rebuildGauge();
    }
    
    public DeviceNumber getDevice() {
        return device;
    }
    
    public void setLabel(String label) {
        this.label = label;
        if (gauge != null) {
            gauge.setTitle(label);
        }
    }
    
    public String getLabel() {
        return label;
    }   
    
    public void setType(GaugeType type) {
        this.type = type == null ? GaugeType.DASHBOARD : type;
        rebuildGauge();
    }
    
    public GaugeType getType() {
        return type;
    }   
        
    private void rebuildGauge() {
        
        // Device not null
        
        if (gauge != null) {
            getChildren().remove(gauge);
            gauge = null;
        }
        
        if (device == null) {
            return;
        }

        gauge = type.build(device.getLevelMin(), device.getLevelMax());  
        gauge.setUnit(device.getUnit());
        getChildren().add(gauge);
        
        setLabel(label == null 
            ? device.getProperties().getProperty("label")
            : label);
    }
}
