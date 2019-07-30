//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ChartNode {
        
    private final Group maingroup;
    private final Group fillgroup;
    private final Group linegroup;
    private final Rectangle rect;
    private final Rectangle rectclip;
    private final StackPane container;
    private final List<ShapeChartArea> shapecharts = new ArrayList<>();
    
    
    public ChartNode() {
        
        rect = new Rectangle(100.0, 100.0, Color.TRANSPARENT);
        fillgroup = new Group();
        linegroup = new Group();
        
        rectclip = new Rectangle(100.0, 100.0);
        maingroup = new Group(rect, fillgroup, linegroup);
        maingroup.setClip(rectclip);
        container = new StackPane(maingroup);
        container.widthProperty().addListener((Observable o) -> {
            resize();
        });
        container.heightProperty().addListener((Observable o) -> {
            resize();
        });        
    }
    
    public void addShapeChart(ShapeChartArea shapechart) {
        shapecharts.add(shapechart);
        fillgroup.getChildren().add(shapechart.getFill());
        linegroup.getChildren().add(shapechart.getLine());
    }
    
    public void removeAllShapeChart() {
        fillgroup.getChildren().clear();
        linegroup.getChildren().clear();
        shapecharts.clear();
    }
    
    private void resize() {
        double width = container.getWidth() - container.getInsets().getLeft() - container.getInsets().getRight();
        double height = container.getHeight() - container.getInsets().getTop() - container.getInsets().getBottom();

        rectclip.setWidth(width);
        rectclip.setHeight(height);
        
        rect.setWidth(width);
        rect.setHeight(height);

        for(ShapeChartArea shapechart : shapecharts) {
            shapechart.resize(width, height);
        }
    }    
    
    public StackPane getNode() {
        return container;
    }
}
