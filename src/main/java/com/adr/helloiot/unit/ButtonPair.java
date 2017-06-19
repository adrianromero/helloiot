//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceSimple;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class ButtonPair extends Tile {

    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic");

    private Button goup;
    private Button godown;
    private boolean roll = false;

    private DeviceSimple device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);

    @Override
    public Node constructContent() {

        goup = new Button();
        goup.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_UP, 22.0).styleClass("icon-fill").build());
        goup.setContentDisplay(ContentDisplay.TOP);
        goup.getStyleClass().add("buttonbase");
        goup.getStyleClass().add("buttonup");
        VBox.setVgrow(goup, Priority.SOMETIMES);
        goup.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        goup.setFocusTraversable(false);
        goup.setOnAction(event -> {
            device.sendStatus(roll ? device.rollNextStatus() : device.nextStatus());
        });

        godown = new Button();
        godown.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_DOWN, 22.0).styleClass("icon-fill").build());
        godown.setContentDisplay(ContentDisplay.TOP);
        godown.getStyleClass().add("buttonbase");
        godown.getStyleClass().add("buttondown");
        VBox.setVgrow(godown, Priority.SOMETIMES);
        godown.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        godown.setFocusTraversable(false);
        godown.setOnAction(event -> {
            device.sendStatus(roll ? device.rollPrevStatus() : device.prevStatus());
        });

//        setIconStatus(IconStatus.valueOf("TEXT/ON/OFF"));
        VBox content = new VBox(goup, godown);
        content.setSpacing(2);
        VBox.setVgrow(content, Priority.SOMETIMES);
        content.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        return content;
    }

    private void updateStatus(byte[] status) {
        if (!roll) {
            goup.setDisable(!device.hasNextStatus());
            godown.setDisable(!device.hasPrevStatus());            
        }
    }
    
    @Override
    public void construct(HelloIoTAppPublic app) {
        super.construct(app);
        device.subscribeStatus(messageHandler);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        super.destroy();
        device.unsubscribeStatus(messageHandler);
    }
    
    public void setRoll(boolean value) {
        roll = value;
    }

    public boolean isRoll() {
        return roll;
    }

    public void setDevice(DeviceSimple device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
    }

    public DeviceSimple getDevice() {
        return device;
    }
}
