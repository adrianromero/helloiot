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

/**
 *
 * @author adrian
 */
public class EditAreaEvent extends EditEvent {

    @Override
    public Node constructContent() {
        HBox hboxroot = new HBox();
        hboxroot.setSpacing(6.0);
        
        payload = new TextArea();
        payload.getStyleClass().add("fieldtextbox");
        payload.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        payload.setPrefHeight(100.0);
        HBox.setHgrow(payload, Priority.SOMETIMES);

        
        fireaction = new Button();
        fireaction.setFocusTraversable(false);
        fireaction.setMnemonicParsing(false);
        fireaction.getStyleClass().add("unitbutton");
        fireaction.setOnAction(this::onSendEvent);
        
        hboxroot.getChildren().addAll(payload, fireaction);
        
        initialize();
        return hboxroot;    
    }
}
