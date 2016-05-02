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

import com.adr.hellocommon.utils.AbstractController;
import com.adr.helloiot.device.DeviceBase;
import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class ViewText extends StackPane implements Unit, AbstractController {
    
    @FXML private Label level;
    @FXML private Label label; 
    
    private DeviceBase device = null;
    
    public ViewText() {   
        this.load("/com/adr/helloiot/fxml/viewtext.fxml");  
    }
    
    @FXML public void initialize() {
        label.setText(null);
        level.setText(null);
        setDisable(true);
    }
    
    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));  
    } 
    
    private void updateStatus(byte[] newstatus) {
        level.setText(device.getFormat().format(newstatus));
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
    }
    
    public DeviceBase getDevice() {
        return device;
    }
    
    
    public void setLabel(String value) {
        label.setText(value);
    }
    
    public String getLabel() {
        return label.getText();
    }    
}
