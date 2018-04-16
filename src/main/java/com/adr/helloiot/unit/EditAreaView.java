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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class EditAreaView extends EditView {

    @Override
    public Node constructContent() {
        
        StackPane stackpaneroot = new StackPane();
        stackpaneroot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        BorderPane borderpane = new BorderPane();
        
        statusview = new TextArea();
        statusview.setEditable(false);
        statusview.setFocusTraversable(false);
        statusview.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        statusview.setPrefHeight(100.0);
        statusview.getStyleClass().add("unitinputview");
        BorderPane.setAlignment(statusview, Pos.CENTER);
        
        borderpane.setCenter(statusview);
        
        stackpaneroot.getChildren().add(borderpane);
        
        initialize();
        return stackpaneroot;
    }
}
