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

import com.adr.helloiot.EventMessage;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;

/**
 *
 * @author adrian
 */
public class MessageCell extends ListCell<EventMessage> {

    @Override
    public void updateItem(EventMessage item, boolean empty) {
        super.updateItem(item, empty);
        setPrefWidth(10.0);
        setPadding(Insets.EMPTY);
        if (item == null) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(new MessageItem(item));
        }
    }
}
