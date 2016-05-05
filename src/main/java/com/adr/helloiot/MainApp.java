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

import com.adr.helloiot.util.CompletableAsync;
import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainApp extends Application {
    
    private final static Logger logger = Logger.getLogger(MainApp.class.getName());

    private MQTTManager mqttHelper;
    private MainController root;
    private HelloIoTApp helloiotapp;
    
    private Stage stage;
    
    protected Properties getConfigProperties() {
        
        Properties config = new Properties();
        
        // read the configuration properties 
        List<String> unnamed = getParameters().getUnnamed();    
        File configfile;
        if (unnamed.isEmpty()) {
            configfile = new File("helloiot.properties");
        } else {
            String param = unnamed.get(0);
            if (Strings.isNullOrEmpty(param)) {
                configfile = new File("helloiot.properties");
            } else {
                configfile = new File(param); 
            }
        }
        try (InputStream in = new FileInputStream(configfile)) {            
            config.load(in);
        } catch (IOException ex) {
            throw new RuntimeException("Properties file name is not correct: " + configfile.toString());
        }
        
        // read the parameters
        config.putAll(getParameters().getNamed());
        
        return config;
    }
  
    @Override
    public final void start(Stage stage) {  
        
        this.stage = stage;
        
        Properties configproperties = getConfigProperties();
                   
        Injector injector = Guice.createInjector(new AppModule(configproperties));               
        mqttHelper = injector.getInstance(MQTTManager.class);        
        helloiotapp = injector.getInstance(HelloIoTApp.class);
        helloiotapp.construct(mqttHelper);
                
        
        root = injector.getInstance(MainController.class);
        
        root.initUnitNodes(helloiotapp.getUnits());

        Scene scene = new Scene(root);
        stage.setScene(scene);
         
        // injector.getInstance(Key.get(String.class, Names.named("annotation")));
        if (root.getAppFullScreen()) {
            scene.setCursor(Cursor.NONE);
            Rectangle2D dimension = Screen.getPrimary().getBounds();
            stage.setX(dimension.getMinX());
            stage.setY(dimension.getMinY());
            stage.setWidth(dimension.getWidth());
            stage.setHeight(dimension.getHeight());
            stage.initStyle(StageStyle.UNDECORATED);
            
            root.getStylesheets().add(getClass().getResource("/com/adr/helloiot/styles/fullscreen.css").toExternalForm());            
        } else {
            boolean maximized = Boolean.parseBoolean(helloiotapp.getProperties().getProperty("window.maximized"));
            if (maximized) {
                stage.setMaximized(true);
            } else {         
                stage.setWidth(Double.parseDouble(helloiotapp.getProperties().getProperty("window.width")));
                stage.setHeight(Double.parseDouble(helloiotapp.getProperties().getProperty("window.height")));
            }
        }

        stage.setTitle(root.getAppTitle());
        stage.show();
        
        // hack to avoid slider to get the focus.
        scene.focusOwnerProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (newValue != null && newValue instanceof Slider) {
                root.requestFocus();
            }
        });

        // Start connection at the end and callback
        mqttHelper.setOnConnectionLost(t -> {
            logger.log(Level.WARNING, "Connection lost to broker.", t);
            Platform.runLater(() -> {
                helloiotapp.stop();
                restartConnection(root); 
            });                
        });
        
        restartConnection(root);
    }
        
    private void restartConnection(MainController root) {
        root.showConnecting();
        tryConnection();        
    }
    
    private void tryConnection() {
        mqttHelper.open().thenAcceptFX((v) -> {
            // success
            root.hideConnecting();
            helloiotapp.start();
        }).exceptionallyFX(ex -> {
            new Timeline(new KeyFrame(Duration.millis(2500), ev -> {
                tryConnection();
            })).play();  
            return null;
        }); 
    }
    
    @Override
    public final void stop() throws Exception {
        helloiotapp.getProperties().setProperty("window.height", Double.toString(stage.getHeight()));
        helloiotapp.getProperties().setProperty("window.width", Double.toString(stage.getWidth()));
        helloiotapp.getProperties().setProperty("window.maximized", Boolean.toString(stage.isMaximized()));
        
        // Stop subscriptions and callback    
        helloiotapp.stop();
        mqttHelper.close();
        helloiotapp.destroy();
        root.destroy();
        CompletableAsync.shutdown();
        
        this.stage = null;
    }
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }     
}
