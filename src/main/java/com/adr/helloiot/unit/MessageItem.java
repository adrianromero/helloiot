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

import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatIdentity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public class MessageItem extends BorderPane {
    
    private final static ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/messages");
          
    public MessageItem(EventMessage message) {
        getStyleClass().add("message");
        setMaxSize(Double.MAX_VALUE, 60.0);
        setMinSize(Control.USE_COMPUTED_SIZE, 60.0);
        setPrefSize(Control.USE_COMPUTED_SIZE, 60.0);
        HBox.setHgrow(this, Priority.SOMETIMES);   
        
        StringFormat format = StringFormatIdentity.INSTANCE;
        String txt = format.format(format.value(message.getMessage()));
        
        Label messageview = new Label(txt);
        messageview.setTextOverrun(OverrunStyle.ELLIPSIS);
        messageview.getStyleClass().add("unitinputview");
        BorderPane.setAlignment(messageview, Pos.CENTER_LEFT);

        setCenter(messageview);
        
        HBox footer = new HBox();
        
        Label topictext = new Label(message.getTopic());
        topictext.setTextOverrun(OverrunStyle.ELLIPSIS);
        topictext.getStyleClass().add("messagefooter");
        topictext.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(topictext, Priority.ALWAYS);
        footer.getChildren().add(topictext);        
        
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Label datetext = new Label(LocalDateTime.now().format(dtf));
        datetext.getStyleClass().add("messagefooter");
        footer.getChildren().add(datetext);
        
        MiniVar v2 = message.getProperty("mqtt.retained");
        if (v2 != null) {
            Label retainedtext = new Label(v2.asBoolean() ? resources.getString("badge.retained.true") : resources.getString("badge.retained.false"));
            retainedtext.getStyleClass().addAll("badge", "badgeretained");
            footer.getChildren().add(retainedtext);            
        }    
        
        MiniVar v = message.getProperty("mqtt.qos");
        if (v != null) {
            Label qostext = new Label(String.format(resources.getString("badge.qos"), v.asInt()));
            qostext.getStyleClass().addAll("badge", "badgeqos");
            footer.getChildren().add(qostext);            
        }
    
        setBottom(footer);
    }   
}
