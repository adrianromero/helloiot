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
package com.adr.helloiot.unit;

import com.adr.textflow.TextContainer;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public abstract class Tile extends BorderPane implements Unit {

    private String title = null;
    private Label titlelabel = null;
    private String footer = null;
    private FlowPane footerpane = null;

    public Tile() {
        getStyleClass().add("unitbase");
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMinSize(160.0, Control.USE_COMPUTED_SIZE);
        setPrefSize(160.0, Control.USE_COMPUTED_SIZE);
        HBox.setHgrow(this, Priority.SOMETIMES);
        setDisable(true);

        setCenter(constructContent());  
    }

    protected abstract Node constructContent();
    
    protected <T> T loadFXML(String fxml) {  
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setController(this);
        try {
            return loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } 
    }      
    
    protected <T> T loadFXML(String fxml, String resources) {  
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setController(this);
        loader.setResources(ResourceBundle.getBundle(resources));
        try {
            return loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } 
    }      

    @Override
    public void start() {
        setDisable(false);
    }

    @Override
    public void stop() {
        setDisable(true);
    }

    @Override
    public Node getNode() {
        return this;
    }

    public void setLabel(String label) {
        if (titlelabel != null) {
            getChildren().remove(titlelabel);
            titlelabel = null;
        }     
        title = label; 
        if (title != null && !title.isEmpty()) {
            titlelabel = new Label(label);
            titlelabel.getStyleClass().add("labelbase");
            titlelabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            setTop(titlelabel);
        }
    }

    public String getLabel() {
        return title;
    }

    public void setFooter(String label) {
        if (footerpane != null) {
            getChildren().remove(footerpane);
            footerpane = null;
        }
        footer = label;
        
        if (footer != null && !footer.isEmpty()) {
            try {
                footerpane = TextContainer.createFlowPane(label);
                footerpane.getStyleClass().add("footerbase");            
                setBottom(footerpane);
            } catch (IOException ex) {
                Logger.getLogger(Tile.class.getName()).log(Level.WARNING, null, ex);;
            }
        }
    }

    public String getFooter() {
        return footer;
    }
}
