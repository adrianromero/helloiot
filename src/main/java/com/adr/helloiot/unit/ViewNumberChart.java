//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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

import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import com.adr.helloiotlib.unit.Units;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.device.DeviceNumber;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class ViewNumberChart extends Tile {

    private HBox boxview;
    private Label level;
    private Timeline timeline;
    
    private ChartSerie serie;
    private ChartNode areachart;
    
    private IconFontGlyph glyph = null;
    private StackPane glyphnode = null;    

    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);
    private Duration duration = Duration.minutes(1.0);

    @Override
    protected Node constructContent() {
        VBox vboxroot = new VBox();
        vboxroot.setSpacing(10.0);   
        
        boxview = new HBox();
        boxview.setSpacing(6.0);
        
        level = new Label();
        level.setAlignment(Pos.CENTER_RIGHT);
        level.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        level.getStyleClass().add("unitmaintext");
        HBox.setHgrow(level, Priority.SOMETIMES);
        
        boxview.getChildren().add(level);
        
        areachart = new ChartNode();
        StackPane chart = areachart.getNode();
        chart.setMinSize(40.0, 50.0);
        chart.setPrefSize(40.0, 50.0);
        chart.setPadding(Insets.EMPTY);
        
        StackPane stack = new StackPane(chart);
        VBox.setVgrow(stack, Priority.SOMETIMES);   
        stack.setPadding(new Insets(0.0, 0.0, 0.0, 3.0));
        vboxroot.getChildren().addAll(boxview, stack);
        
        initialize();
        return vboxroot;
    }

    public void initialize() {
        level.setText(null);
    }

    private void updateStatus(byte[] newstatus) {
        level.setText(device.getFormat().format(device.getFormat().value(newstatus)));
    }

    @Override
    public void construct(IoTApp app) {
        super.construct(app);
        
        if (glyph != null) {
            glyphnode = new StackPane(IconBuilder.create(glyph, 36.0).color(Color.web("#565656")).build());
            glyphnode.setPadding(new Insets(0, 0, 0, 6));
            boxview.getChildren().add(0, glyphnode);
        }        
        
        device.subscribeStatus(messageHandler);

        serie = new ChartSerie();
        serie.setDevice(device);
        serie.construct();
        areachart.addShapeChart(new ShapeChartArea(serie));
               
        timeline = new Timeline(new KeyFrame(duration.divide(ChartSerie.SIZE), ae -> {
            serie.tick();
        }));  
        timeline.setCycleCount(Animation.INDEFINITE);        
        timeline.play();   
        // Do not update status all values come from messages
    }

    @Override
    public void destroy() {
        super.destroy();
        
        if (glyphnode != null) {
            boxview.getChildren().remove(glyphnode);
            glyphnode = null;
        }        
        
        timeline.stop();
        timeline = null;
        
        areachart.removeAllShapeChart();
        serie.setListener(null);
        serie.destroy();  
        serie = null;
        
        device.unsubscribeStatus(messageHandler);
    }

    public void setDevice(DeviceNumber device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
    }

    public DeviceNumber getDevice() {
        return device;
    }
    
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    public void setGlyph(IconFontGlyph glyph) {
        this.glyph = glyph;
    }
    
    public IconFontGlyph getGlyph() {
        return glyph;
    }    
}
