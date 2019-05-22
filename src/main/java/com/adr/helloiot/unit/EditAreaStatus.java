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

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class EditAreaStatus extends EditStatus {

    @Override
    public Node constructContent() {
        StackPane stackpaneroot = new StackPane();
        
        boxview = new HBox();
        boxview.setSpacing(6.0);
        
        statusview = new TextArea();
        statusview.setEditable(false);
        statusview.setFocusTraversable(false);
        statusview.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        statusview.setPrefHeight(100.0);
        statusview.getStyleClass().add("unitinputview");
        HBox.setHgrow(statusview, Priority.SOMETIMES);
        
        editaction = new Button();
        editaction.setFocusTraversable(false);
        editaction.setMnemonicParsing(false);
        editaction.getStyleClass().add("unitbutton");
        editaction.setOnAction(this::onEditEvent);
        
        boxview.getChildren().addAll(statusview, editaction);
        
        boxedit = new HBox();
        boxedit.setSpacing(6.0);
        boxedit.setVisible(false);
        
        statusedit = new TextArea();
        statusedit.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        statusedit.setPrefHeight(100.0);        
        statusedit.getStyleClass().add("unitinputarea");
        HBox.setHgrow(statusedit, Priority.SOMETIMES);
        
        okaction = new Button();
        okaction.setFocusTraversable(false);
        okaction.setMnemonicParsing(false);
        okaction.getStyleClass().add("unitbutton");
        okaction.setOnAction(this::onOkEvent);
        
        cancelaction = new Button();
        cancelaction.setFocusTraversable(false);
        cancelaction.setMnemonicParsing(false);
        cancelaction.getStyleClass().add("unitbutton");
        cancelaction.setOnAction(this::onCancelEvent);
        
        boxedit.getChildren().addAll(statusedit, okaction, cancelaction);
        
        stackpaneroot.getChildren().addAll(boxview, boxedit);

        initialize();
        return stackpaneroot;
    }
}
