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
import com.adr.fonticon.IconFontGlyph;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.topicinfo.TopicInfo;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.Dialogs;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
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

public class TopicsTemplate implements TopicsTab {
    
    private final ListView<TemplateInfo> list;
    private final StackPane container;
    private final String text;
    private Consumer<ActionEvent> actionok = null;
    
    public TopicsTemplate() {
        
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/clientlogin"); 
        text = resources.getString("title.templates");
        
        list = new ListView<>();
        list.setDisable(true);
        list.getStyleClass().add("unitlistview");
        list.setCellFactory(l -> new TemplatesListCell());
        list.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && actionok != null) {
                actionok.accept(new ActionEvent());
            }
        });
        Node loading = Dialogs.createSmallLoadingNode();          
        container = new StackPane(list, loading);
        container.setPadding(new Insets(15));
        
        
        // Load list of templates         
        CompletableAsync.handle(
            loadTemplatesList(),
            templateslist -> {
                container.getChildren().remove(loading);                
                list.setDisable(false);
                list.setItems(FXCollections.observableList(Arrays.asList(templateslist)));
                list.getSelectionModel().selectFirst();
            },
            ex -> {
                container.getChildren().remove(loading);
                MessageUtils.showException(MessageUtils.getRoot(container), resources.getString("title.templates"),  resources.getString("exception.cannotloadtemplateslist"), ex);             
            });        
        
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
        TemplateInfo templateinfo = list.getSelectionModel().getSelectedItem();
        
        if (templateinfo == null) {
            return Futures.immediateFailedFuture(new RuntimeException());
        }
        
        String fxml = "https://raw.githubusercontent.com/adrianromero/helloiot-units/master/" +
                templateinfo.file +
                (HelloPlatform.getInstance().isPhone() ? "_mobile.fxml" : ".fxml");
   
        return CompletableAsync.supplyAsync(() -> {
            String template = Resources.toString(new URL(fxml), StandardCharsets.UTF_8);
            TopicInfo t = TopicInfoBuilder.INSTANCE.create("Code");
            BaseSubProperties props = new BaseSubProperties();
            props.setProperty(".name", templateinfo.name);
            props.setProperty(".code", template);
            t.load(props);  
            return t;
        });               
    }
    
    private ListenableFuture<TemplateInfo[]> loadTemplatesList() {   
        return CompletableAsync.supplyAsync(() -> {
            try {
                String out = new Scanner(new URL("https://raw.githubusercontent.com/adrianromero/helloiot-units/master/units.json").openStream(), "UTF-8").useDelimiter("\\A").next();
                Gson gson = new Gson();
                return gson.fromJson(out, TemplateInfo[].class);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }    
    
    
    private static class TemplateInfo {
        public final String name;
        public final String icon;
        public final String file;
        public TemplateInfo(String name, String icon, String file) {
            this.name = name;
            this.icon = icon;
            this.file = file;
        }
    }
    
    private class TemplatesListCell extends ListCell<TemplateInfo> {
        @Override
        public void updateItem(TemplateInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setGraphic(null);
                setText(null);
            } else {               
                Text t = IconBuilder.create(IconFontGlyph.valueOf(item.icon), 18.0).build();
                t.setFill(Color.WHITE);
                TextFlow tf = new TextFlow(t);
                tf.setTextAlignment(TextAlignment.CENTER);
                tf.setPadding(new Insets(5, 5, 5, 5));
                tf.setStyle("-fx-background-color: #505050; -fx-background-radius: 5px;");
                tf.setPrefWidth(30.0);      
                
                setGraphic(tf);
                setText(item.name);
            }
        }        
    }    
}
