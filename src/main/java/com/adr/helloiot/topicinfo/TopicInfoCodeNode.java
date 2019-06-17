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
package com.adr.helloiot.topicinfo;

import com.adr.helloiot.util.FXMLNames;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author adrian
 */
public class TopicInfoCodeNode implements TopicInfoNode {
    
    Runnable updatecurrent = null;
    
    @FXML
    private GridPane container;
    @FXML
    public TextField name;
    @FXML
    public TextArea code;
    
    public TopicInfoCodeNode() {
        FXMLNames.load(this, "com/adr/helloiot/fxml/topicinfocodenode");  
    }
    
    @FXML
    public void initialize() {    
        name.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        });        
        code.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        });        
    }
    
    @Override
    public void useUpdateCurrent(Runnable updatecurrent) {
        this.updatecurrent = updatecurrent;
    }

    @Override
    public Node getNode() {
        return container;
    }
    
    private void updateCurrent() {
        if (updatecurrent != null) {
            updatecurrent.run();
        }
    } 
}
