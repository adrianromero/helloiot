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
import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceBase;
import com.adr.helloiot.device.DeviceBasic;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class EditView extends VBox implements Unit, AbstractController {
    
    @FXML private Label field;
    @FXML private TextInputControl statusview;
    
    private DeviceBase device = null;
    
    public EditView() {
        loadFXML();
    }   
    
    protected void loadFXML() {        
        this.load("/com/adr/helloiot/fxml/editview.fxml");   
    }   
    
    @FXML public void initialize() {
        setDisable(true);
    }
    
    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));  
    }

    private void updateStatus(byte[] status) {
        statusview.setText(device.getFormat().format(status));
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
        if (Strings.isNullOrEmpty(getLabel())) {
            setLabel(device.getProperties().getProperty("label"));
        }   
    }
    
    public DeviceBase getDevice() {
        return device;
    }
    
    public void setLabel(String label) {
        field.setText(label);
    }
    
    public String getLabel() {
        return field.getText();
    }   
}
