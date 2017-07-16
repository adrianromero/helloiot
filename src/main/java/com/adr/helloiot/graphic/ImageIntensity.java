//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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
package com.adr.helloiot.graphic;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author adrian
 */
public class ImageIntensity extends Group {
    
    private final Node graphic;
    private final double graphicw;
    private final double graphich;
    private final Rectangle r = new Rectangle();
    private final Rectangle rBase = new Rectangle();
   
    
    
    public ImageIntensity(Node graphic) {
        
        this.graphic = graphic;
        graphicw = graphic.getLayoutBounds().getWidth();
        graphich = graphic.getLayoutBounds().getHeight();
        
        rBase.setOpacity(0.7);
        rBase.setFill(Color.DARKGRAY);
        rBase.setLayoutX(-4.0);
        rBase.setLayoutY(graphich - 12);
        rBase.setHeight(5.0);
        rBase.setWidth(graphicw + 8.0);
        
        r.setOpacity(0.7);        
        r.setFill(Color.BLUE);
        r.setLayoutX(-4.0);
        r.setLayoutY(graphich - 13.0);
        r.setHeight(7.0);
        r.setWidth(0.0);

        
        getChildren().addAll(graphic, rBase, r);    
    }
       
    public void setWidth(double value) {
        r.setWidth(value * (graphicw + 8.0));
    }
     
    public double getWidth() {
        return r.getWidth() / (graphicw + 8.0);        
    }
    
    public void setBarFill(Paint value) {
        r.setFill(value);
    }
    
    public Paint getBarFill() {
        return r.getFill();
    }
}