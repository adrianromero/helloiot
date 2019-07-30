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

import com.adr.helloiotlib.format.StringFormatLong;
import com.adr.helloiotlib.unit.Units;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class TimeIndicator {

    private final Label l;
    private final DateTimeFormatter formatter;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);      

    public TimeIndicator(String pattern) {
        l = new Label();
        l.setMaxHeight(Double.MAX_VALUE);
        l.getStyleClass().add("currenttime");
        formatter = DateTimeFormatter.ofPattern(pattern);
    }
    
    public Node getNode() {
        return l;
    }

    public Object getMessageHandler() {
        return messageHandler;
    }
    
    private void updateStatus(byte[] status) {
        long epochsecond = StringFormatLong.INSTANCE.value(status).asLong();
        LocalDateTime datetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochsecond), ZoneId.systemDefault());     
        l.setText(datetime.format(formatter));
    }
}
