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
package com.adr.helloiot.topicinfo;

import com.adr.helloiot.DevicesUnits;
import com.adr.helloiot.HelloIoTException;
import com.adr.helloiot.SubProperties;
import com.adr.helloiot.device.DeviceNumber;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiot.unit.GaugeType;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiot.unit.ViewGauge;
import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.unit.Unit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class TopicInfoGauge implements TopicInfo {

    private final static String STYLEQOS = "{} {-fx-background-color: darkblue; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 30px; -fx-text-alignment: center;}";
    private final static String STYLEQOSSPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 30px;}";

    private final TopicInfoFactory factory;
    private final TopicInfoGaugeNode editnode;

    protected String page = null;
    protected String topic = null;
    private final SimpleStringProperty name = new SimpleStringProperty();
    protected GaugeNodeFormat format = GaugeNodeFormat.LONG;
    protected String jsonpath = null;
    protected GaugeType gaugetype = GaugeType.SIMPLE_SECTION;
    protected String min = "0";
    protected String max = "100";
    protected Color color = null;
    protected Color barcolor = null;
    protected Color background = null;
    protected int qos = 0;
    protected boolean retained = false;

    public TopicInfoGauge(TopicInfoFactory factory, TopicInfoGaugeNode editnode) {
        this.factory = factory;
        this.editnode = editnode;
    }
    
    @Override
    public TopicInfoFactory getFactory() {
        return factory;
    }
    
    @Override
    public ReadOnlyProperty<String> getLabel() {
        return name;
    }
    
    @Override
    public DevicesUnits getDevicesUnits() throws HelloIoTException {
        
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
        
        if (topic == null || topic.isEmpty()) {           
            throw new HelloIoTException(resources.getString("exception.topicinfoedit"));
        }
        
        NumberFormat nf = NumberFormat.getInstance();
        double levelmin;
        try {
            levelmin = nf.parse(min).doubleValue();
        } catch (ParseException ex) {
            throw new HelloIoTException(resources.getString("exception.minnumber"));  
        }
        double levelmax;
        try {
            levelmax = nf.parse(max).doubleValue();
        } catch (ParseException ex) {
            throw new HelloIoTException(resources.getString("exception.maxnumber"));
        }
        
        DeviceNumber d = new DeviceNumber();
        d.setTopic(topic);
        MQTTProperty.setQos(d, qos);
        MQTTProperty.setRetained(d, retained);
        d.setFormat(createFormat());
        d.setLevelMin(levelmin);
        d.setLevelMax(levelmax);

        ViewGauge u = new ViewGauge();
        u.setType(gaugetype);
        u.setUnit(format.getUnit());
        u.setDecimals(format.getDecimals());
        u.setPrefWidth(320.0);
        u.setLabel(getLabel().getValue());
        u.setFooter(topic + getQOSBadge(qos));
        if (barcolor != null) {
            u.setBarColor(barcolor);
        }
        setStyle(u);
        u.setDevice(d);
        UnitPage.setPage(u, page);
        
        return new DevicesUnits(Arrays.asList(d), Arrays.asList(u));
    }    
    
    @Override
    public void load(SubProperties properties) {
        name.setValue(properties.getProperty(".name"));
        page = properties.getProperty(".page", null);
        topic = properties.getProperty(".topic", null);
        jsonpath = properties.getProperty(".jsonpath", null);
        format = GaugeNodeFormat.valueOf(properties.getProperty(".format", GaugeNodeFormat.LONG.name()));
        gaugetype = GaugeType.valueOf(properties.getProperty(".gaugetype", GaugeType.BASIC.name()));
        min = properties.getProperty(".min", "0");
        max = properties.getProperty(".max", "100");
        String bc = properties.getProperty(".barcolor", null);
        barcolor = bc == null ? null : Color.valueOf(bc);
        String c = properties.getProperty(".color", null);
        color = c == null ? null : Color.valueOf(c);
        c = properties.getProperty(".background", null);
        background = c == null ? null : Color.valueOf(c);    
        qos = Integer.parseInt(properties.getProperty(".qos", "0"));
        retained = Boolean.parseBoolean(properties.getProperty(".retained", "false"));
   }
    
    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".name", name.getValue());
        properties.setProperty(".page", page);
        properties.setProperty(".topic", topic);
        properties.setProperty(".jsonpath", jsonpath);
        properties.setProperty(".format", format.name());
        properties.setProperty(".gaugetype", gaugetype.name());
        properties.setProperty(".min", min);
        properties.setProperty(".max", max);
        properties.setProperty(".barcolor", barcolor == null ? null : barcolor.toString());
        properties.setProperty(".color", color == null ? null : color.toString());
        properties.setProperty(".background", background == null ? null : background.toString());
        properties.setProperty(".qos", Integer.toString(qos));
        properties.setProperty(".retained", Boolean.toString(retained));        
    }

    @Override
    public TopicInfoNode getEditNode() {
        return editnode;
    }

    @Override
    public void writeToEditNode() {
        editnode.editname.setText(name.getValue());
        editnode.editpage.setValue(page);
        editnode.edittopic.setText(topic);
        editnode.editjsonpath.setText(jsonpath);
        editnode.editformat.getSelectionModel().select(format);
        editnode.editgaugetype.getSelectionModel().select(gaugetype);
        editnode.editmin.setText(min);
        editnode.editmax.setText(max);
        editnode.editbarcolor.setValue(barcolor);
        editnode.editcolor.setValue(color);
        editnode.editbackground.setValue(background);
        editnode.editqos.setValue(qos);
        editnode.editretained.setValue(retained);     
    }

    @Override
    public void readFromEditNode() {
        name.setValue(editnode.editname.getText());
        topic = editnode.edittopic.getText();
        format = editnode.editformat.getValue();
        jsonpath = editnode.editjsonpath.getText();
        gaugetype = editnode.editgaugetype.getValue();
        min = editnode.editmin.getText();
        max = editnode.editmax.getText();
        barcolor = editnode.editbarcolor.getValue();
        color = editnode.editcolor.getValue();
        background = editnode.editbackground.getValue();
        qos = editnode.editqos.getValue();
        retained = editnode.editretained.getValue();  
    }  
    
    private void setStyle(Unit u) {
        StringBuilder style = new StringBuilder();
        if (color != null) {
            style.append("-fx-value-color: ").append(webColor(color)).append(";");
        }
        if (background != null) {
            style.append("-fx-background-unit: ").append(webColor(background)).append(";");
        }
        u.getNode().setStyle(style.toString());        
    }
    
    private StringFormat createFormat() {
        return format.createFormat(jsonpath == null || jsonpath.isEmpty() ? null : jsonpath);
    }   
    
    private String getQOSBadge(int i) {
        if (i == 0) {
            return STYLEQOSSPACE;
        } else {
            return STYLEQOS + Integer.toString(i);
        }
    } 
    
    private String webColor(Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }        
}
