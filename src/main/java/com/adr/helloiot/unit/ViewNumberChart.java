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

import com.adr.helloiotlib.unit.Units;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.device.DeviceNumber;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class ViewNumberChart extends Tile {

    private Label level;
    private Timeline timeline;
    private ViewChartSerie serie;
    private ObservableList<XYChart.Series<Number, Number>> areaChartData;
    private NumberAxis yAxis;

    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);
    private Duration duration = Duration.minutes(1.0);

    @Override
    protected Node constructContent() {
        VBox vboxroot = new VBox();
        vboxroot.setSpacing(10.0);        
        
        level = new Label();
        level.setAlignment(Pos.CENTER_RIGHT);
        level.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        level.getStyleClass().add("unitmaintext");
        
        
        // Get all data

        areaChartData = FXCollections.observableArrayList();       
        
        AreaChart chart;
        NumberAxis xAxis;
    
        xAxis = new NumberAxis(1.0, ViewChartSerie.SIZE, 0.0);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);     
        xAxis.setTickLabelsVisible(false);
        yAxis = new NumberAxis(0,0,0);
        yAxis.setSide(Side.RIGHT);
        yAxis.setMinorTickVisible(false);        
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        
        chart = new AreaChart<>(xAxis, yAxis, areaChartData);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setMinSize(40.0, 50.0);
        chart.setPrefSize(40.0, 50.0);
        chart.setPadding(Insets.EMPTY);
        
        StackPane stack = new StackPane(chart);
        VBox.setVgrow(stack, Priority.SOMETIMES);   
        stack.setPadding(new Insets(0.0, 0.0, 0.0, 3.0));
        vboxroot.getChildren().addAll(level, stack);
        
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
        
        device.subscribeStatus(messageHandler);
        
        serie = new ViewChartSerie();
        serie.setDevice(device);
        serie.construct();
        areaChartData.add(serie.createSerie());  
        yAxis.setLowerBound(device.getLevelMin()- 5.0);
        yAxis.setUpperBound(device.getLevelMax() + 5.0);
        yAxis.setTickUnit((device.getLevelMax() - device.getLevelMin()) / 10.0);      
        
        timeline = new Timeline(new KeyFrame(duration.divide(ViewChartSerie.SIZE), ae -> {
            serie.tick();
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
        serie.destroy();  
        serie = null;
        areaChartData.clear();
        
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
}
