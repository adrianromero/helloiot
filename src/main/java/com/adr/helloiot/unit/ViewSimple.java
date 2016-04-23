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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public class ViewSimple extends Label implements Unit  {
    
    private final static IconStatus ICONNULL = new IconNull();
    private IconStatus iconbuilder = ICONNULL;
    
    private DeviceBase device = null;
        
    public ViewSimple() {   
        
        setContentDisplay(ContentDisplay.TOP);
        setAlignment(Pos.CENTER);
        getStyleClass().add("labelbase");
        GridPane.setVgrow(this, Priority.SOMETIMES);
        GridPane.setHgrow(this, Priority.SOMETIMES);
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setDisable(true);
        setText(null);
    }
    
    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));  
    }
    
    private void updateStatus(String status) {
        setGraphic(iconbuilder.buildIcon(status));
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
    
    public void setLabel(String label) {
        setText(label);
    }
    
    public String getLabel() {
        return getText();
    }
    
    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
    }
    
    public IconStatus getIconStatus() {
        return iconbuilder;
    }    
}
