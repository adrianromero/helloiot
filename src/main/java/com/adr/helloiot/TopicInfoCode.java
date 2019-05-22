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
package com.adr.helloiot;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.unit.Unit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
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
public class TopicInfoCode implements TopicInfo {

    private final SimpleStringProperty name = new SimpleStringProperty();
    private String code;

    private final TopicInfoCodeNode editnode;

    public TopicInfoCode(TopicInfoCodeNode editnode) {
        this.editnode = editnode;
    }

    @Override
    public String getType() {
        return "Code";
    }

    @Override
    public ReadOnlyProperty<String> getLabel() {
        return name;
    }

    @Override
    public Node getGraphic() {

        Text t = IconBuilder.create(FontAwesome.FA_FILE_CODE_O, 18.0).build();
        t.setFill(Color.WHITE);
        TextFlow tf = new TextFlow(t);
        tf.setTextAlignment(TextAlignment.CENTER);
        tf.setPadding(new Insets(5, 5, 5, 5));
        tf.setStyle("-fx-background-color: #d48545; -fx-background-radius: 5px;");
        tf.setPrefWidth(30.0);        
        return tf; 
    }

    @Override
    public void load(SubProperties properties) {
        name.setValue(properties.getProperty(".name"));
        code = properties.getProperty(".code");
    }

    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".name", getName());
        properties.setProperty(".code", getCode());
    }

    @Override
    public DevicesUnits getDevicesUnits() throws HelloIoTException {

        String fxml
                = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<?import java.lang.*?>\n"
                + "<?import java.util.*?>\n"
                + "<?import javafx.scene.layout.*?>\n"
                + "<?import com.adr.fonticon.*?>\n"
                + "<?import com.adr.helloiot.*?>\n"
                + "<?import com.adr.helloiot.unit.*?>\n"
                + "<?import com.adr.helloiot.unitsensor.*?>\n"
                + "<?import com.adr.helloiot.device.*?>\n"
                + "<?import com.adr.helloiot.device.format.*?>\n"
                + "<?import com.adr.helloiot.graphic.*?>\n"
                + "<?import com.adr.helloiot.mqtt.MQTTProperty?>\n"
                + "<ArrayList xmlns=\"http://javafx.com/javafx/8.0.40\" xmlns:fx=\"http://javafx.com/fxml/1\">\n"
                + code
                + "</ArrayList>";

        try (InputStream in = new ByteArrayInputStream(fxml.getBytes(StandardCharsets.UTF_8))) {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(StandardCharsets.UTF_8);
            ArrayList list = fxmlloader.<ArrayList>load(in);
            List<Device> devices = new ArrayList<>();
            List<Unit> units = new ArrayList<>();

            for (Object o : list) {
                if (o instanceof Device) {
                    devices.add((Device) o);
                } else if (o instanceof Unit) {
                    units.add((Unit) o);
                } else if (o instanceof ApplicationDevicesUnits) {
                    ApplicationDevicesUnits du = (ApplicationDevicesUnits) o;
                    devices.addAll(du.getDevices());
                    units.addAll(du.getUnits());
                }
            }
            return new DevicesUnits(devices, units);
        } catch (IOException ex) {
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
            String label = getLabel().getValue();
            throw new HelloIoTException(String.format(resources.getString("exception.topicinfocode"), label == null || label.isEmpty() ? resources.getString("label.empty") : label), ex);
        }
    }

    @Override
    public TopicInfoNode getEditNode() {
        return editnode;
    }

    @Override
    public void writeToEditNode() {
        editnode.name.setText(getName());
        editnode.code.setText(code);
    }

    @Override
    public void readFromEditNode() {
        name.setValue(editnode.name.getText());
        code = editnode.code.getText();
    }

    public String getName() {
        return name.getValue();
    }

    public String getCode() {
        return code;
    }
}
