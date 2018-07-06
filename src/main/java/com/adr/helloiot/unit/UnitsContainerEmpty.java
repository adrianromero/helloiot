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
package com.adr.helloiot.unit;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class UnitsContainerEmpty implements UnitsContainer {

    private Label message;
    
    public UnitsContainerEmpty(String label) {
        message = new Label(label);
        message.setAlignment(Pos.TOP_CENTER);
        message.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        message.getStyleClass().add("unitsempty");
        VBox.setVgrow(message, Priority.SOMETIMES);                    
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }    
    
    @Override
    public Node getNode() {
        return message;
    }
    
    @Override
    public void showNode() {
    }

    @Override
    public void addLayout(String layout) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public void addChildren(Node n) {
        throw new UnsupportedOperationException();
    }
}
