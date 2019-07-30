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

import com.adr.helloiotlib.unit.Units;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.device.DeviceNumber;
import eu.hansolo.medusa.Gauge;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ViewGauge extends Tile {

    private static final StyleablePropertyFactory<ViewGauge> FACTORY = new StyleablePropertyFactory<>(Tile.getClassCssMetaData());
    private static final CssMetaData<ViewGauge, Color> VALUECOLOR = FACTORY.createColorCssMetaData("-fx-value-color", s -> s.valueColor, Color.BLACK, false);
    
    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);

    private StackPane gaugecontainer;
    private Gauge gauge = null;
    private GaugeType type = GaugeType.DASHBOARD;
    
    private final StyleableObjectProperty<Color> valueColor = new SimpleStyleableObjectProperty<>(VALUECOLOR, this, "-fx-value-color");
    private Color barColor = Color.web("#29b1ff"); // blue cyan
    private int decimals = 2;
    private String title = "";
    private String subtitle = "";
    private String unit = "";

    public ViewGauge() {
         getStyleClass().add("unitgauge");
    }

    public ObjectProperty<Color> valueColorProperty() {
        return valueColor;
    }

    public final Color getValueColor() {
        return valueColor.getValue();
    }

    public final void setValueColor(Color color) {
        valueColor.setValue(color);
    }
    
    public final Color getBarColor() {
        return barColor;
    }

    public final void setBarColor(Color color) {
        barColor = color;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    protected Node constructContent() {
        gaugecontainer = new StackPane();
        return gaugecontainer;
    }

    private void updateStatus(byte[] status) {

        if (gauge == null) {
            return;
        }

        double newvalue = device.getFormat().value(status).asDouble();
        if (newvalue < gauge.getMinValue()) {
            newvalue = gauge.getMinValue();
    }
        if (newvalue > gauge.getMaxValue()) {
            newvalue = gauge.getMaxValue();
        }
        gauge.setValue(newvalue);
    }

    @Override
    public void construct(IoTApp app) {
        super.construct(app);
        gauge = type.build(device.getLevelMin(), device.getLevelMax(), barColor);
        
        gauge.titleColorProperty().bind(valueColor);
        gauge.subTitleColorProperty().bind(valueColor);
        gauge.unitColorProperty().bind(valueColor);
        gauge.valueColorProperty().bind(valueColor);
        gauge.setTickLabelColor(Color.DARKGRAY);
         
        gauge.setFocusTraversable(false);
        
        gauge.setDecimals(decimals);
        gauge.setTitle(title);
        gauge.setSubTitle(subtitle);
        gauge.setUnit(unit);        
        
        gaugecontainer.getChildren().add(gauge);

        device.subscribeStatus(messageHandler);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        super.destroy();
        
        device.unsubscribeStatus(messageHandler);
                
        gaugecontainer.getChildren().remove(gauge);
        gauge = null;
    }

    public void setDevice(DeviceNumber device) {
        this.device = device;
    }

    public DeviceNumber getDevice() {
        return device;
    }

    public void setType(GaugeType type) {
        this.type = type == null ? GaugeType.DASHBOARD : type;
    }

    public GaugeType getType() {
        return type;
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}
