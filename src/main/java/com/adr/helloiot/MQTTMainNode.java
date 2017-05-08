//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.helloiot.unit.UnitPage;
import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
import com.adr.helloiot.device.format.StringFormatIdentity;
import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.media.ClipFactory;
import com.adr.helloiot.unit.UnitLine;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public final class MQTTMainNode {

//    @FXML private URL url;
    @FXML
    private ResourceBundle resources;
    @FXML
    private AnchorPane rootpane;
    @FXML
    private BorderPane appcontainer;
    @FXML
    private VBox container;
    @FXML
    private ListView<UnitPage> listpages;
    @FXML
    private Pane listpagesgray;
    @FXML
    private HBox headerbox;
    @FXML
    private Button menubutton;
    @FXML
    private Button backbutton;
    @FXML
    private Button exitbutton;
    @FXML
    private Label headertitle;
    @FXML
    private Label currenttime;
    @FXML
    private Label alert;
    
    private DialogView connectingdialog = null;
    
    private Clock clock = null;
    private Transition listpagestransition;
    
    private final Map<String, UnitPage> unitpages = new LinkedHashMap<>();
    private final Beeper beeper;
    private final Buzzer buzzer;
    private final HelloIoTApp app;
    private final boolean appclock;
    private final boolean appexitbutton;
    
    public MQTTMainNode(
            HelloIoTApp app,
            ClipFactory factory,
            boolean appclock,
            boolean appexitbutton) {
        
        this.app = app;
        this.appclock = appclock;
        this.appexitbutton = appexitbutton;
        FXMLUtil.load(this, "/com/adr/helloiot/fxml/main.fxml", "com/adr/helloiot/fxml/main");
        beeper = new Beeper(factory, alert);
        buzzer = new Buzzer(factory);
    }
    
    public Node getNode() {
        return rootpane;
    }
    
    public void construct(List<UnitPage> appunitpages) {
        
        app.getUnitPage().subscribeStatus(this);
        app.getBeeper().subscribeStatus(beeper);
        app.getBuzzer().subscribeStatus(buzzer);

        // Add configured unitpages.
        for (UnitPage up : appunitpages) {
            this.addUnitPage(up);
        }

        //Init unit nodes
        for (Unit u : app.getUnits()) {
            Node n = u.getNode();
            if (n != null) {
                UnitPage unitpage = buildUnitPage(UnitPage.getPage(n));
                unitpage.addUnitNode(n);
            }
        }

        // Build listpages based on unitpages
        List<UnitPage> sortedunitpages = new ArrayList<>();
        sortedunitpages.addAll(unitpages.values());
        Collections.sort(sortedunitpages);
        for (UnitPage value : sortedunitpages) {
            if (!value.isSystem() && value.getUnitLines().size() > 0 && (value.getName() == null || !value.getName().startsWith("."))) {
                listpages.getItems().add(value);
            }
        }
        
        gotoPage("start");

        // Remove menubutton if 0 or 1 visible page.
        menubutton.setVisible(!listpages.getItems().isEmpty());

        // Remove headerbox if empty
        if ((headertitle.getText() == null || headertitle.getText().equals(""))
                && clock == null
                && menubutton == null
                && exitbutton == null) {
            // There is nothing visible in the headerbox
            appcontainer.getChildren().remove(headerbox);
        }
    }
    
    public void destroy() {
        app.getUnitPage().unsubscribeStatus(this);
        app.getBeeper().unsubscribeStatus(beeper);
        app.getBuzzer().unsubscribeStatus(buzzer);
        unitpages.clear();
    }
    
    public void setToolbarButton(EventHandler<ActionEvent> backevent, Node graphic, String text) {
        backbutton.setOnAction(backevent);
        backbutton.setText(text);
        backbutton.setGraphic(graphic);
        backbutton.setVisible(backevent != null);
    }
    
    @FXML
    public void initialize() {
        
        alert.setGraphic(IconBuilder.create(FontAwesome.FA_VOLUME_UP, 72.0).fill(Color.WHITE).shine(Color.RED).build());
        
        if (appexitbutton) {
            exitbutton.setVisible(true);
            exitbutton.setGraphic(IconBuilder.create(FontAwesome.FA_POWER_OFF, 18.0).styleClass("icon-fill").build());
            exitbutton.setOnAction(ev -> {
                rootpane.getScene().getWindow().hide();
            });
        } else {
            exitbutton.setVisible(false);
            headerbox.getChildren().remove(exitbutton);
            exitbutton = null;
        }
        menubutton.setGraphic(IconBuilder.create(FontAwesome.FA_NAVICON, 18.0).styleClass("icon-fill").build());
        menubutton.setDisable(true);
        
        if (appclock) {
            clock = new Clock(currenttime, resources.getString("clock.pattern"));
            clock.play();
        }
        
        listpages.setCellFactory((ListView<UnitPage> list) -> new UnitPageCell());
        
        listpagesgray.setBackground(new Background(new BackgroundFill(Color.gray(0.5, 0.75), CornerRadii.EMPTY, Insets.EMPTY)));
        FadeTransition ft = new FadeTransition(Duration.millis(300), listpages);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.LINEAR);
        FadeTransition ft2 = new FadeTransition(Duration.millis(300), listpagesgray);
        ft2.setFromValue(0.0);
        ft2.setToValue(1.0);
        ft2.setInterpolator(Interpolator.LINEAR);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), listpages);
        tt.setFromX(-listpages.prefWidth(0));
        tt.setToX(0.0);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(300), appcontainer);
        tt2.setFromX(0.0);
        tt2.setToX(listpages.prefWidth(0));
        tt2.setInterpolator(Interpolator.EASE_BOTH);
        
        listpagestransition = new ParallelTransition(ft, ft2, tt, tt2);
        listpagestransition.setRate(-1.0);
        listpagestransition.setOnFinished((ActionEvent actionEvent) -> {
            if (listpagestransition.getCurrentTime().equals(Duration.ZERO)) {
                listpages.setVisible(false);
                listpagesgray.setVisible(false);
            }
        });
    }
    
    @FXML
    void onMenuAction(ActionEvent e) {
        animateListPages(-listpagestransition.getRate());
    }
    
    @FXML
    void onMenuHide(MouseEvent event) {
        animateListPages(-1.0);
    }
    
    @FXML
    void onSelectedItem(MouseEvent event) {
        
        UnitPage selectedpage = listpages.getSelectionModel().getSelectedItem();
        if (selectedpage != null) {
            // clicked on an item not empty space
            app.getUnitPage().sendStatus(selectedpage.getName());
        }
    }
    
    private void animateListPages(double newrate) {
        
        if (newrate > 0.0) {
            listpages.setVisible(true);
            listpagesgray.setVisible(true);
        }
        
        if (newrate > 0.0 || !listpagestransition.getCurrentTime().equals(Duration.ZERO)) {
            listpagestransition.setRate(newrate);
            listpagestransition.play();
        }
    }
    
    @Subscribe
    public void selectUnitPage(EventMessage message) {
        Platform.runLater(() -> {
            updateStatus(message.getMessage());
        });
    }
    
    private void updateStatus(byte[] status) {
        gotoPage(StringFormatIdentity.INSTANCE.format(status));
    }
    
    private void gotoPage(String status) {
        
        listpages.getSelectionModel().select(-1);
        StackPane messagesroot = MessageUtils.getRoot(rootpane);
        if (messagesroot != null) {
            // If it is not already added to the scene, there is no need to dispose dialogs.
            MessageUtils.disposeAllDialogs(messagesroot);
        }
        
        UnitPage unitpage = unitpages.get(status);
        
        if (unitpage == null) {
            unitpage = unitpages.get("notfound");
        }

        // clean everything
        container.getChildren().clear();
        
        FadeTransition s2 = new FadeTransition(Duration.millis(200), container);
        s2.setInterpolator(Interpolator.EASE_IN);
        s2.setFromValue(0.3);
        s2.setToValue(1.0);
        s2.playFromStart();

        // Initialize grid
        container.setMaxSize(unitpage.getMaxWidth(), unitpage.getMaxHeight());
        for (UnitLine line : unitpage.getUnitLines()) {
            container.getChildren().add(line.getNode());
        }
        
        headertitle.setText(unitpage.getText());

        // Set label if empty
        if (container.getChildren().isEmpty()) {
            
            container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            
            Label l = new Label();
            l.setText(unitpage.getEmptyLabel());
            l.setAlignment(Pos.CENTER);
            l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            l.getStyleClass().add("emptypanel");
            VBox.setVgrow(l, Priority.SOMETIMES);
            container.getChildren().add(l);
        }

        // Modify panel if system
        if (menubutton != null) {
            menubutton.setDisable(unitpage.isSystem());
        }
        
        animateListPages(-1.0);
    }
    
    public void showConnecting() {
        if (connectingdialog == null) {
            Label l = new Label(resources.getString("message.waitingconnection"));
            l.setAlignment(Pos.CENTER);
            l.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
            
            ProgressBar p = new ProgressBar();
            p.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
            
            VBox box = new VBox();
            box.setSpacing(5.0);
            box.setPadding(new Insets(0.0, 0.0, 50.0, 0.0));
            box.getChildren().addAll(l, p);
            
            connectingdialog = new DialogView();
            connectingdialog.setMaster(true);
            connectingdialog.setContent(box);
            connectingdialog.show(MessageUtils.getRoot(rootpane));
        }
    }
    
    public void hideConnecting() {
        if (connectingdialog != null) {
            connectingdialog.dispose();
            connectingdialog = null;
        }
    }
    
    private void addUnitPage(UnitPage unitpage) {
        unitpages.put(unitpage.getName(), unitpage);
    }
    
    private UnitPage buildUnitPage(String name) {
        UnitPage unitpage = unitpages.get(Strings.isNullOrEmpty(name) ? "main" : name);
        if (unitpage == null) {
            unitpage = new UnitPage(name, IconBuilder.create(FontAwesome.FA_CUBES, 24.0).build(), name);
            addUnitPage(unitpage);
        }
        return unitpage;
    }
    
    private static class UnitPageCell extends ListCell<UnitPage> {
        
        @Override
        public void updateItem(UnitPage item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                Label l = new Label();
                l.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                l.setAlignment(Pos.CENTER);
                l.setGraphic(item.getGraphic());
                l.setPrefSize(45.0, 40.0);
                setGraphic(l);
                setText(item.getText());
            }
        }
    }
}
