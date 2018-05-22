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
package com.adr.helloiot;

import com.adr.helloiot.device.DeviceBasic;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatBase64;
import com.adr.helloiot.device.format.StringFormatDecimal;
import com.adr.helloiot.device.format.StringFormatHex;
import com.adr.helloiot.device.format.StringFormatIdentity;
import com.adr.helloiot.unit.EditAreaEvent;
import com.adr.helloiot.unit.EditAreaStatus;
import com.adr.helloiot.unit.EditAreaView;
import com.adr.helloiot.unit.EditEvent;
import com.adr.helloiot.unit.EditStatus;
import com.adr.helloiot.unit.EditView;
import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.util.ExternalFonts;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author adrian
 */
public class TopicInfoEdit implements TopicInfo {

    private final static String STYLEFORMAT = "{} {-fx-background-color: gray; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 70px; -fx-text-alignment: center;}";
    private final static String STYLEFORMATSPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 70px;}";
    private final static String STYLEQOS = "{} {-fx-background-color: darkblue; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 30px; -fx-text-alignment: center;}";
    private final static String STYLEQOSSPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 30px;}";

    private final String type;  
    private final TopicInfoEditNode editnode;

    private String topic = null;
    private SimpleStringProperty name = new SimpleStringProperty();
    private String topicpub = null;
    private String format = "STRING";
    private String jsonpath = null;
    private boolean multiline = false;
    private Color color = null;
    private Color background = null;
    private int qos = -1;
    private int retained = -1;

    public TopicInfoEdit(String type, TopicInfoEditNode editnode) {
        this.type = type;
        this.editnode = editnode;
    }
    
    @Override
    public String getType() {
        return type;
    }

    @Override
    public ReadOnlyProperty<String> getLabel() {
        return name;
    }
    
    @Override
    public Node getGraphic() {

        Text t = new Text();
        t.setFill(Color.WHITE);
        t.setFont(Font.font(ExternalFonts.SOURCESANSPRO_BOLD, FontWeight.BOLD, 10.0));
        TextFlow tf = new TextFlow(t);
        tf.setPrefWidth(55);
        tf.setTextAlignment(TextAlignment.CENTER);
        tf.setPadding(new Insets(2, 5, 2, 5));

        if ("Subscription".equals(getType())) {
            t.setText("SUB");
            tf.setStyle("-fx-background-color: #001A80; -fx-background-radius: 12px;");
        } else if ("Publication".equals(getType())) {
            t.setText("PUB");
            tf.setStyle("-fx-background-color: #4D001A; -fx-background-radius: 12px;");
        } else { // "Publication/Subscription"
            t.setText("P/SUB");
            tf.setStyle("-fx-background-color: #003300; -fx-background-radius: 12px;");
        }
        return tf;
    }
    
    @Override
    public void load(SubProperties properties) {
        topic = properties.getProperty(".topic", null);
        name.setValue(topic == null ? null : capitalize(leaf(topic)));
        topicpub = properties.getProperty(".topicpub", null);
        format = properties.getProperty(".format", "STRING");
        jsonpath = properties.getProperty(".jsonpath", null);
        multiline = Boolean.parseBoolean(properties.getProperty(".multiline", "false"));
        String c = properties.getProperty(".color", null);
        color = c == null ? null : Color.valueOf(c);
        c = properties.getProperty(".background", null);
        background = c == null ? null : Color.valueOf(c);    
        qos = Integer.parseInt(properties.getProperty(".qos", "-1"));
        retained = Integer.parseInt(properties.getProperty(".retained", "-1"));
   }
    
    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".type", getType());

        properties.setProperty(".topic", getTopic());
        properties.setProperty(".topicpub", getTopicpub());
        properties.setProperty(".format", getFormat());
        properties.setProperty(".jsonpath", getJsonpath());
        properties.setProperty(".multiline", Boolean.toString(isMultiline()));
        properties.setProperty(".color", getColor() == null ? null : getColor().toString());
        properties.setProperty(".background", getBackground() == null ? null : getBackground().toString());
        properties.setProperty(".qos", Integer.toString(getQos()));
        properties.setProperty(".retained", Integer.toString(getRetained()));        
    }
    
    @Override
    public TopicStatus getTopicStatus() throws HelloIoTException {
        
        if (topic == null || topic.isEmpty()) {
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
            String label = getLabel().getValue();
            throw new HelloIoTException(resources.getString("exception.topicinfoedit"));
        }
        
        if ("Subscription".equals(getType())) {
            return buildTopicSubscription();
        } else if ("Publication".equals(getType())) {
            return buildTopicPublish();
        } else { // "Publication/Subscription"
            return buildTopicPublishSubscription();
        }
    }

    @Override
    public TopicInfoNode getEditNode() {
        return editnode;
    }

    @Override
    public void writeToEditNode() {
        editnode.edittopic.setText(getTopic());
        editnode.edittopicpub.setText(getTopicpub());
        editnode.edittopicpub.setDisable("Subscription".equals(getType()));
        editnode.editformat.getSelectionModel().select(getFormat());
        editnode.editjsonpath.setText(getJsonpath());
        editnode.editjsonpath.setDisable("BASE64".equals(getFormat()) || "HEX".equals(getFormat()) || "SWITCH".equals(getFormat()));
        editnode.editmultiline.setSelected(isMultiline());
        editnode.editcolor.setValue(getColor());
        editnode.editbackground.setValue(getBackground());
        editnode.editqos.setValue(getQos());
        editnode.editretained.setValue(getRetained());     
    }

    @Override
    public void readFromEditNode() {
        topic = editnode.edittopic.getText();
        name.setValue(topic == null ? null : capitalize(leaf(topic)));
        if ("Subscription".equals(type)) {
            topicpub = null;
            editnode.edittopicpub.setDisable(true);
        } else {
            editnode.edittopicpub.setDisable(false);
            topicpub = editnode.edittopicpub.getText() == null || editnode.edittopicpub.getText().isEmpty() ? null : editnode.edittopicpub.getText();
        }
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
    
    public String getTopic() {
        return topic;
    }

    public String getTopicpub() {
        return topicpub;
    }

    public String getFormat() {
        return format;
    }

    public String getJsonpath() {
        return jsonpath;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public Color getColor() {
        return color;
    }

    public Color getBackground() {
        return background;
    }

    public int getQos() {
        return qos;
    }

    public int getRetained() {
        return retained;
    }

    private static String capitalize(String s) {
        final char[] buffer = s.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (ch == '_' || ch == ' ') {
                buffer[i] = ' ';
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }

    private static String leaf(String s) {
        int i = s.lastIndexOf('/');
        if (i < 0) {
            return s;
        } else if (i == s.length() - 1) {
            return leaf(s.substring(0, s.length() - 1));
        } else {
            return s.substring(i + 1);
        }
    }
    
    private TopicStatus buildTopicPublish() {

        TransmitterSimple d = new TransmitterSimple();
        d.setTopic(getTopic());
        d.setTopicPublish(getTopicpub());
        d.setQos(getQos());
        if (getRetained() >= 0) {
            d.setRetained(getRetained() != 0);
        }
        d.setFormat(createFormat());

        EditEvent u = isMultiline() ? new EditAreaEvent() : new EditEvent();
        u.setPrefWidth(320.0);
        u.setLabel(getLabel().getValue());
        u.setFooter(getTopic() + getQOSBadge(getQos()) + getFormatBadge(d.getFormat()));
        setStyle(u);
        u.setDevice(d);

        return new TopicStatus(Arrays.asList(d), Arrays.asList(u));
    }

    private TopicStatus buildTopicPublishSubscription() {

        DeviceSimple d = new DeviceSimple();
        d.setTopic(getTopic());
        d.setTopicPublish(getTopicpub());
        d.setQos(getQos());
        if (getRetained() >= 0) {
            d.setRetained(getRetained() != 0);
        }
        d.setFormat(createFormat());

        EditStatus u = isMultiline() ? new EditAreaStatus() : new EditStatus();
        u.setPrefWidth(320.0);
        u.setLabel(getLabel().getValue());
        u.setFooter(getTopic() + getQOSBadge(getQos()) + getFormatBadge(d.getFormat()));
        setStyle(u);
        u.setDevice(d);
        
        return new TopicStatus(Arrays.asList(d), Arrays.asList(u));
    }

    private TopicStatus buildTopicSubscription() {

        DeviceBasic d = new DeviceBasic();
        d.setTopic(getTopic());
        d.setTopicPublish(getTopicpub());
        d.setQos(getQos());
        if (getRetained() >= 0) {
            d.setRetained(getRetained() != 0);
        }
        d.setFormat(createFormat());

        EditView u = isMultiline() ? new EditAreaView() : new EditView();
        u.setPrefWidth(320.0);
        u.setLabel(getLabel().getValue());
        u.setFooter(getTopic() + getQOSBadge(getQos()) + getFormatBadge(d.getFormat()));
        setStyle(u);
        u.setDevice(d);

        return new TopicStatus(Arrays.asList(d), Arrays.asList(u));
    }
    
    private void setStyle(Unit u) {
        StringBuilder style = new StringBuilder();
        if (getColor() != null) {
            style.append("-fx-unit-fill: ").append(webColor(getColor())).append(";");
        }
        if (getBackground() != null) {
            style.append("-fx-background-color: ").append(webColor(getBackground())).append(";");
        }
        u.getNode().setStyle(style.toString());        
    }

    private String getFormatBadge(StringFormat f) {
        if (f instanceof StringFormatIdentity) {
            return STYLEFORMATSPACE;
        } else {
            return STYLEFORMAT + f.toString();
        }
    }

    private String getQOSBadge(int i) {
        if (i < 0) {
            return STYLEQOSSPACE;
        } else {
            return STYLEQOS + Integer.toString(i);
        }
    }

    private StringFormat createFormat() {
        if ("STRING".equals(getFormat())) {
            return new StringFormatIdentity(getJsonpath() == null || getJsonpath().isEmpty() ? null : getJsonpath());
        } else if ("INT".equals(getFormat())) {
            return new StringFormatDecimal(getJsonpath() == null || getJsonpath().isEmpty() ? null : getJsonpath(), "0");
        } else if ("BASE64".equals(getFormat())) {
            return new StringFormatBase64();
        } else if ("HEX".equals(getFormat())) {
            return new StringFormatHex();
        } else if ("DOUBLE".equals(getFormat())) {
            return new StringFormatDecimal(getJsonpath() == null || getJsonpath().isEmpty() ? null : getJsonpath(), "0.00");
        } else if ("DECIMAL".equals(getFormat())) {
            return new StringFormatDecimal(getJsonpath() == null || getJsonpath().isEmpty() ? null : getJsonpath(), "0.000");
        } else if ("DEGREES".equals(getFormat())) {
            return new StringFormatDecimal(getJsonpath() == null || getJsonpath().isEmpty() ? null : getJsonpath(), "0.0°");
        } else {
            return StringFormatIdentity.INSTANCE;
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
