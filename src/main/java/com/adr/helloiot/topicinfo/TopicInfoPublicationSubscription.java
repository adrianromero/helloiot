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
package com.adr.helloiot.topicinfo;

import com.adr.fonticon.IconFontGlyph;
import com.adr.helloiot.SubProperties;
import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatBase64;
import com.adr.helloiotlib.format.StringFormatDecimal;
import com.adr.helloiotlib.format.StringFormatHex;
import com.adr.helloiotlib.format.StringFormatIdentity;
import com.adr.helloiotlib.unit.Unit;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public abstract class TopicInfoPublicationSubscription implements TopicInfo {

    private final static String STYLEQOS = "{} {-fx-background-color: darkblue; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 30px; -fx-text-alignment: center;}";
    private final static String STYLEQOSSPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 30px;}";

    private final TopicInfoFactory factory;
    private final TopicInfoEditNode editnode;

    protected String page = null;
    protected String topic = null;
    private final SimpleStringProperty name = new SimpleStringProperty();
    protected String topicpub = null;
    protected String format = "STRING";
    protected String jsonpath = null;
    protected boolean multiline = false;
    protected Color color = null;
    protected Color background = null;
    protected int qos = 0;
    protected boolean retained = false;

    public TopicInfoPublicationSubscription(TopicInfoFactory factory, TopicInfoEditNode editnode) {
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
    public void load(SubProperties properties) {
        name.setValue(properties.getProperty(".name"));
        page = properties.getProperty(".page", null);
        topic = properties.getProperty(".topic", null);
        topicpub = properties.getProperty(".topicpub", null);
        format = properties.getProperty(".format", "STRING");
        jsonpath = properties.getProperty(".jsonpath", null);
        multiline = Boolean.parseBoolean(properties.getProperty(".multiline", "false"));
        String c = properties.getProperty(".color", null);
        color = c == null ? null : Color.valueOf(c);
        c = properties.getProperty(".background", null);
        background = c == null ? null : Color.valueOf(c);    
        qos = Integer.parseInt(properties.getProperty(".qos", ""));
        retained = Boolean.parseBoolean(properties.getProperty(".retained", "false"));
   }
    
    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".name", name.getValue());
        properties.setProperty(".page", page);
        properties.setProperty(".topic", topic);
        properties.setProperty(".topicpub", topicpub);
        properties.setProperty(".format", format);
        properties.setProperty(".jsonpath", jsonpath);
        properties.setProperty(".multiline", Boolean.toString(multiline));
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
        editnode.edittopicpub.setText(topicpub);
        editnode.editformat.getSelectionModel().select(format);
        editnode.editjsonpath.setText(jsonpath);
        editnode.editjsonpath.setDisable("BASE64".equals(format) || "HEX".equals(format) || "SWITCH".equals(format));
        editnode.editmultiline.setSelected(multiline);
        editnode.editcolor.setValue(color);
        editnode.editbackground.setValue(background);
        editnode.editqos.setValue(qos);
        editnode.editretained.setValue(retained);     
    }

    @Override
    public void readFromEditNode() {
        name.setValue(editnode.editname.getText());
        topic = editnode.edittopic.getText();
        topicpub = editnode.edittopicpub.getText() == null || editnode.edittopicpub.getText().isEmpty() ? null : editnode.edittopicpub.getText();
        format = editnode.editformat.getValue();
        if ("BASE64".equals(format) || "HEX".equals(format) || "SWITCH".equals(format)) {
            jsonpath = null;
            editnode.editjsonpath.setDisable(true);
        } else {
            jsonpath = editnode.editjsonpath.getText();
            editnode.editjsonpath.setDisable(false);
        }
        multiline = editnode.editmultiline.isSelected();
        color = editnode.editcolor.getValue();
        background = editnode.editbackground.getValue();
        qos = editnode.editqos.getValue();
        retained = editnode.editretained.getValue();  
    }  
    
    protected void setStyle(Unit u) {
        StringBuilder style = new StringBuilder();
        if (color != null) {
            style.append("-fx-level-fill: ").append(webColor(color)).append(";");
        }
        if (background != null) {
            style.append("-fx-background-unit: ").append(webColor(background)).append(";");
        }
        u.getNode().setStyle(style.toString());        
    }

    protected String getQOSBadge(int i) {
        if (i == 0) {
            return STYLEQOSSPACE;
        } else {
            return STYLEQOS + Integer.toString(i);
        }
    }

    protected StringFormat createFormat() {
        if ("STRING".equals(format)) {
            return new StringFormatIdentity(jsonpath == null || jsonpath.isEmpty() ? null : jsonpath);
        } else if ("INT".equals(format)) {
            return new StringFormatDecimal(jsonpath == null || jsonpath.isEmpty() ? null : jsonpath, "0");
        } else if ("BASE64".equals(format)) {
            return new StringFormatBase64();
        } else if ("HEX".equals(format)) {
            return new StringFormatHex();
        } else if ("DOUBLE".equals(format)) {
            return new StringFormatDecimal(jsonpath == null || jsonpath.isEmpty() ? null : jsonpath, "0.00");
        } else if ("DECIMAL".equals(format)) {
            return new StringFormatDecimal(jsonpath == null || jsonpath.isEmpty() ? null : jsonpath, "0.000");
        } else if ("DEGREES".equals(format)) {
            return new StringFormatDecimal(jsonpath == null || jsonpath.isEmpty() ? null : jsonpath, "0.0°");
        } else {
            return StringFormatIdentity.INSTANCE;
        }
    }    

    protected IconFontGlyph createGlyph() {
        if ("STRING".equals(format)) {
            return null;
        } else if ("INT".equals(format)) {
            return IconFontGlyph.FA_SOLID_HASHTAG;
        } else if ("BASE64".equals(format)) {
            return IconFontGlyph.FA_SOLID_CODE;
        } else if ("HEX".equals(format)) {
            return IconFontGlyph.FA_SOLID_MICROCHIP;
        } else if ("DOUBLE".equals(format)) {
            return IconFontGlyph.FA_SOLID_SLIDERS_H;
        } else if ("DECIMAL".equals(format)) {
            return IconFontGlyph.FA_SOLID_RULER;
        } else if ("DEGREES".equals(format)) {
            return IconFontGlyph.FA_SOLID_THERMOMETER_QUARTER;
        } else {
            return null;
        }
    }    
    
    protected String webColor(Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }    
}
