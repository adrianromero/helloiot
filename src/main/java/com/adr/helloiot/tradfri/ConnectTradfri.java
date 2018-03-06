//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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
package com.adr.helloiot.tradfri;

import com.adr.hellocommon.utils.FXMLUtil;
import com.adr.helloiot.ConfigProperties;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.device.format.MiniVarString;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author adrian
 */
public class ConnectTradfri {
    
    @FXML private GridPane root;
    @FXML private Label labeltradfihost;
    @FXML private TextField tradfrihost;
    @FXML private Label labeltradfripsk;
    @FXML private TextField tradfripsk;    
    
    public ConnectTradfri() {
        FXMLUtil.load(this, "/com/adr/helloiot/fxml/connecttradfri.fxml", "com/adr/helloiot/fxml/connecttradfri");
    }    
    
    @FXML
    public void initialize() {
        tradfrihost.textProperty().addListener((ov, old_val, new_val) ->  disableTradfri(new_val.isEmpty()));
        disableTradfri(tradfrihost.getText().isEmpty());        
    }  
    
    public Node getNode() {
        return root;
    }   
    
    public void loadConfig(ConfigProperties configprops) {
        tradfrihost.setText(configprops.getProperty("tradfri.host", ""));
        tradfripsk.setText(configprops.getProperty("tradfri.psk", ""));  
    }
    
    public void saveConfig(ConfigProperties configprops) {
        configprops.setProperty("tradfri.host", tradfrihost.getText());        
    }
    
    public void applyConfig(Map<String, MiniVar> config) {
        config.put("tradfri.host", new MiniVarString(tradfrihost.getText()));
        config.put("tradfri.psk", new MiniVarString(tradfripsk.getText()));   
        tradfripsk.setText("");
    }
    
    private void disableTradfri(boolean value) {
        labeltradfripsk.setDisable(value);
        tradfripsk.setDisable(value);
    }  
}
