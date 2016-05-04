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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.AbstractController;
import com.adr.helloiot.device.TransmitterSimple;
import com.google.common.base.Strings;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class EditEvent extends VBox implements Unit, AbstractController {
    
    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic"); 
    
    @FXML private Label field;
    @FXML private TextInputControl payload;
    @FXML private Button fireaction;
    
    private String defaultValue = null;
    private boolean deleteSent = false;
    private TransmitterSimple device;
    
    public EditEvent() {
        loadFXML();
    }   
    
    protected void loadFXML() {
        this.load("/com/adr/helloiot/fxml/editevent.fxml"); 
    }
    
    @FXML public void initialize() {
        fireaction.setGraphic(IconBuilder.create(FontAwesome.FA_SEND, 16).build());
        setDisable(true);        
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
    
    public void setDevice(TransmitterSimple device) {
        this.device = device;
        if (Strings.isNullOrEmpty(getLabel())) {
            setLabel(device.getProperties().getProperty("label"));
        }   
    }
    
    public TransmitterSimple getDevice() {
        return device;
    }
    
    public void setLabel(String label) {
        field.setText(label);
    }
    
    public String getLabel() {
        return field.getText();
    }
    
    public void setDefaultValue(String value) {
        this.defaultValue = value;  
        payload.setText(value);   
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDeleteSent(boolean deleteSent) {
        this.deleteSent = deleteSent;
    }
    
    public boolean isDeleteSent() {
        return deleteSent;
    }
    
    @FXML
    void onSendEvent(ActionEvent event) {
        
        try {
            device.sendEvent(device.getFormat().parse(payload.getText()));
            if (deleteSent) {
                payload.setText(defaultValue);
            }
            payload.selectAll();
            payload.requestFocus();
        } catch (IllegalArgumentException ex) {
            MessageUtils.showException(MessageUtils.getRoot(this), resources.getString("label.sendevent"), resources.getString("message.valueerror"), ex);    
        }
    }
    
    @FXML
    void onEnterEvent(ActionEvent event) {
        onSendEvent(event);
    }     
}
