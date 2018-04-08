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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.HelloIoTAppPublic;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jidefx.utils.AutoRepeatButtonUtils;

/**
 *
 * @author adrian
 */
public class ButtonsSpinner extends Tile {

    private Button goup;
    private Button godown;
    private Label level;

    private DeviceSimple device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);

    @Override
    public Node constructContent() {
        
        VBox vboxroot = new VBox();
        vboxroot.setSpacing(10.0);        
        
        level = new Label();
        level.setAlignment(Pos.CENTER_RIGHT);
        level.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        level.getStyleClass().add("levelbase");
        VBox.setVgrow(level, Priority.SOMETIMES);    
        
        HBox hbox = new HBox();
        hbox.setSpacing(6.0);
        hbox.setAlignment(Pos.TOP_CENTER);
        
        godown = new Button();
        godown.setFocusTraversable(false);
        godown.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        godown.setMnemonicParsing(false);
        godown.getStyleClass().add("buttonbase");
        godown.setOnAction(this::onGoDown);
        
        goup = new Button();
        goup.setFocusTraversable(false);
        goup.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        goup.setMnemonicParsing(false);
        goup.getStyleClass().add("buttonbase");
        goup.setOnAction(this::onGoUp);
        
        hbox.getChildren().addAll(godown, goup);
        
        vboxroot.getChildren().addAll(level, hbox);
        
        initialize();
        return vboxroot;
    }

    public void initialize() {
        goup.setGraphic(IconBuilder.create(FontAwesome.FA_PLUS, 22.0).styleClass("icon-fill").build());
        goup.getStyleClass().add("buttonbase");
        goup.getStyleClass().add("buttonup");
        AutoRepeatButtonUtils.install(goup);
        godown.setGraphic(IconBuilder.create(FontAwesome.FA_MINUS, 22.0).styleClass("icon-fill").build());
        godown.getStyleClass().add("buttonbase");
        godown.getStyleClass().add("buttondown");
        AutoRepeatButtonUtils.install(godown);
        level.setText(null);
    }

    private void updateStatus(byte[] status) {
        level.setText(device.getFormat().format(device.getFormat().value(status)));
        goup.setDisable(!device.hasNextStatus());
        godown.setDisable(!device.hasPrevStatus());
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

    public void setDevice(DeviceSimple device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
    }

    public DeviceSimple getDevice() {
        return device;
    }

    private void onGoDown(ActionEvent event) {
        device.sendStatus(device.prevStatus());
    }

    private void onGoUp(ActionEvent event) {
        device.sendStatus(device.nextStatus());
    }
}
