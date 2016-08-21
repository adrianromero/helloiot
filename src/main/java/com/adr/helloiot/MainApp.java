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

package com.adr.helloiot;

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.util.CompletableAsync;
import java.io.File;
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
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class MainApp extends Application {
    
    private MainManager manager;    
    private Stage stage;
    
    private Properties appproperties;
    private File fileproperties;    
    
    protected abstract MainManager createManager();
    
    protected boolean isFullScreen() {
        return false;
    }
    
    protected String getAppTitle() {
        return "Hello IoT";
    }
    
    protected void initializeApp() {
        // Locale.setDefault(Locale.forLanguageTag("en-US"));      
    }
  
    @Override
    public final void start(Stage stage) {  
        
        this.stage = stage;
        StackPane root = new StackPane();
        MessageUtils.setDialogRoot(root, true);
        
        initializeApp();       
        loadAppProperties();    

        // Construct root graph scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
         
        // injector.getInstance(Key.get(String.class, Names.named("annotation")));
        if (isFullScreen()) {
            scene.setCursor(Cursor.NONE);
            Rectangle2D dimension = Screen.getPrimary().getBounds();
            stage.setX(dimension.getMinX());
            stage.setY(dimension.getMinY());
            stage.setWidth(dimension.getWidth());
            stage.setHeight(dimension.getHeight());
            stage.initStyle(StageStyle.UNDECORATED);
            
            root.setPadding(new Insets(15.0));
            root.getStylesheets().add(getClass().getResource("/com/adr/helloiot/styles/fullscreen.css").toExternalForm());    
        } else {
            // Dimension properties only managed if not fullscreen
            boolean maximized = Boolean.parseBoolean(appproperties.getProperty("window.maximized"));
            if (maximized) {
                stage.setMaximized(true);
            } else {         
                stage.setWidth(Double.parseDouble(appproperties.getProperty("window.width")));
                stage.setHeight(Double.parseDouble(appproperties.getProperty("window.height")));
            }
        }

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
        
        if (!isFullScreen()) {
            // Dimension properties only managed if not fullscreen
            appproperties.setProperty("window.height", Double.toString(stage.getHeight()));
            appproperties.setProperty("window.width", Double.toString(stage.getWidth()));
            appproperties.setProperty("window.maximized", Boolean.toString(stage.isMaximized()));
        }
        saveAppProperties();
        
        manager.destroy();
        
        CompletableAsync.shutdown();
        
        this.stage = null;
    }
    
    private void loadAppProperties() {
        // Load the properties
        appproperties = new Properties();
        appproperties.setProperty("window.height", "600.0");
        appproperties.setProperty("window.width", "800.0");
        appproperties.setProperty("window.maximized", "false");
        fileproperties = new File(System.getProperty("user.home"), ".helloiot-app.properties");
        try (InputStream in = new FileInputStream(fileproperties)) {            
            appproperties.load(in);
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }            
    }
    
    private void saveAppProperties() {
        // Save the properties...
        try (OutputStream out = new FileOutputStream(fileproperties)) {            
            appproperties.store(out, "HelloIoT properties");
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }           
    }    
}
