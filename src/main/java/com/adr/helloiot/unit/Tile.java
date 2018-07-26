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
import com.adr.textflow.TextContainer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public abstract class Tile extends BorderPane implements Unit {

    private String title = null;
    private Label titlelabel = null;
    private String footer = null;
    private FlowPane footerpane = null;
    
    private DeviceSubscribe deviceavailable = null; // can be null
    private final Object messageHandler = Units.messageHandler(this::updateAvailable);

    public Tile() {
        getStyleClass().add("unitbase");
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMinSize(150.0, Control.USE_COMPUTED_SIZE);
        setPrefSize(160.0, Control.USE_COMPUTED_SIZE);
        HBox.setHgrow(this, Priority.SOMETIMES);

        setCenter(constructContent());  
    }

    protected abstract Node constructContent();  
    
    private void updateAvailable(byte[] newstatus) {
        setDisable(!deviceavailable.getFormat().value(newstatus).asBoolean());
    }   
    
    @Override
    public void construct(HelloIoTAppPublic app) {
        if (deviceavailable != null) {
            deviceavailable.subscribeStatus(messageHandler);
        }
    }

    @Override
    public void destroy() {
        if (deviceavailable != null) {
            deviceavailable.unsubscribeStatus(messageHandler);
        }
    }    

    @Override
    public Node getNode() {
        return this;
    }

    public void setLabel(String label) {
        if (titlelabel != null) {
            getChildren().remove(titlelabel);
            titlelabel = null;
        }     
        title = label; 
        if (title != null && !title.isEmpty()) {
            titlelabel = new Label(label);
            titlelabel.getStyleClass().add("unittitle");
            titlelabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            setTop(titlelabel);
        }
    }

    public String getLabel() {
        return title;
    }
    
    public void setDeviceAvailable(DeviceSubscribe deviceavailable) {
        this.deviceavailable = deviceavailable;
    }
    
    public DeviceSubscribe getDeviceAvailable() {
        return deviceavailable;
    }

    public void setFooter(String label) {
        if (footerpane != null) {
            getChildren().remove(footerpane);
            footerpane = null;
        }
        footer = label;
        
        if (footer != null && !footer.isEmpty()) {
            try {
                footerpane = TextContainer.createFlowPane(label);
                footerpane.getStyleClass().add("unitfooter");            
                setBottom(footerpane);
            } catch (IOException ex) {
                Logger.getLogger(Tile.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public String getFooter() {
        return footer;
    }
}
