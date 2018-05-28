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

import com.adr.helloiot.HelloIoTAppPublic;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public class LabelSection extends Label implements Unit {

    protected boolean confirm = false;
    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic");

    public LabelSection() {

        this.getStyleClass().add("labelsection");
        HBox.setHgrow(this, Priority.SOMETIMES);
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setFocusTraversable(false);
    }
    
    @Override
    public void construct(HelloIoTAppPublic app) {
    }

    @Override
    public void destroy() {
    }
    
    @Override
    public Node getNode() {
        return this;
    }
}
