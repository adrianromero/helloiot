/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.helloiot.unit;

import com.adr.helloiot.EventMessage;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.device.format.StringFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

/**
 *
 * @author adrian
 */
public class MessageItem extends BorderPane {
      
    public MessageItem(EventMessage message, StringFormat format) {
        getStyleClass().add("message");
        setMaxSize(Double.MAX_VALUE, 60.0);
        setMinSize(Control.USE_COMPUTED_SIZE, 60.0);
        setPrefSize(Control.USE_COMPUTED_SIZE, 60.0);
        HBox.setHgrow(this, Priority.SOMETIMES);   
        
        String txt = format.format(format.value(message.getMessage()));
        
        TextField messageview = new TextField(txt);
        messageview.setEditable(false);
        messageview.setFocusTraversable(false);
        messageview.getStyleClass().add("unitinputview");
        BorderPane.setAlignment(messageview, Pos.CENTER_LEFT);

        setCenter(messageview);
        
        HBox footer = new HBox();
        
        Label topictext = new Label(message.getTopic());
        topictext.getStyleClass().add("messagefooter");
        topictext.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(topictext, Priority.ALWAYS);
        footer.getChildren().add(topictext);        
        
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Label datetext = new Label(LocalDateTime.now().format(dtf));
        datetext.getStyleClass().add("messagefooter");
        footer.getChildren().add(datetext);
        
        MiniVar v = message.getProperty("mqtt.qos");
        if (v != null) {
            Label qostext = new Label("QoS " + v.asInt());
            qostext.getStyleClass().addAll("messagefooter", "badgeqos");
            footer.getChildren().add(qostext);            
        }
        MiniVar v2 = message.getProperty("mqtt.retained");
        if (v2 != null) {
            Label retainedtext = new Label(v2.asBoolean() ? "RETAINED" : "");
            retainedtext.getStyleClass().addAll("messagefooter", "badgeretained");
            footer.getChildren().add(retainedtext);            
        }        
        setBottom(footer);
 
    }
    
    public Node getNode() {
        return this;
    }    
}
