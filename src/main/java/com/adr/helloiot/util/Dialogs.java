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
package com.adr.helloiot.util;

import com.adr.hellocommon.dialog.DialogView;
import javafx.animation.RotateTransition;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

public class Dialogs {
    
    private Dialogs() {}

    public static DialogView createLoading() {
        DialogView dialog = new DialogView();
        dialog.setCSS("/com/adr/helloiot/styles/loading.css");
        dialog.setMaster(true);
        dialog.setAnimate(false);
        dialog.setContent(createLoadingNode ());
        return dialog;      
    }
    
    private static Node createLoadingNode () {
        
        Circle c0 = new Circle(65);
        c0.setFill(Color.TRANSPARENT);
        c0.setStrokeWidth(0.0);

        Circle c1 = new Circle(50);
        c1.setFill(Color.TRANSPARENT);
        c1.setStrokeType(StrokeType.INSIDE);
        c1.setStrokeLineCap(StrokeLineCap.BUTT);
        c1.getStrokeDashArray().addAll(78.54); // 50 * 2 * 3.1416 / 4
        c1.setCache(true);
        c1.setCacheHint(CacheHint.ROTATE);   
        c1.getStyleClass().add("loading-circle");
        setRotate(c1, true, 440.0, 10);

        Circle c2 = new Circle(40);
        c2.setFill(Color.TRANSPARENT);
        c2.setStrokeType(StrokeType.INSIDE);
        c2.setStrokeLineCap(StrokeLineCap.BUTT);
        c2.getStrokeDashArray().addAll(41.89); // 40 * 2 * 3.1416 / 6
        c2.setCache(true);
        c2.setCacheHint(CacheHint.ROTATE);    
        c2.getStyleClass().add("loading-circle");
        setRotate(c2, true, 360.0, 14);

        Circle c3 = new Circle(30);
        c3.setFill(Color.TRANSPARENT);
        c3.setStrokeType(StrokeType.INSIDE);
        c3.setStrokeLineCap(StrokeLineCap.BUTT);        
        c3.getStyleClass().add("loading-circle");

        return new Group(c0, c1, c2, c3);
    }
    
    private static void setRotate(Shape s, boolean reverse, double angle, int duration) {
        RotateTransition r = new RotateTransition(Duration.seconds(duration), s);
        r.setAutoReverse(reverse);
        r.setDelay(Duration.ZERO);
        r.setRate(3.0);
        r.setCycleCount(RotateTransition.INDEFINITE);
        r.setByAngle(angle);
        r.play();
    }
}
