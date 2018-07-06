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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class UnitsContainerFull implements UnitsContainer {

    private VBox container = null;

    public UnitsContainerFull(double maxwidth, double maxheight) {
        container = new VBox();
        container.setMaxSize(maxwidth, maxheight);
        BorderPane.setAlignment(container, Pos.CENTER);
    }

    @Override
    public void addLayout(String layout) {
        if ("StartFull".equals(layout)) {
            HBox line = new HBox();
            line.getStyleClass().add("linecontainer");
            VBox.setVgrow(line, Priority.ALWAYS);
            container.getChildren().add(line);
        } else {
            throw new IllegalArgumentException("Layout not supported: " + layout);
        }
    }

    @Override
    public void addChildren(Node n) {
        ((Pane) container.getChildren().get(container.getChildren().size() - 1)).getChildren().add(n);
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public void showNode() {
    }
}
