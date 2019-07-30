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
package com.adr.helloiot;

import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.topicinfo.TopicInfo;
import com.adr.helloiot.topicinfo.TopicInfoFactory;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;


public class TopicsGallery implements TopicsTab {
    
    private final ListView<TopicInfoFactory> list;
    private final StackPane container;
    private final String text;
    private Consumer<ActionEvent> actionok = null;
    
    public TopicsGallery () {
        
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/clientlogin"); 
        text = resources.getString("title.gallery");
        
        list = new ListView<>();
        list.getStyleClass().add("unitlistview");
        list.setItems(FXCollections.observableArrayList(TopicInfoBuilder.INSTANCE.getTopicInfoFactories()));       
        list.setCellFactory(l -> new TopicInfoListCell());
        list.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && actionok != null) {
                actionok.accept(new ActionEvent());
            }
        });           
        list.getSelectionModel().selectFirst();
        
        container = new StackPane(list);
        container.setPadding(new Insets(15));        
    }
    
    @Override
    public void setActionOK(Consumer<ActionEvent> actionok) {
        this.actionok = actionok;
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public Node getNode() {
        return container;
    }
    
    @Override
    public ListenableFuture<TopicInfo> createSelected() {
        TopicInfo t = list.getSelectionModel().getSelectedItem().create();
        return Futures.immediateFuture(t);
    }
    
    private class TopicInfoListCell extends ListCell<TopicInfoFactory> {
        @Override
        public void updateItem(TopicInfoFactory item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setGraphic(null);
                setText(null);
            } else {                 
                Text t = IconBuilder.create(item.getGlyph(), 18.0).build();
                t.setFill(Color.WHITE);
                TextFlow tf = new TextFlow(t);
                tf.setTextAlignment(TextAlignment.CENTER);
                tf.setPadding(new Insets(5, 5, 5, 5));
                tf.setStyle("-fx-background-color: #505050; -fx-background-radius: 5px;");
                tf.setPrefWidth(30.0);    
                
                setGraphic(tf);
                setText(item.getTypeName());
            }
        }        
    }      
}
