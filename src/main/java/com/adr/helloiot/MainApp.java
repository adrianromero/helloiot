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

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.util.CompletableAsync;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public abstract class MainApp extends Application {

    private final static String APP_PROPERTIES = ".helloiot-app.properties";

    private MainManager manager; 
    private Stage stage;

    private Properties appproperties;

    protected abstract MainManager createManager();

    protected String getAppTitle() {
        return "Hello IoT";
    }

    @Override
    public final void start(Stage stage) {

        loadAppProperties();

        this.stage = stage;
        StackPane root = new StackPane();
        root.getStyleClass().add("maincontainer");

        MessageUtils.setDialogRoot(root, true);

        // Construct root graph scene
        Scene scene;
        if (HelloPlatform.getInstance().isFullScreen()) {
            Rectangle2D dimension = Screen.getPrimary().getVisualBounds();
            scene = new Scene(root, dimension.getWidth(), dimension.getHeight());
            scene.setCursor(Cursor.NONE);

        } else {
            // Dimension properties only managed if not fullscreen
            scene = new Scene(root);
            boolean maximized = Boolean.parseBoolean(appproperties.getProperty("window.maximized"));
            if (maximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(Double.parseDouble(appproperties.getProperty("window.width")));
                stage.setHeight(Double.parseDouble(appproperties.getProperty("window.height")));
            }
        }

        stage.setScene(scene);

        // hack to avoid slider to get the focus.
        scene.focusOwnerProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (newValue != null && (newValue instanceof Slider || newValue instanceof ScrollPane)) {
                root.requestFocus();
            }
        });

        manager = createManager();
        manager.construct(root, getParameters(), appproperties);

        stage.setTitle(getAppTitle());
        stage.show();
    }

    @Override
    public final void stop() throws Exception {

        appproperties.setProperty("window.height", Double.toString(stage.getHeight()));
        appproperties.setProperty("window.width", Double.toString(stage.getWidth()));
        appproperties.setProperty("window.maximized", Boolean.toString(stage.isMaximized()));
        
        manager.destroy(appproperties);
        saveAppProperties();
        
        manager = null;
        stage = null;
        
        CompletableAsync.shutdown();
    }

    private void loadAppProperties() {
        // Load the properties
        appproperties = new Properties();
        appproperties.setProperty("window.height", "600.0");
        appproperties.setProperty("window.width", "800.0");
        appproperties.setProperty("window.maximized", "false");
        try (InputStream in = new FileInputStream(HelloPlatform.getInstance().getFile(APP_PROPERTIES))) {
            appproperties.load(in);
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }
    }

    private void saveAppProperties() {
        // Save the properties...
        try (OutputStream out = new FileOutputStream(HelloPlatform.getInstance().getFile(APP_PROPERTIES))) {
            appproperties.store(out, "HelloIoT properties");
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }
    }
}
