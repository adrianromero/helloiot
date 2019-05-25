//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018-2019 Adrián Romero Corchado.
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
package com.adr.helloiot;

import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import com.adr.helloiot.util.FXMLNames;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author adrian
 */
public class TopicInfoSwitchNode implements TopicInfoNode {
    
    Runnable updatecurrent = null;
    
    @FXML
    private GridPane container;
    @FXML
    public TextField editname;
    @FXML
    public ComboBox<String> editpage;
    @FXML
    public ChoiceBox<String> editicon;
    @FXML
    public ColorPicker editcolor;
    @FXML 
    public Button clearcolor;
    @FXML
    public TextField edittopic;
    @FXML
    public TextField edittopicpub;
    
    public TopicInfoSwitchNode() {
        FXMLNames.load(this, "com/adr/helloiot/fxml/topicinfoswitchnode");  
    }
    
    @FXML
    public void initialize() {    
        editname.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        });    
        
        editpage.getItems().addAll("Lights", "Numbers"); // TODO:  add more
        editpage.getEditor().textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        });    
        
        edittopic.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        });      
        
        edittopicpub.promptTextProperty().bind(edittopic.textProperty());
        edittopicpub.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        }); 
        
        editicon.setItems(FXCollections.observableArrayList(
                "TOGGLE",
                "BULB",
                "PADLOCK",
                "POWER"));
        editicon.getSelectionModel().selectFirst();
        editicon.valueProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrent();
        });        

        clearcolor.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_TRASH_ALT, 14.0).styleClass("icon-fill").build());
        editcolor.setValue(null);
        editcolor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
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
    
    @FXML
    void onClearColor(ActionEvent event) {
        editcolor.setValue(null);
    }    
}
