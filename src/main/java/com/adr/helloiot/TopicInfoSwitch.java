//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiot.unit.ButtonSimple;
import com.adr.helloiot.unit.UnitPage;
import java.util.Arrays;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author adrian
 */
public class TopicInfoSwitch implements TopicInfo {

    private final TopicInfoSwitchNode editnode;

    private final SimpleStringProperty name = new SimpleStringProperty();
    private String page = null;
    private String topic = null;
    private String topicpub = null;
    private String icon = "TOGGLE";
    private Color color;

    public TopicInfoSwitch(TopicInfoSwitchNode editnode) {
        this.editnode = editnode;
    }
    
    @Override
    public String getType() {
        return "Switch";
    }

    @Override
    public ReadOnlyProperty<String> getLabel() {
        return name;
    }
    
    @Override
    public Node getGraphic() {

        Text t = IconBuilder.create(FontAwesome.FA_TOGGLE_ON, 12.0).build();
        t.setFill(Color.WHITE);
        TextFlow tf = new TextFlow(t);
        tf.setTextAlignment(TextAlignment.CENTER);
        tf.setPadding(new Insets(5, 8, 5, 8));
        tf.setStyle("-fx-background-color: #4559d4; -fx-background-radius: 5px;");
        tf.setPrefWidth(30.0);
        return tf;   
    }
    
    @Override
    public void load(SubProperties properties) {
        name.setValue(properties.getProperty(".name"));
        page = properties.getProperty(".page", null);
        topic = properties.getProperty(".topic", null);
        topicpub = properties.getProperty(".topicpub", null);
        icon = properties.getProperty(".icon", "TOGGLE");
        String c = properties.getProperty(".color", null);
        color = c == null ? null : Color.valueOf(c);
    }
        
    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".type", getType());

        properties.setProperty(".name", name.getValue());
        properties.setProperty(".page", page);
        properties.setProperty(".topic", topic);
        properties.setProperty(".topicpub", topicpub);
        properties.setProperty(".icon", icon);
        properties.setProperty(".color", color == null ? null : color.toString());
    }
    
    @Override
    public DevicesUnits getDevicesUnits() throws HelloIoTException {
        DeviceSwitch l = new DeviceSwitch();
        l.setTopic(topic);
        l.setTopicPublish(topicpub);
        MQTTProperty.setQos(l, 0);
        MQTTProperty.setRetained(l, false);

        ButtonSimple s = new ButtonSimple();
        s.setText(getLabel().getValue());
        s.setDevice(l);
        s.setIconStatus(IconStatus.valueOf(icon));
        Color c = color;
        if (c != null) {
            s.setStyle("-fx-base:" + webColor(c) + "; -fx-button-fill:" + webColor(c) + ";");
        }
        UnitPage.setPage(s, page);
        return new DevicesUnits(Arrays.asList(l), Arrays.asList(s));
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
        editnode.editicon.setValue(icon);
        editnode.editcolor.setValue(color);
    }

    @Override
    public void readFromEditNode() {
        name.setValue(editnode.editname.getText());
        page = editnode.editpage.getEditor().getText();
        topic = editnode.edittopic.getText();
        topicpub = editnode.edittopicpub.getText() == null || editnode.edittopicpub.getText().isEmpty() ? null : editnode.edittopicpub.getText();
        icon = editnode.editicon.getValue();
        color = editnode.editcolor.getValue();
    }  
    
    private String webColor(Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }      
}
