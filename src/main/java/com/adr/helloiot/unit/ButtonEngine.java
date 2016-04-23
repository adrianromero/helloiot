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

import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.graphic.IconNull;
import com.adr.helloiot.graphic.IconStatus;
import com.google.common.eventbus.Subscribe;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class ButtonEngine extends Button implements Unit {

    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic"); 

    private final static IconStatus ICONNULL = new IconNull();
    private IconStatus iconbuilder = ICONNULL;    
    private DeviceSwitch device = null;
    
    private Timeline timerarm = null;
    private boolean timedarmed = false;
    private Duration initialDelay = Duration.millis(300.0);
            
    public ButtonEngine() {   
        
        setContentDisplay(ContentDisplay.TOP);
        this.getStyleClass().add("buttonbase");
        GridPane.setVgrow(this, Priority.SOMETIMES);
        GridPane.setHgrow(this, Priority.SOMETIMES);        
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setFocusTraversable(false);
        setDisable(true);
        
        setText(null);
        
        armedProperty().addListener(this::onArmedChanged); 
        setOnAction(this::onAction);
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
        if (timerarm != null) {
            timerarm.stop();
            timerarm = null;
        }
        timedarmed = false;
        device.unsubscribeStatus(this);    
    }
    
    @Override
    public void start() {
        setDisable(false);
    }

    @Override
    public void stop() {
        if (timerarm != null) {
            timerarm.stop();
            timerarm = null;
        }
        timedarmed = false;
        setDisable(true);
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));  
    }
    
    private void updateStatus(String status) {
        setGraphic(iconbuilder.buildIcon(status));
    }
    
    public void setDevice(DeviceSwitch device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }     
        if (getIconStatus() == ICONNULL) {
            setIconStatus(device.getIconStatus());
        }
    }
    
    public DeviceSwitch getDevice() {
        return device;
    }
    
    public void setLabel(String label) {
        setText(label);
    }
    
    public String getLabel() {
        return getText();
    }
    
    public void setInitialDelay(double millis) {
        initialDelay = Duration.millis(millis);
    }
    
    public double getInitialDelay() {
        return initialDelay.toMillis();
    }
    
    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
    }
    
    public IconStatus getIconStatus() {
        return iconbuilder;
    }  
    
    void onAction(ActionEvent event) {
        
        if (timerarm != null) {
            timerarm.stop();
            timerarm = null;
        }   
        
        if (!timedarmed) {
            device.sendStatus(device.nextStatus());
        }     
    }
    
    void onArmedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        
        if (timerarm != null) {
            timerarm.stop();
            timerarm = null;
        }
        
        
        if (newValue) {           
            if (initialDelay.toMillis() <= 0.0) {
                timedarmed = true;                
                device.sendON();               
            } else {    
                timedarmed = false;
                timerarm = new Timeline(new KeyFrame(initialDelay, (ActionEvent event) -> {
                    timedarmed = true;
                    device.sendON();                   
                }));
                timerarm.play();
            }
        } else if (timedarmed) {
            timedarmed = false;
            device.sendOFF();           
        }   
    }
}
