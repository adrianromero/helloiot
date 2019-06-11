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
import com.adr.helloiotlib.app.IoTApp;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

@DefaultProperty("series")
public class ViewChart extends Tile {

    private ChartNode areachart;
    private BorderPane chartcontainer; 
    private VBox legendcontainer;
    private boolean legendVisible = true;
    private Side legendSide  = Side.RIGHT;
    private Timeline timeline;
    private final List<ChartSerie> series = new ArrayList<>();
    private Duration duration = Duration.minutes(1.0);

    @Override
    protected Node constructContent() {
        areachart = new ChartNode();
        StackPane chart = areachart.getNode();
        chart.setMinSize(40.0, 240.0);
        chart.setPrefSize(40.0, 240.0);
        chart.setPadding(Insets.EMPTY);
        
        legendcontainer = new VBox();
        legendcontainer.getStyleClass().add("unitchartlegend");
        
        chartcontainer = new BorderPane(chart);
        return chartcontainer;
    }

    @Override
    public void construct(IoTApp app) {
        super.construct(app);

        for (ChartSerie serie: series) {
            serie.construct();
            areachart.addShapeChart(new ShapeChartArea(serie));
            
            Label legend = new Label(serie.getLabel());
            legend.getStyleClass().addAll("chartlegend", serie.getStyleClass() + "-legend");
            legend.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_CIRCLE, 14.0)
                    .styleClass(serie.getStyleClass() + "-line")
                    .styleClass(serie.getStyleClass() + "-fill")
                    .build());
            legendcontainer.getChildren().add(legend);
        }
        
        if (legendVisible) {
            if (legendSide == Side.RIGHT) {
                chartcontainer.setRight(legendcontainer);
            } else if (legendSide == Side.BOTTOM) {
                chartcontainer.setBottom(legendcontainer);
            } else if (legendSide == Side.LEFT) {
                chartcontainer.setLeft(legendcontainer);
            } else if (legendSide == Side.TOP) {
                chartcontainer.setTop(legendcontainer);
            } else {
                chartcontainer.setRight(legendcontainer);
            }       
        }

        timeline = new Timeline(new KeyFrame(duration.divide(ChartSerie.SIZE), ae -> {
            for (ChartSerie serie: series) {
                serie.tick();
            }    
        }));  
        
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        // Do not update status all values come from messages
    }

    @Override
    public void destroy() {
        super.destroy();
        
        timeline.stop();
        timeline = null;
        
        for (ChartSerie serie: series) {
            serie.setListener(null);
            serie.destroy();   
        }       
        legendcontainer.getChildren().clear();
        chartcontainer.getChildren().remove(legendcontainer); 
        areachart.removeAllShapeChart();
    }

    public List<ChartSerie> getSeries() {
        return series;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public boolean isLegendVisible() {
        return legendVisible;
    }

    public void setLegendVisible(boolean legendVisible) {
        this.legendVisible = legendVisible;
    }

    public Side getLegendSide() {
        return legendSide;
    }

    public void setLegendSide(Side legendSide) {
        this.legendSide = legendSide;
    }
}
