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

import java.util.List;
import javafx.scene.Node;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

public class ShapeChartArea implements ChartSerieListener {
    
    private double width = 100.0;
    private double height = 100.0;
    
    private final Path pFill;
    private final Path pLine;
    private final ChartSerie serie;
    
    public ShapeChartArea(ChartSerie serie) {
        
        pFill = new Path();
        pFill.getStyleClass().add(serie.getStyleClass() + "-fill");
        pFill.setStrokeWidth(0.0);        
        
        pLine = new Path();
        pLine.getStyleClass().add(serie.getStyleClass() + "-line");        
        
        this.serie = serie;
        serie.setListener(this);
    }
    
    public Node getFill() {
        return pFill;
    }
    
    public Node getLine() {
        return pLine;
    }
    
    private void render() {
        
        List<Double> data = serie.getData();
        
        if (data != null && data.size() > 0) {
        
            PathElement[] elements = new PathElement[data.size()];
            PathElement[] elementsLine = new PathElement[data.size()];

            if (data.size() == 1) {
                double y = 0.1 * height + 0.8 * height * (1.0 - data.get(0));
                elements = new PathElement[2];
                elements[0] = new LineTo(0.0, y);
                elements[1] = new LineTo(width, y);
                elementsLine = new PathElement[2];
                elementsLine[0] = new MoveTo(0.0, y);
                elementsLine[1] = new LineTo(width, y);       
            } else {
                elements = new PathElement[data.size()];
                int i = 0;
                for (double d : data) {
                    double x = i * width / (elements.length - 1);
                    double y = 0.1 * height + 0.8 * height * (1.0 - data.get(i));
                    elements[i] = new LineTo(x, y);
                    elementsLine[i] = i == 0 ? new MoveTo(x, y) : new LineTo(x, y);
                    i++;                   
                }
            }

            pFill.getElements().clear();
            pFill.getElements().add(new MoveTo(0, height));
            pFill.getElements().addAll(elements);
            pFill.getElements().add(new LineTo(width, height));
            
            pLine.getElements().clear();
            pLine.getElements().addAll(elementsLine);
        }
    }

    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
        render();
    }

    @Override
    public void handleData() {
        render();
    }
}
