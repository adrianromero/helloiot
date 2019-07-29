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

import com.adr.helloiot.topicinfo.TopicInfoNode;
import com.adr.helloiot.topicinfo.TopicInfo;
import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.Dialogs;
import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class ClientLoginNode {

    private final ResourceBundle resources;
    private BorderPane rootpane;
    private Button nextbutton;
    private VBox connections;
    private CheckBox mainpage;
    
    private Button adddeviceunit;
    private Button removedeviceunit;
    private Button updeviceunit;
    private Button downdeviceunit;
    
    private ListView<TopicInfo> devicesunitslist;
    private ComboBox<TopicInfo> devicesunitslist_mobile;
    private Label devicesunitscounter_mobile;
    private SelectionModel<TopicInfo> devicesunitsselection;
    private ObservableList<TopicInfo> devicesunitsitems;
    
    private ScrollPane deviceunitform;
    private Label propslabel;
    
    private StackPane topicinfocontainer;
    private ToolBar unitstoolbar;
    
    private ChoiceBox<Style> skins;
        
    private TopicInfoNode editnode = null;
    
    private String topicapp;
    private String topicsys;
    
    private boolean updating = false;

    public ClientLoginNode() {      
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/clientlogin"); 
        if (HelloPlatform.getInstance().isPhone()) {
            load_mobile();
        } else {
            load();
        }
        initialize();
    }
    
    private void load() {
        
        rootpane = new BorderPane();
        
        HBox hbox = new HBox();
        hbox.setSpacing(6.0);
        hbox.getStyleClass().add("header");
        BorderPane.setAlignment(hbox, Pos.CENTER);
        
        Label label = new Label(resources.getString("label.clientlogin"));
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("headertitle");
        HBox.setHgrow(label, Priority.SOMETIMES);
        
        nextbutton = new Button(resources.getString("button.connect"));
        nextbutton.setFocusTraversable(false);
        nextbutton.setMnemonicParsing(false);
        nextbutton.getStyleClass().addAll("nextbutton", "nextbutton-default");
        
        hbox.getChildren().addAll(label, nextbutton);
        
        rootpane.setTop(hbox);
        
        TabPane tabpane = new TabPane();
        tabpane.getStyleClass().add("unittabpane");
        tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        BorderPane.setAlignment(tabpane, Pos.CENTER);
        
        Tab tab0 = new Tab(resources.getString("tab.connection"));
        tab0.setClosable(false);
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("unitform");
        
        connections = new VBox();
        
        scroll.setContent(connections);
        
        tab0.setContent(scroll);
        
        Tab tab1 = new Tab(resources.getString("tab.devicesunits"));
        tab1.setClosable(false);
        
        BorderPane borderpanetab1 = new BorderPane();
        
        unitstoolbar = new ToolBar();
        unitstoolbar.getStyleClass().add("unittoolbar");
        BorderPane.setAlignment(unitstoolbar, Pos.CENTER);
        
        adddeviceunit = new Button();
        adddeviceunit.setFocusTraversable(false);
        adddeviceunit.setMnemonicParsing(false);
        adddeviceunit.getStyleClass().add("unitbutton");
        adddeviceunit.setOnAction(this::onAddDeviceUnit);
        
        // TODO: Implement Tradfri Button
        // Button tradfributton = createTradfriButton();
                
        Separator sep = new Separator(Orientation.VERTICAL);
                       
        removedeviceunit = new Button();
        removedeviceunit.setFocusTraversable(false);
        removedeviceunit.setMnemonicParsing(false);
        removedeviceunit.getStyleClass().add("unitbutton");
        removedeviceunit.setOnAction(this::onRemoveDeviceUnit);
        
        updeviceunit = new Button();
        updeviceunit.setFocusTraversable(false);
        updeviceunit.setMnemonicParsing(false);
        updeviceunit.getStyleClass().add("unitbutton");
        updeviceunit.setOnAction(this::onUpDeviceUnit);
        
        downdeviceunit = new Button();
        downdeviceunit.setFocusTraversable(false);
        downdeviceunit.setMnemonicParsing(false);
        downdeviceunit.getStyleClass().add("unitbutton");
        downdeviceunit.setOnAction(this::onDownDeviceUnit);      

        unitstoolbar.getItems().addAll(adddeviceunit, sep, removedeviceunit, updeviceunit, downdeviceunit);
        
        borderpanetab1.setTop(unitstoolbar);
        
        VBox vbox2 = new VBox();        
        
        ////
        devicesunitslist = new ListView<>();
        devicesunitslist.setPrefWidth(280.0);
        devicesunitslist.getStyleClass().add("unitlistview");
        HBox.setMargin(devicesunitslist, new Insets(5.0));
        HBox.setHgrow(devicesunitslist, Priority.NEVER);    
        devicesunitslist.setCellFactory(l -> new DevicesUnitsListCell());
        devicesunitsselection = devicesunitslist.getSelectionModel();
        devicesunitsitems = FXCollections.observableArrayList(t -> new Observable[]{
            t.getLabel()
        });
        devicesunitslist.setItems(devicesunitsitems);
        ////

        deviceunitform = new ScrollPane();
        deviceunitform.setFitToWidth(true);
        deviceunitform.setFitToHeight(true);
        deviceunitform.getStyleClass().add("unitscroll");
        HBox.setMargin(deviceunitform, new Insets(5.0));
        HBox.setHgrow(deviceunitform, Priority.ALWAYS);

        GridPane griddeviceunit = new GridPane();
        griddeviceunit.getStyleClass().add("unitform");
        griddeviceunit.setHgap(10.0);
        griddeviceunit.setVgap(10.0);       
        ColumnConstraints constr = new ColumnConstraints();
        constr.setHgrow(Priority.SOMETIMES);
        griddeviceunit.getColumnConstraints().addAll(
               new ColumnConstraints(150.0, 150.0, Region.USE_COMPUTED_SIZE),
               new ColumnConstraints(200.0, 200.0, Region.USE_COMPUTED_SIZE),
               new ColumnConstraints(150.0, 150.0, Region.USE_COMPUTED_SIZE),
               constr);
        griddeviceunit.getRowConstraints().addAll(
                new RowConstraints());
        griddeviceunit.setPadding(new Insets(10.0, 10.0, 0.0, 10.0));
        
        propslabel = new Label(resources.getString("label.properties"));
        propslabel.getStyleClass().add("unitsection");
        propslabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(propslabel, Integer.MAX_VALUE);
        GridPane.setRowIndex(propslabel, 0);
        
        griddeviceunit.getChildren().addAll(propslabel);
        
        topicinfocontainer = new StackPane();
        VBox.setVgrow(topicinfocontainer, Priority.SOMETIMES);
        
        vbox2.getChildren().addAll(griddeviceunit, topicinfocontainer);
        
        deviceunitform.setContent(vbox2);

        HBox hboxunits = new HBox();
        hboxunits.getStyleClass().add("unitwindow");
        hboxunits.getChildren().addAll(devicesunitslist, deviceunitform);
        
        borderpanetab1.setCenter(hboxunits);

        tab1.setContent(borderpanetab1);
        
        Tab tab2 = new Tab(resources.getString("tab.configuration"));
        tab2.setClosable(false);
        
        ScrollPane scrolltab2 = new ScrollPane();
        scrolltab2.setFitToWidth(true);
        scrolltab2.getStyleClass().add("unitform");
        
        GridPane grid2 = new GridPane();
        grid2.setHgap(10.0);
        grid2.setVgap(10.0);       
        ColumnConstraints constr2 = new ColumnConstraints();
        constr2.setHgrow(Priority.SOMETIMES);
        grid2.getColumnConstraints().addAll(
               new ColumnConstraints(150.0, 150.0, Region.USE_COMPUTED_SIZE),
               new ColumnConstraints(200.0, 200.0, Region.USE_COMPUTED_SIZE),
               constr2);
        grid2.getRowConstraints().addAll(
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE),
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE));
        grid2.setPadding(new Insets(10.0, 10.0, 0.0, 10.0));
        
        Label labelstyle = new Label(resources.getString("label.style"));
        labelstyle.getStyleClass().add("unitlabel");
        
        skins = new ChoiceBox<>();
        skins.setPrefWidth(280.0);
        skins.setMaxWidth(Double.MAX_VALUE);
        skins.getStyleClass().add("unitinput");
        GridPane.setColumnIndex(skins, 1);
        
        grid2.getChildren().addAll(labelstyle, skins);
        
        scrolltab2.setContent(grid2);
        
        tab2.setContent(scrolltab2);
                
        tabpane.getTabs().addAll(tab0, tab1, tab2);
        
        rootpane.setCenter(tabpane);    
    }
    
    private void load_mobile() {
        
        rootpane = new BorderPane();
        
        HBox hbox = new HBox();
        hbox.setSpacing(6.0);
        hbox.getStyleClass().add("header");
        BorderPane.setAlignment(hbox, Pos.CENTER);
        
        Label label = new Label(resources.getString("label.clientlogin"));
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("headertitle");
        HBox.setHgrow(label, Priority.SOMETIMES);
        
        nextbutton = new Button(resources.getString("button.connect"));
        nextbutton.setFocusTraversable(false);
        nextbutton.setMnemonicParsing(false);
        nextbutton.getStyleClass().addAll("nextbutton", "nextbutton-default");
        
        hbox.getChildren().addAll(label, nextbutton);
        
        rootpane.setTop(hbox);
        
        TabPane tabpane = new TabPane();
        tabpane.getStyleClass().add("unittabpane");
        tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        BorderPane.setAlignment(tabpane, Pos.CENTER);
        
        Tab tab0 = new Tab(resources.getString("tab.connection"));
        tab0.setClosable(false);
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("unitform");
        
        connections = new VBox();
        
        scroll.setContent(connections);
        
        tab0.setContent(scroll);
        
        Tab tab1 = new Tab(resources.getString("tab.devicesunits"));
        tab1.setClosable(false);
        
        BorderPane borderpanetab1 = new BorderPane();
               
        unitstoolbar = new ToolBar();
        unitstoolbar.getStyleClass().add("unittoolbar");
        BorderPane.setAlignment(unitstoolbar, Pos.CENTER);
        
        adddeviceunit = new Button();
        adddeviceunit.setFocusTraversable(false);
        adddeviceunit.setMnemonicParsing(false);
        adddeviceunit.getStyleClass().add("unitbutton");
        adddeviceunit.setOnAction(this::onAddDeviceUnit);
        
        // TODO: Implement Tradfri Button
        // Button tradfributton = createTradfriButton();
                
        Separator sep = new Separator(Orientation.VERTICAL);
        
        removedeviceunit = new Button();
        removedeviceunit.setFocusTraversable(false);
        removedeviceunit.setMnemonicParsing(false);
        removedeviceunit.getStyleClass().add("unitbutton");
        removedeviceunit.setOnAction(this::onRemoveDeviceUnit);
        
        updeviceunit = new Button();
        updeviceunit.setFocusTraversable(false);
        updeviceunit.setMnemonicParsing(false);
        updeviceunit.getStyleClass().add("unitbutton");
        updeviceunit.setOnAction(this::onUpDeviceUnit);
        
        downdeviceunit = new Button();
        downdeviceunit.setFocusTraversable(false);
        downdeviceunit.setMnemonicParsing(false);
        downdeviceunit.getStyleClass().add("unitbutton");
        downdeviceunit.setOnAction(this::onDownDeviceUnit);
        
        unitstoolbar.getItems().addAll(adddeviceunit, sep, removedeviceunit, updeviceunit, downdeviceunit);
        
        borderpanetab1.setTop(unitstoolbar); 
                
        VBox vbox2 = new VBox();
        
        ////
        HBox hboxdeviceunitscontainer = new HBox();        
        VBox.setMargin(hboxdeviceunitscontainer, new Insets(5.0));
        VBox.setVgrow(hboxdeviceunitscontainer, Priority.NEVER);     
        hboxdeviceunitscontainer.setSpacing(5.0);
        hboxdeviceunitscontainer.setAlignment(Pos.CENTER_LEFT);
        
        devicesunitslist_mobile = new ComboBox<>();
        devicesunitslist_mobile.setMinSize(280.0, 40.0);
        devicesunitslist_mobile.setPrefSize(280.0, 40.0);
        devicesunitslist_mobile.setMaxSize(Double.MAX_VALUE, 40.0);
        devicesunitslist_mobile.getStyleClass().add("unitinput");
        devicesunitslist_mobile.setButtonCell(new DevicesUnitsListCell()); 
        devicesunitslist_mobile.setCellFactory((ListView<TopicInfo> list) -> new DevicesUnitsListCell());  
        HBox.setHgrow(devicesunitslist_mobile, Priority.SOMETIMES);
        devicesunitsselection = devicesunitslist_mobile.getSelectionModel();
        devicesunitsitems = FXCollections.observableArrayList(t -> new Observable[]{
            t.getLabel()
        });
        devicesunitslist_mobile.setItems(devicesunitsitems);  
        devicesunitsselection.selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            changeCounterLabel(newValue.intValue(), devicesunitsitems.size());
        });
        devicesunitsitems.addListener((ListChangeListener.Change<? extends TopicInfo> c) -> {
            changeCounterLabel(devicesunitsselection.getSelectedIndex(), devicesunitsitems.size());
        });
        
        devicesunitscounter_mobile = new Label();
        devicesunitscounter_mobile.setMinWidth(80.0);
        devicesunitscounter_mobile.setAlignment(Pos.CENTER);
        devicesunitscounter_mobile.getStyleClass().add("unitlabel");
        
        hboxdeviceunitscontainer.getChildren().addAll(devicesunitslist_mobile, devicesunitscounter_mobile);
        ////
        
        deviceunitform = new ScrollPane();
        deviceunitform.setFitToWidth(true);
        deviceunitform.setFitToHeight(true);
        deviceunitform.getStyleClass().add("unitscroll");
        VBox.setVgrow(deviceunitform, Priority.ALWAYS);
        
        GridPane griddeviceunit = new GridPane();
        griddeviceunit.getStyleClass().add("unitform");
        griddeviceunit.setHgap(10.0);
        griddeviceunit.setVgap(10.0);       
        ColumnConstraints constr = new ColumnConstraints();
        constr.setHgrow(Priority.SOMETIMES);
        griddeviceunit.getColumnConstraints().add(
               constr);
        griddeviceunit.getRowConstraints().addAll(
                new RowConstraints());
        griddeviceunit.setPadding(new Insets(10.0, 10.0, 0.0, 10.0));

        propslabel = new Label(resources.getString("label.properties"));
        propslabel.getStyleClass().add("unitsection");
        propslabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(propslabel, Integer.MAX_VALUE);
        GridPane.setRowIndex(propslabel, 0);
        
        griddeviceunit.getChildren().addAll(propslabel);
        
        topicinfocontainer = new StackPane();
        VBox.setVgrow(topicinfocontainer, Priority.SOMETIMES);
        
        vbox2.getChildren().addAll(griddeviceunit, topicinfocontainer);

        deviceunitform.setContent(vbox2);
        
        VBox vboxunits = new VBox();
        vboxunits.getStyleClass().add("unitwindow");
        vboxunits.getChildren().addAll(hboxdeviceunitscontainer, deviceunitform);

        borderpanetab1.setCenter(vboxunits);
 
        tab1.setContent(borderpanetab1);
        
        Tab tab2 = new Tab(resources.getString("tab.configuration"));
        tab2.setClosable(false);
        
        ScrollPane scrolltab2 = new ScrollPane();
        scrolltab2.setFitToWidth(true);
        scrolltab2.getStyleClass().add("unitform");
        
        GridPane grid2 = new GridPane();
        grid2.setHgap(10.0);
        grid2.setVgap(10.0);       
        ColumnConstraints constr2 = new ColumnConstraints();
        constr2.setPrefWidth(0.0);
        constr2.setHgrow(Priority.SOMETIMES);
        grid2.getColumnConstraints().add(
               constr2);
        grid2.getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE),
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE));
        grid2.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        
        Label labelstyle = new Label(resources.getString("label.style"));
        labelstyle.getStyleClass().add("unitlabel");
        
        skins = new ChoiceBox<>();
        skins.setPrefWidth(280.0);
        skins.setMaxWidth(Double.MAX_VALUE);
        skins.getStyleClass().add("unitinput");
        GridPane.setRowIndex(skins, 1);

        grid2.getChildren().addAll(labelstyle, skins);
        
        scrolltab2.setContent(grid2);
        
        tab2.setContent(scrolltab2);
                
        tabpane.getTabs().addAll(tab0, tab1, tab2);
        
        rootpane.setCenter(tabpane);    
    }

    private void changeCounterLabel(int index, int size) {
        if (devicesunitscounter_mobile != null) { // be sure we are in mobile
            devicesunitscounter_mobile.setText(String.format("%s / %s", index < 0 ? "-" : Integer.toString(index + 1), size <= 0 ? "-" : Integer.toString(size)));
        }
    }
    
    private class DevicesUnitsListCell extends ListCell<TopicInfo> {
        @Override
        public void updateItem(TopicInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setGraphic(null);
                setText(null);
            } else {
                Text t = IconBuilder.create(item.getFactory().getGlyph(), 18.0).build();
                t.setFill(Color.WHITE);
                TextFlow tf = new TextFlow(t);
                tf.setTextAlignment(TextAlignment.CENTER);
                tf.setPadding(new Insets(5, 5, 5, 5));
                tf.setStyle("-fx-background-color: #505050; -fx-background-radius: 5px;");
                tf.setPrefWidth(30.0);              
                setGraphic(tf);
                
                String label = item.getLabel().getValue();
                setText(item.getFactory().getTypeName() + ((label == null || label.isEmpty()) ? "" : " : " + label));
            }
        }        
    }
    
    public void appendConnectNode(Node n) {
        connections.getChildren().add(n);
    }

    private void initialize() {

        nextbutton.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_PLAY, 18.0).styleClass("icon-fill").build());

        adddeviceunit.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_FILE_ALT, 18.0).styleClass("icon-fill").build());
        adddeviceunit.setText(resources.getString("title.new"));
        
        removedeviceunit.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_TRASH_ALT, 18.0).styleClass("icon-fill").build());
        updeviceunit.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_CHEVRON_UP, 18.0).styleClass("icon-fill").build());
        downdeviceunit.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_CHEVRON_DOWN, 18.0).styleClass("icon-fill").build());

        devicesunitsselection.selectedItemProperty().addListener((ObservableValue<? extends TopicInfo> ov, TopicInfo old_val, TopicInfo new_val) -> {
            updateDevicesUnitsList();
        });
        updateDevicesUnitsList();
        
        skins.getItems().addAll(Style.values());
        skins.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> {
            if (!updating) {
                Style.changeStyle(MessageUtils.getRoot(rootpane), new_val);
            }
        });
    }

    public Node getNode() {
        return rootpane;
    }

    private void updateCurrentTopic() {
        if (!updating) {     
            TopicInfo topic = devicesunitsselection.getSelectedItem();
            topic.readFromEditNode();
        }
    }

    private void updateDevicesUnitsList() {
        TopicInfo topic = devicesunitsselection.getSelectedItem();
        int index = devicesunitsselection.getSelectedIndex();
        if (topic == null) {
            removedeviceunit.setDisable(true);
            deviceunitform.setDisable(true);
            updeviceunit.setDisable(true);
            downdeviceunit.setDisable(true);
            propslabel.setText(resources.getString("label.properties"));
            
            if (editnode != null) {
                editnode.useUpdateCurrent(null);
                topicinfocontainer.getChildren().remove(editnode.getNode());
                editnode = null;
            }            
        } else {
            removedeviceunit.setDisable(false);
            deviceunitform.setDisable(false);
            updeviceunit.setDisable(index <= 0);
            downdeviceunit.setDisable(index >= devicesunitsitems.size() - 1);
            propslabel.setText(resources.getString("label.properties") + " : " + topic.getFactory().getTypeName());
            
            updating = true;
            TopicInfoNode node = topic.getEditNode();
            if (editnode != null && node != editnode) {
                editnode.useUpdateCurrent(null);
                topicinfocontainer.getChildren().remove(editnode.getNode());
                editnode = null;
            }
            if (editnode == null) {
                editnode = node;
                editnode.useUpdateCurrent(this::updateCurrentTopic);
                topicinfocontainer.getChildren().add(editnode.getNode());                
            }          
            topic.writeToEditNode(); 
            updating = false;
        }
    }

    void onAddDeviceUnit(ActionEvent event) {

        DialogView dialog = new DialogView();
        List<TopicsTab> topicstabadd = new ArrayList<>();
        
        TabPane tabadd = new TabPane();        
        tabadd.getStyleClass().add("unittabpane");
        tabadd.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);        
        
        Consumer<ActionEvent> actionok = evOK -> {
            TopicsTab tt = topicstabadd.get(tabadd.getSelectionModel().getSelectedIndex());
            
            DialogView loading3 = Dialogs.createLoading();
            loading3.show(MessageUtils.getRoot(rootpane));             
            CompletableAsync.handle(
                tt.createSelected(),
                result -> {
                    loading3.dispose();
                    devicesunitsitems.add(result);
                    devicesunitsselection.select(result);  
                }, 
                ex -> {
                    loading3.dispose();
                    MessageUtils.showException(MessageUtils.getRoot(rootpane), resources.getString("title.new"),  resources.getString("exception.cannotloadunit"), ex);
                });             
        };
        
        
        // Create Tab
        TopicsTab topicstab0 = new TopicsGallery();
        topicstab0.setActionOK(actionok.andThen(e -> dialog.dispose()));
        // ADD Tab
        topicstabadd.add(topicstab0);
        Tab tab0 = new Tab(topicstab0.getText(), topicstab0.getNode());
        tab0.setClosable(false);
        
        // Create Tab
        TopicsTab topicstab1 = new TopicsTemplate();
        topicstab1.setActionOK(actionok.andThen(e -> dialog.dispose()));
        // Add Tab
        topicstabadd.add(topicstab1);
        Tab tab1 = new Tab(topicstab1.getText(), topicstab1.getNode());
        tab1.setClosable(false);
        
        
        tabadd.getTabs().addAll(tab0, tab1);
             
        dialog.setCSS("/com/adr/helloiot/styles/topicinfodialog.css");
        dialog.setTitle(resources.getString("title.new"));
        dialog.setContent(tabadd);
        dialog.addButtons(dialog.createCancelButton(), dialog.createOKButton());
        dialog.show(MessageUtils.getRoot(rootpane));              
        dialog.setActionOK(actionok);      

    }

    void onRemoveDeviceUnit(ActionEvent event) {
        TopicInfo t = devicesunitsselection.getSelectedItem();
        int index = devicesunitsselection.getSelectedIndex();
        devicesunitsitems.remove(t);
        if (devicesunitsitems.size() > 0 && index < devicesunitsitems.size()) {
            devicesunitsselection.select(index);
        }
    }

    void onUpDeviceUnit(ActionEvent event) {
        TopicInfo topic = devicesunitsselection.getSelectedItem();
        int index = devicesunitsselection.getSelectedIndex();
        devicesunitsitems.remove(index);
        devicesunitsitems.add(index - 1, topic);
        devicesunitsselection.select(index - 1);
    }

    void onDownDeviceUnit(ActionEvent event) {
        TopicInfo topic = devicesunitsselection.getSelectedItem();
        int index = devicesunitsselection.getSelectedIndex();
        devicesunitsitems.remove(index);
        devicesunitsitems.add(index + 1, topic);
        devicesunitsselection.select(index + 1);
    }
  
    public void setOnNextAction(EventHandler<ActionEvent> exitevent) {
        nextbutton.setOnAction(exitevent);
    }

    public String getTopicApp() {
        return topicapp;
    }

    public void setTopicApp(String value) {
        topicapp = value;
    }

    public String getTopicSys() {
        return topicsys;
    }

    public void setTopicSys(String value) {
        topicsys = value;
    }
    
    public void setStyle(Style value) {
        updating = true;
        skins.getSelectionModel().select(value);
        updating = false;
    }
    
    public Style getStyle() {
        return skins.getSelectionModel().getSelectedItem();
    }

    public ObservableList<TopicInfo> getTopicInfoList() {
        return devicesunitsitems;
    }

    public void setTopicInfoList(ObservableList<TopicInfo> list) {
        devicesunitsitems.clear();
        devicesunitsitems.addAll(list);
        if (list.size() > 0) {
            devicesunitsselection.select(0);
        } else {
            changeCounterLabel(-1, 0);
        }
    }

    public boolean isMainPage() {
        return mainpage.isSelected();
    }

    public void setMainPage(boolean value) {
        mainpage.setSelected(value);
    }
    
    public MenuItem createSamplesButton(String key, Node graphic, String resource) {
        MenuItem b = new MenuItem(resources.getString(key), graphic);   
        String fxml = resource + (HelloPlatform.getInstance().isPhone() ? "_mobile.fxml" : ".fxml");
        b.setOnAction(e -> {
            try {
                addCodeUnit(resources.getString(key), Resources.toString(Resources.getResource(fxml), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        return b;
    }
    
    public void addCodeUnit(String name, String code) {
        TopicInfo t = TopicInfoBuilder.INSTANCE.create("Code");
        BaseSubProperties props = new BaseSubProperties();
        props.setProperty(".name", name);
        props.setProperty(".code", code);
        t.load(props);

        devicesunitsitems.add(t);
        devicesunitsselection.select(t);        
    }
     
//    private Button createTradfriButton() {
//
//        Button b = new Button(resources.getString("button.tradfri"), IconBuilder.create(IconFontGlyph.FA_SOLID_SEARCH, 18.0).styleClass("icon-fill").build());       
//        b.setFocusTraversable(false);
//        b.setMnemonicParsing(false);
//        b.getStyleClass().add("unitbutton");       
//        b.setOnAction(e -> {
//            ConfigProperties tempconfig = new ConfigProperties();
//            clienttradfri.saveConfig(new ConfigSubProperties(tempconfig, "tradfri."));
//            
//            if (HTTPUtils.getAddress(tempconfig.getProperty("tradfri.host", "")) == null) {
//                MessageUtils.showWarning(MessageUtils.getRoot(root), resources.getString("title.tradfridiscovery"), resources.getString("message.notradfriconnection"));                
//                return;
//            }
//
//            DialogView loading2 = Dialogs.createLoading();
//            loading2.show(MessageUtils.getRoot(root));    
//
//            CompletableAsync.handle(clienttradfri.requestSample(
//                    tempconfig.getProperty("tradfri.host"), 
//                    tempconfig.getProperty("tradfri.identity"), 
//                    tempconfig.getProperty("tradfri.psk")), 
//                units -> {
//                    loading2.dispose();
//                    for(Map.Entry<String, String> entry: units.entrySet()) {
//                        clientlogin.addCodeUnit(entry.getKey(), entry.getValue());
//                    }
//                },
//                ex -> {                             
//                    loading2.dispose();
//                    MessageUtils.showException(MessageUtils.getRoot(root), resources.getString("title.tradfridiscovery"), ex.getLocalizedMessage(), ex);
//                });  
//        });
//        return b;
//    }     
}
