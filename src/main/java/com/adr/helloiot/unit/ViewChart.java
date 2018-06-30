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
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
@DefaultProperty("series")
public class ViewChart extends Tile {

    private StackPane chartcontainer; 
    private boolean legendVisible = true;
    private Side legendSide  = Side.RIGHT;
    private Timeline timeline;
    private final List<ViewChartSerie> series = new ArrayList<>();
    private Duration duration = Duration.minutes(1.0);

    @Override
    protected Node constructContent() {
        chartcontainer = new StackPane();
        return chartcontainer;
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        super.construct(app);
        
        // Get all data
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double inc = 0.0;
        List<XYChart.Series<Number, Number>> chartdata = new ArrayList<>();
        for (ViewChartSerie serie: series) {
            serie.construct();
            min = Math.min(min, serie.getDevice().getLevelMin() - 5.0);
            max = Math.max(max, serie.getDevice().getLevelMax() + 5.0);
            inc = Math.max(inc, serie.getDevice().getIncrement());
            chartdata.add(serie.createSerie());
        }
        ObservableList<XYChart.Series<Number, Number>> areaChartData = FXCollections.observableArrayList(chartdata);       
        
        AreaChart chart;
        NumberAxis xAxis;
        NumberAxis yAxis;
    
        xAxis = new NumberAxis(1.0, ViewChartSerie.SIZE, 0.0);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);     
        xAxis.setTickLabelsVisible(false);
        yAxis = new NumberAxis(min, max, inc * 10.0);
        yAxis.setSide(Side.RIGHT);
        yAxis.setMinorTickVisible(false);        
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        
        chart = new AreaChart<>(xAxis, yAxis, areaChartData);
        chart.setLegendVisible(legendVisible);
        chart.setLegendSide(legendSide);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setMinSize(40.0, 240.0);
        chart.setPrefSize(40.0, 240.0);
        chart.setPadding(Insets.EMPTY);

        StackPane stack = new StackPane(chart);
        stack.setPadding(new Insets(0.0, 0.0, 0.0, 3.0));
        chartcontainer.getChildren().add(stack); 
        
        timeline = new Timeline(new KeyFrame(duration.divide(ViewChartSerie.SIZE), ae -> {
            for (ViewChartSerie serie: series) {
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
        for (ViewChartSerie serie: series) {
            serie.destroy();
        }       
        chartcontainer.getChildren().clear();
    }

    public List<ViewChartSerie> getSeries() {
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
