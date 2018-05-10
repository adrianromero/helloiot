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

import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.unit.ButtonSimple;
import com.adr.helloiot.util.ExternalFonts;
import java.util.Arrays;
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
public class TopicInfoSwitch implements TopicInfo {

    private final TopicInfoSwitchNode editnode;

    private String name = null;
    private String topic = null;
    private String topicpub = null;
    private String icon = null;
    private Color color;

    public TopicInfoSwitch(TopicInfoSwitchNode editnode) {
        this.editnode = editnode;
    }
    
    @Override
    public String getType() {
        return "Switch";
    }

    @Override
    public String getLabel() {
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

        t.setText("SWITCH");
        tf.setStyle("-fx-background-color: #001A80; -fx-background-radius: 12px;");
        return tf;
    }
    
    @Override
    public void load(SubProperties properties) {
        name = properties.getProperty(".name");
        topic = properties.getProperty(".topic", null);
        topicpub = properties.getProperty(".topicpub", null);
        icon = properties.getProperty(".icon");
        String c = properties.getProperty(".color", null);
        color = c == null ? null : Color.valueOf(c);
    }
        
    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".type", getType());

        properties.setProperty(".name", getName());
        properties.setProperty(".topic", getTopic());
        properties.setProperty(".topicpub", getTopicpub());
        properties.setProperty(".icon", getIcon());
        properties.setProperty(".color", getColor() == null ? null : getColor().toString());

    }
    
    @Override
    public TopicStatus getTopicStatus() throws HelloIoTException {
        DeviceSwitch l = new DeviceSwitch();
        l.setTopic(getTopic());
        l.setTopicPublish(getTopicpub());
        l.setQos(1);
        l.setRetained(false);

        ButtonSimple s = new ButtonSimple();
        s.setText(getLabel());
        s.setDevice(l);
        s.setIconStatus(IconStatus.valueOf((getIcon())));
        Color c = getColor();
        if (c != null) {
            s.setStyle("-fx-base:" + webColor(c) + "; -fx-button-fill:" + webColor(c) + ";");
        }
        return new TopicStatus(Arrays.asList(l), Arrays.asList(s));
    }

    @Override
    public TopicInfoNode getEditNode() {
        return editnode;
    }

    @Override
    public void writeToEditNode() {
        editnode.editname.setText(getName());
        editnode.edittopic.setText(getTopic());
        editnode.edittopicpub.setText(getTopicpub());
        editnode.editicon.setValue(getIcon());
        editnode.editcolor.setValue(getColor());
    }

    @Override
    public void readFromEditNode() {
        name = editnode.editname.getText();
        topic = editnode.edittopic.getText();
        topicpub = editnode.edittopicpub.getText() == null || editnode.edittopicpub.getText().isEmpty() ? null : editnode.edittopicpub.getText();
        icon = editnode.editicon.getValue();
        color = editnode.editcolor.getValue();
    }  
    
    public String getName() {
        return name;
    }
    
    public String getTopic() {
        return topic;
    }

    public String getTopicpub() {
        return topicpub;
    }

    public String getIcon() {
        return icon;
    }
    
    public Color getColor() {
        return color;
    }
    
    private String webColor(Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }      
}
