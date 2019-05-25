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

import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.unit.UnitPage;
import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFontGlyph;
import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiotlib.format.StringFormatIdentity;
import com.adr.helloiotlib.unit.Unit;
import com.adr.helloiot.media.ClipFactory;
import com.adr.helloiotlib.unit.Units;
import com.adr.helloiot.util.Dialogs;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
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
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class MainNode {

    private final ResourceBundle resources;
    private AnchorPane rootpane;
    private BorderPane appcontainer;
    private ScrollPane scrollpages;
    private VBox menupages;
    private Pane listpagesgray;
    private HBox headerbox;
    private Button menubutton;
    private Button exitbutton;
    private Label headertitle;
    private Label currenttime;
    private VBox alert;
    private Animation alertanimation;
    
    private DialogView connectingdialog = null;
    
    private Clock clock = null;
    private Transition listpagestransition;
    
    private String firstmenupage;
    
    private final Map<String, UnitPage> unitpages = new LinkedHashMap<>();
    private final Object messagePageHandler = Units.messageHandler(this::updatePageStatus);  
    private final Beeper beeper;
    private final Buzzer buzzer;
    private final HelloIoTApp app;
    private final boolean appclock;
    private final boolean appexitbutton;
    private Button backbutton;

    private final DeviceSimple appunitpage;
    private final DeviceSwitch appbeeper;
    private final DeviceSimple appbuzzer;    
    
    private String currentpage = null;
    
    public MainNode(
            HelloIoTApp app,
            ClipFactory factory,
            boolean appclock,
            boolean appexitbutton) {
        
        this.app = app;
        this.appclock = appclock;
        this.appexitbutton = appexitbutton;
        
        appunitpage = ((DeviceSimple) app.getDevice(IoTApp.SYS_UNITPAGE_ID));
        appbeeper = ((DeviceSwitch) app.getDevice(IoTApp.SYS_BEEPER_ID));
        appbuzzer = ((DeviceSimple) app.getDevice(IoTApp.SYS_BUZZER_ID));
        
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main"); 
        load();
        initialize();
        
        beeper = new Beeper(factory, alert, alertanimation);
        buzzer = new Buzzer(factory);
        backbutton = null;
    }
    
    private void load() {
        rootpane = new AnchorPane();
        
        AnchorPane anchormain = new AnchorPane();
        AnchorPane.setTopAnchor(anchormain, 0.0);
        AnchorPane.setLeftAnchor(anchormain, 0.0);
        AnchorPane.setBottomAnchor(anchormain, 0.0);
        AnchorPane.setRightAnchor(anchormain, 0.0);        
        
        appcontainer = new BorderPane();
        AnchorPane.setTopAnchor(appcontainer, 0.0);
        AnchorPane.setLeftAnchor(appcontainer, 0.0);
        AnchorPane.setBottomAnchor(appcontainer, 0.0);
        AnchorPane.setRightAnchor(appcontainer, 0.0); 
        
        headerbox = new HBox();
        headerbox.getStyleClass().add("header");
        BorderPane.setAlignment(headerbox, Pos.CENTER);
        
        menubutton = new Button();
        menubutton.setId("menubutton");
        menubutton.setFocusTraversable(false);
        menubutton.setMnemonicParsing(false);
        menubutton.setOnAction(this::onMenuAction);
        menubutton.getStyleClass().add("headerbutton");
        
        headertitle = new Label();
        headertitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        headertitle.getStyleClass().add("headertitle");
        HBox.setHgrow(headertitle, Priority.SOMETIMES);
        
        currenttime = new Label();
        currenttime.setMaxHeight(Double.MAX_VALUE);
        currenttime.getStyleClass().add("currenttime");
        
        exitbutton = new Button();
        exitbutton.setId("exitbutton");
        exitbutton.setFocusTraversable(false);
        exitbutton.setMnemonicParsing(false);
        exitbutton.getStyleClass().add("headerbutton");    
        exitbutton.setVisible(false);
        
        headerbox.getChildren().addAll(menubutton, headertitle, currenttime, exitbutton);
        
        appcontainer.setTop(headerbox);
        
        listpagesgray = new Pane();
        AnchorPane.setTopAnchor(listpagesgray, 0.0);
        AnchorPane.setLeftAnchor(listpagesgray, 0.0);
        AnchorPane.setBottomAnchor(listpagesgray, 0.0);
        AnchorPane.setRightAnchor(listpagesgray, 0.0);         
        listpagesgray.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        listpagesgray.setOnMouseClicked(this::onMenuHide);
        listpagesgray.setVisible(false);
        
        menupages = new VBox();
        
        scrollpages = new ScrollPane(menupages);
        AnchorPane.setTopAnchor(scrollpages, 0.0);
        AnchorPane.setLeftAnchor(scrollpages, 0.0);
        AnchorPane.setBottomAnchor(scrollpages, 0.0); 
        scrollpages.setPrefWidth(250.0);        
        scrollpages.setFitToWidth(true);
        scrollpages.setFocusTraversable(false);
        scrollpages.setVisible(false);
        
        anchormain.getChildren().addAll(appcontainer, listpagesgray, scrollpages);
                
        Text icon1 = IconBuilder.create(IconFontGlyph.FA_SOLID_VOLUME_UP, 72.0).apply(s -> {
            s.setFill(Color.WHITE);
            s.setStroke(Color.GRAY);
            s.setStrokeWidth(1.0);
        }).build();
        icon1.setScaleX(-1.0);
        Text icon2 = IconBuilder.create(IconFontGlyph.FA_SOLID_VOLUME_UP, 72.0).apply(s -> {
            s.setFill(Color.WHITE);
            s.setStroke(Color.GRAY);
            s.setStrokeWidth(1.0);
        }).build();
        HBox icons = new HBox(icon1, icon2);
        icons.setAlignment(Pos.CENTER);
        icons.setSpacing(10.0);
       
        alert = new VBox(icons);
        alert.getStyleClass().add("alert");
        alert.setPadding(new Insets(50.0));
        AnchorPane.setTopAnchor(alert, 0.0);
        AnchorPane.setBottomAnchor(alert, 0.0);
        AnchorPane.setLeftAnchor(alert, 0.0);
        AnchorPane.setRightAnchor(alert, 0.0);         

        alert.setMouseTransparent(true);
        alert.setVisible(false);
               
        ScaleTransition sizetransition = new ScaleTransition(Duration.millis(2000), icons);
        sizetransition.setCycleCount(Animation.INDEFINITE);
        sizetransition.setToX(2.0);
        sizetransition.setToY(2.0);
        
        FadeTransition fadetransition = new FadeTransition(Duration.millis(1000), icons);
        fadetransition.setCycleCount(Animation.INDEFINITE);
        fadetransition.setAutoReverse(true);
        fadetransition.setFromValue(0.0);
        fadetransition.setToValue(1.0);
        
        alertanimation = new ParallelTransition(sizetransition, fadetransition);
        
        rootpane.getChildren().addAll(anchormain, alert);
    }
    
    public Node getNode() {
        return rootpane;
    }
    
    public void construct(List<UnitPage> appunitpages) {
        
        appunitpage.subscribeStatus(messagePageHandler);
        appbeeper.subscribeStatus(beeper.getMessageHandler());
        appbuzzer.subscribeStatus(buzzer.getMessageHandler());

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
        firstmenupage = null;
        for (UnitPage value : sortedunitpages) {
            value.buildNode();
            if (!value.isSystem() && !value.isEmpty() && (value.getName() == null || !value.getName().startsWith("."))) {
                Label l = new Label();
                l.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                l.setAlignment(Pos.CENTER);
                l.setGraphic(value.getGraphic());
                l.setPrefSize(45.0, 40.0);                
                Button buttonmenu = new Button(value.getText(), l);
                buttonmenu.getStyleClass().add("menubutton");
                buttonmenu.setAlignment(Pos.BASELINE_LEFT);
                buttonmenu.setMaxWidth(Double.MAX_VALUE);
                buttonmenu.setFocusTraversable(false);
                buttonmenu.setMnemonicParsing(false);
                buttonmenu.setOnAction(e -> {
                    appunitpage.sendStatus(value.getName());              
                });
                menupages.getChildren().add(buttonmenu); // Last button is disconnect button
                if (firstmenupage == null) {
                    firstmenupage = value.getName();
                }
            }
        }
        
        // Add backbutton
        if (backbutton != null && backbutton.isVisible()) {
            menupages.getChildren().add(new Separator(Orientation.HORIZONTAL));
            menupages.getChildren().add(backbutton);
        }
        
        gotoPage("start");

        // Remove menubutton if 0 or 1 visible page.
        menubutton.setVisible(!menupages.getChildren().isEmpty());

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
        appunitpage.unsubscribeStatus(messagePageHandler);
        appbeeper.unsubscribeStatus(beeper.getMessageHandler());
        appbuzzer.unsubscribeStatus(buzzer.getMessageHandler());
        unitpages.clear();
    }

    public void start() {
        appcontainer.setDisable(false);
    }
    
    public void stop() {
        appcontainer.setDisable(true);
    }
    
    public void setToolbarButton(EventHandler<ActionEvent> backevent, Node graphic, String text) {
        
        Label l = new Label();
        l.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        l.setAlignment(Pos.CENTER);
        l.setGraphic(graphic);
        l.setPrefSize(45.0, 40.0);             
        
        backbutton = new Button(text, l);     
        backbutton.setAlignment(Pos.BASELINE_LEFT);
        backbutton.setMaxWidth(Double.MAX_VALUE);
        backbutton.setFocusTraversable(false);
        backbutton.setMnemonicParsing(false);
        backbutton.getStyleClass().add("menubutton");
        backbutton.setOnAction(backevent);
        backbutton.setVisible(backevent != null);
    }
    
    public void initialize() {

        if (appexitbutton) {
            exitbutton.setVisible(true);
            exitbutton.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_POWER_OFF, 18.0).styleClass("icon-fill").build());
            exitbutton.setOnAction(ev -> {
                rootpane.getScene().getWindow().hide();
            });
        } else {
            exitbutton.setVisible(false);
            headerbox.getChildren().remove(exitbutton);
            exitbutton = null;
        }
        menubutton.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_BARS, 18.0).styleClass("icon-fill").build());
        menubutton.setDisable(true);
        
        if (appclock) {
            clock = new Clock(currenttime, resources.getString("clock.pattern"));
            clock.play();
        }
        
        listpagesgray.setBackground(new Background(new BackgroundFill(Color.gray(0.5, 0.75), CornerRadii.EMPTY, Insets.EMPTY)));
        FadeTransition ft = new FadeTransition(Duration.millis(300), scrollpages);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.LINEAR);
        FadeTransition ft2 = new FadeTransition(Duration.millis(300), listpagesgray);
        ft2.setFromValue(0.0);
        ft2.setToValue(1.0);
        ft2.setInterpolator(Interpolator.LINEAR);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), scrollpages);
        tt.setFromX(-scrollpages.prefWidth(0));
        tt.setToX(0.0);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(300), appcontainer);
        tt2.setFromX(0.0);
        tt2.setToX(scrollpages.prefWidth(0));
        tt2.setInterpolator(Interpolator.EASE_BOTH);
        
        listpagestransition = new ParallelTransition(ft, ft2, tt, tt2);
        listpagestransition.setRate(-1.0);
        listpagestransition.setOnFinished((ActionEvent actionEvent) -> {
            if (listpagestransition.getCurrentTime().equals(Duration.ZERO)) {
                scrollpages.setVisible(false);
                listpagesgray.setVisible(false);
            }
        });
    }
    
    void onMenuAction(ActionEvent e) {
        animateListPages(-listpagestransition.getRate());
    }
    
    void onMenuHide(MouseEvent event) {
        animateListPages(-1.0);
    }
    
    private void animateListPages(double newrate) {
        
        if (newrate > 0.0) {
            scrollpages.setVisible(true);
            listpagesgray.setVisible(true);
        }
        
        if (newrate > 0.0 || !listpagestransition.getCurrentTime().equals(Duration.ZERO)) {
            listpagestransition.setRate(newrate);
            listpagestransition.play();
        }
    }
       
    private void updatePageStatus(byte[] status) {
        gotoPage(StringFormatIdentity.INSTANCE.format(StringFormatIdentity.INSTANCE.value(status)));
    }
    
    private void gotoPage(String status) {
        
        StackPane messagesroot = MessageUtils.getRoot(rootpane);
        if (messagesroot != null) {
            // If it is not already added to the scene, there is no need to dispose dialogs.
            MessageUtils.disposeAllDialogs(messagesroot);
        }
        
        UnitPage unitpage = unitpages.get(status);
        
        if (unitpage == null && "_first".equals(status)) {
            unitpage = unitpages.get(firstmenupage);
        }
        
        if (unitpage == null) {
            unitpage = unitpages.get("notfound");
        }
        
        // Hide Menu
        animateListPages(-1.0);
        
        // Check if page to display is already displayed
        if (unitpage.getName().equals(currentpage)) {
            return; 
        }

        // Sets currentpage
        currentpage = unitpage.getName();
        
        // Display selected page everything
        appcontainer.setCenter(unitpage.getNode());
        unitpage.showNode();
        
        headertitle.setText(unitpage.getText());
 
        // Modify panel if system
        if (menubutton != null) {
            menubutton.setDisable(unitpage.isSystem());
        }
    }
    
    public void showConnecting() {
        if (connectingdialog == null) {
            connectingdialog = Dialogs.createLoading();
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
            IconFontGlyph fa;
            String label;
            int i = name.indexOf("//");
            if (i > 0) {
                fa = IconFontGlyph.valueOf(name.substring(0, i));
                label = name.substring(i + 2);
            } else {
                fa = IconFontGlyph.FA_SOLID_CUBES;
                label = name;                
            }

            unitpage = new UnitPage(name, IconBuilder.create(fa, 24.0).styleClass("icon-fill").build(), label);
            addUnitPage(unitpage);
        }
        return unitpage;
    }
}
