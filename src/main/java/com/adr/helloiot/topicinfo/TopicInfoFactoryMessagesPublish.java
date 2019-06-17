//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
package com.adr.helloiot.topicinfo;

import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class TopicInfoFactoryMessagesPublish implements TopicInfoFactory {
    
    private TopicInfoMessagesPublishNode messagespublishnode = null;
    private final String name;
    
    public TopicInfoFactoryMessagesPublish() {
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/clientlogin");
        name = resources.getString("label.topicinfo.MessagesPublish");
    }

    @Override
    public String getType() {
        return "MessagesPublish";
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public TopicInfoNode getTopicInfoNode() {
        return messagespublishnode;
    }   
    
    @Override
    public TopicInfo create() {
        if (messagespublishnode == null) {
            messagespublishnode = new TopicInfoMessagesPublishNode();
        }
        return new TopicInfoMessagesPublish(this, messagespublishnode);
    }

    @Override
    public Node getGraphic() {
        Text t = IconBuilder.create(IconFontGlyph.FA_SOLID_ENVELOPE, 18.0).build();
        t.setFill(Color.WHITE);
        TextFlow tf = new TextFlow(t);
        tf.setTextAlignment(TextAlignment.CENTER);
        tf.setPadding(new Insets(5, 5, 5, 5));
        tf.setStyle("-fx-background-color: #505050; -fx-background-radius: 5px;");
        tf.setPrefWidth(30.0);        
        return tf;
    }
}
