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
package com.adr.helloiot.unit;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.EventMessage;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceSubscribe;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class MessagesPage extends BorderPane implements Unit {

    private VBox messagescontainer;
    private ScrollPane scroller;
    private Button deletemessages;

    private DeviceSubscribe device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);
    private DoubleProperty wProperty;
    private boolean following = false;

    public MessagesPage() {
        load();
    }

    private void load() {

        HBox.setHgrow(this, Priority.ALWAYS);

        deletemessages = new Button();
        deletemessages.setMnemonicParsing(false);
        deletemessages.setFocusTraversable(false);
        deletemessages.setGraphic(IconBuilder.create(FontAwesome.FA_TRASH, 18.0).styleClass("icon-fill").build());
        deletemessages.setOnAction(this::actionDelete);

        ToolBar toolbar = new ToolBar();
        BorderPane.setAlignment(toolbar, Pos.CENTER);
        toolbar.getItems().addAll(deletemessages);
        setTop(toolbar);

        messagescontainer = new VBox();
        scroller = new ScrollPane(messagescontainer);
        scroller.setFitToWidth(true);
        scroller.setFocusTraversable(false);
        scroller.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        BorderPane.setAlignment(scroller, Pos.CENTER);

        setCenter(scroller);

        wProperty = new SimpleDoubleProperty();
        wProperty.bind(messagescontainer.heightProperty());
        wProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            if (following) {
                scroller.setVvalue(scroller.getVmax());
                following = false;
            }
        });
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        device.subscribeStatus(messageHandler);
        messagescontainer.getChildren().clear();
    }

    private void updateStatus(EventMessage message) {
        following = scroller.getHeight() > messagescontainer.getHeight() || scroller.getVmax() == scroller.getVvalue();
        messagescontainer.getChildren().add(new MessageItem(message, device.getFormat()));
    }

    @Override
    public void destroy() {
        device.unsubscribeStatus(messageHandler);
    }

    @Override
    public Node getNode() {
        return this;
    }

    public void setDevice(DeviceSubscribe device) {
        this.device = device;
    }

    public DeviceSubscribe getDevice() {
        return device;
    }
    
    private void actionDelete(ActionEvent ev) {
        messagescontainer.getChildren().clear();
    }
}
