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
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public abstract class MainApp extends Application {

    private MainManager manager;
    private Stage stage;

    protected abstract MainManager createManager();

    protected String getAppTitle() {
        return "Hello IoT";
    }

    @Override
    public final void start(Stage stage) {

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
            HelloPlatform.getInstance().keepON();
        } else {
            // Dimension properties only managed if not fullscreen
            scene = new Scene(root);
            boolean maximized = Boolean.parseBoolean(HelloPlatform.getInstance().getProperty("window.maximized", "false"));
            if (maximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(Double.parseDouble(HelloPlatform.getInstance().getProperty("window.width", "800.0")));
                stage.setHeight(Double.parseDouble(HelloPlatform.getInstance().getProperty("window.height", "600.0")));
            }
        }

        stage.getIcons().addAll(
                new Image(MainApp.class.getResource("/com/adr/helloiot/res/mipmap-hdpi/ic_launcher.png").toExternalForm()),
                new Image(MainApp.class.getResource("/com/adr/helloiot/res/mipmap-ldpi/ic_launcher.png").toExternalForm()),
                new Image(MainApp.class.getResource("/com/adr/helloiot/res/mipmap-mdpi/ic_launcher.png").toExternalForm()),
                new Image(MainApp.class.getResource("/com/adr/helloiot/res/mipmap-xhdpi/ic_launcher.png").toExternalForm()),
                new Image(MainApp.class.getResource("/com/adr/helloiot/res/mipmap-xxhdpi/ic_launcher.png").toExternalForm()),
                new Image(MainApp.class.getResource("/com/adr/helloiot/res/mipmap-xxxhdpi/ic_launcher.png").toExternalForm()));
        stage.setScene(scene);

        // hack to avoid slider to get the focus.
        scene.focusOwnerProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (newValue != null && (newValue instanceof Slider || newValue instanceof ScrollPane)) {
                root.requestFocus();
            }
        });

        manager = createManager();
        manager.construct(root, getParameters());

        stage.setTitle(getAppTitle());
        stage.show();
    }

    @Override
    public final void stop() throws Exception {

        HelloPlatform.getInstance().setProperty("window.height", Double.toString(stage.getHeight()));
        HelloPlatform.getInstance().setProperty("window.width", Double.toString(stage.getWidth()));
        HelloPlatform.getInstance().setProperty("window.maximized", Boolean.toString(stage.isMaximized()));
        HelloPlatform.getInstance().saveAppProperties();

        manager.destroy();
        manager = null;
        stage = null;

        CompletableAsync.shutdown();
        System.exit(0);
    }
}
