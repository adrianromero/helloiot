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
package com.adr.helloiot.unit;

import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceNumber;
import com.adr.helloiot.device.StatusNumber;
import com.google.common.eventbus.Subscribe;
import eu.hansolo.medusa.Gauge;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author adrian
 */
public class ViewGauge extends Tile implements Unit {

    private static final StyleablePropertyFactory<ViewGauge> FACTORY = new StyleablePropertyFactory<>(Tile.getClassCssMetaData());
    private static final CssMetaData<ViewGauge, Color> VALUECOLOR = FACTORY.createColorCssMetaData("-fx-value-color", s -> s.valueColor, Color.BLACK, false);

    private DeviceNumber device = null;

    private StackPane gaugecontainer;
    private Gauge gauge = null;
    private GaugeType type = GaugeType.DASHBOARD;

    public ObjectProperty<Color> valueColorProperty() {
        return valueColor;
    }
    public final Color getValueColor() {
        return valueColor.getValue();
    }
    public final void setSelected(Color color) {
        valueColor.setValue(color);
    }
    private final StyleableObjectProperty<Color> valueColor = new SimpleStyleableObjectProperty<>(VALUECOLOR, this, "value-color");

    @Override
    protected Node constructContent() {
        gaugecontainer = new StackPane();
        return gaugecontainer;
    }

    @Subscribe
    public void receivedStatus(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));
    }

    private void updateStatus(byte[] status) {

        if (gauge == null) {
            return;
        }

        double newvalue = StatusNumber.getFromBytes(status);
        if (newvalue < gauge.getMinValue()) {
            newvalue = gauge.getMinValue();
        }
        if (newvalue > gauge.getMaxValue()) {
            newvalue = gauge.getMaxValue();
        }
        gauge.setValue(newvalue);
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        Unit.super.construct(app);
        device.subscribeStatus(this);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        Unit.super.destroy();
        device.unsubscribeStatus(this);
    }

    public void setDevice(DeviceNumber device) {
        this.device = device;
        rebuildGauge();
    }

    public DeviceNumber getDevice() {
        return device;
    }

    public void setType(GaugeType type) {
        this.type = type == null ? GaugeType.DASHBOARD : type;
        rebuildGauge();
    }

    public GaugeType getType() {
        return type;
    }

    private void rebuildGauge() {

        // Device not null
        if (gauge != null) {
            gaugecontainer.getChildren().remove(gauge);
            gauge = null;
        }

        if (device == null) {
            return;
        }

        gauge = type.build(device.getLevelMin(), device.getLevelMax());        
        gauge.titleColorProperty().bind(valueColor);
        gauge.valueColorProperty().bind(valueColor);
        gauge.unitColorProperty().bind(valueColor);
        gauge.setUnit(device.getUnit());
        gaugecontainer.getChildren().add(gauge);

        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}
