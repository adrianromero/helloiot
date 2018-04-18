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
package com.adr.helloiot;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
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

/**
 *
 * @author adrian
 */
public class ClientLoginNode {

    private ResourceBundle resources;
    private BorderPane rootpane;
    private Button nextbutton;
    private VBox connections;
    private CheckBox mainpage;
    
    private Button adddeviceunit;
    private Button removedeviceunit;
    private Button updeviceunit;
    private Button downdeviceunit;
    private MenuButton menubutton;
    private ListView<TopicInfo> devicesunitslist;
    private ScrollPane deviceunitform;
    
    private ChoiceBox<String> edittype;
    private StackPane topicinfocontainer;
    private ToolBar unitstoolbar;
    
    private ChoiceBox<Style> skins;
    private CheckBox clock;
        
    private TopicInfoNode editnode = null;
 
    private TopicInfoBuilder topicinfobuilder;
    
    private String topicapp;
    private String topicsys;
    
    private boolean updating = false;

    public ClientLoginNode() {
        
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/clientlogin");
        
        rootpane = new BorderPane();
        rootpane.setPrefSize(900.0, 720.0);
        
        HBox hbox = new HBox();
        hbox.setSpacing(6.0);
        hbox.getStyleClass().add("headerclient");
        BorderPane.setAlignment(hbox, Pos.CENTER);
        
        Label label = new Label(resources.getString("label.clientlogin"));
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("headerclienttitle");
        HBox.setHgrow(label, Priority.SOMETIMES);
        
        nextbutton = new Button(resources.getString("button.connect"));
        nextbutton.setFocusTraversable(false);
        nextbutton.setMnemonicParsing(false);
        nextbutton.getStyleClass().add("nextbutton");
        
        hbox.getChildren().addAll(label, nextbutton);
        
        rootpane.setTop(hbox);
        
        TabPane tabpane = new TabPane();
        tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        BorderPane.setAlignment(tabpane, Pos.CENTER);
        
        Tab tab0 = new Tab(resources.getString("tab.connection"));
        tab0.setClosable(false);
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        
        connections = new VBox();
        
        scroll.setContent(connections);
        
        tab0.setContent(scroll);
        
        Tab tab1 = new Tab(resources.getString("tab.devicesunits"));
        tab1.setClosable(false);
        
        BorderPane borderpanetab1 = new BorderPane();
        
        deviceunitform = new ScrollPane();
        deviceunitform.setFitToWidth(true);
        BorderPane.setMargin(deviceunitform, new Insets(5.0, 5.0, 5.0, 0.0));
        
        VBox vbox2 = new VBox();
        
        GridPane griddeviceunit = new GridPane();
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
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE),
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE),
                new RowConstraints(10.0, 30.0, Region.USE_COMPUTED_SIZE));
        griddeviceunit.setPadding(new Insets(10.0, 10.0, 0.0, 10.0));
        
        Label section = new Label(resources.getString("label.unit"));
        section.getStyleClass().add("formsection");
        section.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(section, Integer.MAX_VALUE);
        
        Label ltype = new Label(resources.getString("label.type"));
        GridPane.setRowIndex(ltype, 1);
        
        edittype = new ChoiceBox<>();
        edittype.setMaxWidth(Double.MAX_VALUE);
        GridPane.setRowIndex(edittype, 1);
        GridPane.setColumnIndex(edittype, 1);
        
        Label lprops = new Label(resources.getString("label.properties"));
        lprops.getStyleClass().add("formsection");
        lprops.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(lprops, Integer.MAX_VALUE);
        GridPane.setRowIndex(lprops, 2);
        
        griddeviceunit.getChildren().addAll(section, ltype, edittype, lprops);
        
        topicinfocontainer = new StackPane();
        
        vbox2.getChildren().addAll(griddeviceunit, topicinfocontainer);
        
        deviceunitform.setContent(vbox2);
        
        borderpanetab1.setCenter(deviceunitform);
        
        unitstoolbar = new ToolBar();
        BorderPane.setAlignment(unitstoolbar, Pos.CENTER);
        
        adddeviceunit = new Button();
        adddeviceunit.setFocusTraversable(false);
        adddeviceunit.setMnemonicParsing(false);
        adddeviceunit.setOnAction(this::onAddDeviceUnit);
        
        removedeviceunit = new Button();
        removedeviceunit.setFocusTraversable(false);
        removedeviceunit.setMnemonicParsing(false);
        removedeviceunit.setOnAction(this::onRemoveDeviceUnit);
        
        updeviceunit = new Button();
        updeviceunit.setFocusTraversable(false);
        updeviceunit.setMnemonicParsing(false);
        updeviceunit.setOnAction(this::onUpDeviceUnit);
        
        downdeviceunit = new Button();
        downdeviceunit.setFocusTraversable(false);
        downdeviceunit.setMnemonicParsing(false);
        downdeviceunit.setOnAction(this::onDownDeviceUnit);
        
        Separator sep = new Separator(Orientation.VERTICAL);
        
        menubutton = new MenuButton(resources.getString("label.samplestitle"));
        menubutton.setFocusTraversable(false);
        
        unitstoolbar.getItems().addAll(adddeviceunit, removedeviceunit, updeviceunit, downdeviceunit, sep, menubutton);
        
        borderpanetab1.setTop(unitstoolbar);
        
        devicesunitslist = new ListView<>();
        devicesunitslist.setPrefWidth(280.0);
        BorderPane.setAlignment(devicesunitslist, Pos.CENTER);
        BorderPane.setMargin(devicesunitslist, new Insets(5.0));
        
        borderpanetab1.setLeft(devicesunitslist);
        
        tab1.setContent(borderpanetab1);
        
        Tab tab2 = new Tab(resources.getString("tab.configuration"));
        tab2.setClosable(false);
        
        ScrollPane scrolltab2 = new ScrollPane();
        scrolltab2.setFitToWidth(true);
        
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
        
        skins = new ChoiceBox<>();
        skins.setPrefWidth(200.);
        GridPane.setColumnIndex(skins, 1);
        
        clock = new CheckBox(resources.getString("label.clock"));
        clock.setMnemonicParsing(false);
        GridPane.setColumnIndex(clock, 1);
        GridPane.setRowIndex(clock, 1);
        
        grid2.getChildren().addAll(labelstyle, skins, clock);
        
        scrolltab2.setContent(grid2);
        
        tab2.setContent(scrolltab2);
                
        tabpane.getTabs().addAll(tab0, tab1, tab2);
        
        rootpane.setCenter(tabpane);
        
        initialize();
    }
    
    public void appendConnectNode(Node n) {
        connections.getChildren().add(n);
    }

    public void initialize() {

        nextbutton.setGraphic(IconBuilder.create(FontAwesome.FA_PLAY, 18.0).styleClass("icon-fill").build());

        adddeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_PLUS, 18.0).styleClass("icon-fill").build());
        removedeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_MINUS, 18.0).styleClass("icon-fill").build());
        updeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_UP, 18.0).styleClass("icon-fill").build());
        downdeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_DOWN, 18.0).styleClass("icon-fill").build());
        
        menubutton.setGraphic(IconBuilder.create(FontAwesome.FA_DASHBOARD, 18.0).styleClass("icon-fill").build());

        edittype.setItems(FXCollections.observableArrayList("Publication/Subscription", "Subscription", "Publication", "Code"));
        edittype.getSelectionModel().clearSelection();
        edittype.valueProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });

        constructSampleButtons();

        devicesunitslist.setCellFactory((ListView<TopicInfo> list) -> new ListCell<TopicInfo>() {
            @Override
            public void updateItem(TopicInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(item.getGraphic());
                    String label = item.getLabel();
                    setText((label == null || label.isEmpty()) ? resources.getString("label.empty") : label);
                }
            }
        });
        devicesunitslist.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TopicInfo> ov, TopicInfo old_val, TopicInfo new_val) -> {
            updateDevicesUnitsList();
        });
        updateDevicesUnitsList();
        
        skins.getItems().addAll(Style.values());
        skins.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> {
            if (!updating) {
                Style.changeStyle(MessageUtils.getRoot(rootpane), new_val);
            }
        });

//        Platform.runLater(host::requestFocus);
    }

    private void constructSampleButtons() {      
        menubutton.getItems().addAll(
                createSamplesButton("samples.lights", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 18.0).styleClass("icon-fill").build(), "com/adr/helloiot/samples/lights.fxml"),
                createSamplesButton("samples.chart", IconBuilder.create(FontAwesome.FA_BAR_CHART, 18.0).styleClass("icon-fill").build(), "com/adr/helloiot/samples/chart.fxml"),
                createSamplesButton("samples.numbers", IconBuilder.create(FontAwesome.FA_DASHBOARD, 18.0).styleClass("icon-fill").build(), "com/adr/helloiot/samples/numbers.fxml"),
                createSamplesButton("samples.scenes", IconBuilder.create(FontAwesome.FA_PICTURE_O, 18.0).styleClass("icon-fill").build(), "com/adr/helloiot/samples/scenes.fxml"));
    }

    public Node getNode() {
        return rootpane;
    }

    private void updateCurrentTopic() {
        if (!updating) {
            int index = devicesunitslist.getSelectionModel().getSelectedIndex();        
            TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
            String type = edittype.getValue();
            if (!topic.getType().equals(type)) {
                // This is just an optimization. Most of the times we can reuse current TopicInfo
                // Create a new TopicInfo, we cannot reuse current one
                topic = topicinfobuilder.create(type);
                
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
                // TopicInfo -> TopicInfoNode
                topic.writeToEditNode(); 
                updating = false;
            } else {      
                // TopicInfoNode -> TopicInfo
                topic.readFromEditNode();
            }

            devicesunitslist.getItems().set(index, topic);
            devicesunitslist.getSelectionModel().select(topic);
        }
    }

    private void updateDevicesUnitsList() {
        TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        if (topic == null) {
            removedeviceunit.setDisable(true);
            deviceunitform.setDisable(true);
            updeviceunit.setDisable(true);
            downdeviceunit.setDisable(true);

            updating = true;    
            edittype.getSelectionModel().clearSelection();
            if (editnode != null) {
                editnode.useUpdateCurrent(null);
                topicinfocontainer.getChildren().remove(editnode.getNode());
                editnode = null;
            }            
            updating = false;
        } else {
            removedeviceunit.setDisable(false);
            deviceunitform.setDisable(false);
            updeviceunit.setDisable(index <= 0);
            downdeviceunit.setDisable(index >= devicesunitslist.getItems().size() - 1);

            updating = true;
            edittype.getSelectionModel().select(topic.getType());
            
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
            // TopicInfo -> TopicInfoNode
            topic.writeToEditNode(); 
            updating = false;
        }
    }

    void onAddDeviceUnit(ActionEvent event) {
        TopicInfo t = topicinfobuilder.create();
        devicesunitslist.getItems().add(t);
        devicesunitslist.getSelectionModel().select(t);
    }

    void onRemoveDeviceUnit(ActionEvent event) {
        TopicInfo t = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        devicesunitslist.getItems().remove(t);
        if (devicesunitslist.getItems().size() > 0 && index < devicesunitslist.getItems().size()) {
            devicesunitslist.getSelectionModel().select(index);
        }
    }

    void onUpDeviceUnit(ActionEvent event) {
        TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        devicesunitslist.getItems().remove(index);
        devicesunitslist.getItems().add(index - 1, topic);
        devicesunitslist.getSelectionModel().select(index - 1);
    }

    void onDownDeviceUnit(ActionEvent event) {
        TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        devicesunitslist.getItems().remove(index);
        devicesunitslist.getItems().add(index + 1, topic);
        devicesunitslist.getSelectionModel().select(index + 1);
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
    
    public void setClock(boolean value) {
        clock.setSelected(value);
    }
    
    public boolean isClock() {
        return clock.isSelected();
    }

    public ObservableList<TopicInfo> getTopicInfoList() {
        return devicesunitslist.getItems();
    }

    public void setTopicInfoList(TopicInfoBuilder topicinfobuilder, ObservableList<TopicInfo> list) {
        this.topicinfobuilder = topicinfobuilder;
        devicesunitslist.setItems(list);
        if (list.size() > 0) {
            devicesunitslist.getSelectionModel().select(0);
        }
    }

    public boolean isMainPage() {
        return mainpage.isSelected();
    }

    public void setMainPage(boolean value) {
        mainpage.setSelected(value);
    }
    
    public MenuItem createSamplesButton(String key, Node graphic, String fxml) {
        MenuItem b = new MenuItem(resources.getString(key), graphic);       
        b.setOnAction(e -> {
            try {
                addCodeUnit(resources.getString(key), Resources.toString(Resources.getResource(fxml), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        return b;
    }
    
    public void addToolbarButton(MenuItem b) {
        menubutton.getItems().add(b);
    }
    
    public void addCodeUnit(String name, String code) {
        TopicInfo t = topicinfobuilder.create("Code");
        BaseSubProperties props = new BaseSubProperties();
        props.setProperty(".name", name);
        props.setProperty(".code", code);
        t.load(props);

        devicesunitslist.getItems().add(t);
        devicesunitslist.getSelectionModel().select(t);        
    }
}
