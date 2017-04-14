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

package com.adr.helloiot.unit;

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.graphic.IconStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class ButtonsSwitch extends Tile implements Unit {

    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic"); 

    private IconStatus iconbuilder;
    
    private Button goup;
    private Button godown;

    private final Map<String, Object> params = new HashMap<>();
    private ScriptCode code = null;
    
    @Override
    public Node constructContent() {   

        goup = new Button();
        goup.setContentDisplay(ContentDisplay.TOP);
        goup.getStyleClass().add("buttonbase");
        goup.getStyleClass().add("buttonup");
        VBox.setVgrow(goup, Priority.SOMETIMES);   
        goup.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        goup.setFocusTraversable(false);        
        goup.setOnAction(event -> {
            doRunSwitch(event, "ON");
        });
 
        godown = new Button();
        godown.setContentDisplay(ContentDisplay.TOP);
        godown.getStyleClass().add("buttonbase");
        godown.getStyleClass().add("buttondown");
        VBox.setVgrow(godown, Priority.SOMETIMES);   
        godown.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        godown.setFocusTraversable(false);             
        godown.setOnAction(event -> {
                doRunSwitch(event, "OFF");
        });
        
        setIconStatus(IconStatus.valueOf("TEXT/ON/OFF"));
        
        VBox content = new VBox(goup, godown);
        content.setSpacing(2);
        VBox.setVgrow(content, Priority.SOMETIMES);   
        content.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);        
        return content;       
    }
    
    @Override
    public void construct(HelloIoTAppPublic app) {
        Unit.super.construct(app);
        code.construct(app);
    }

    public Map<String, Object> getParameters() {
        return params;
    }

    public void setScriptCode(ScriptCode code) {
        this.code = code;
    }
    
    public ScriptCode getScriptCode() {
        return code;
    }
    
    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
        goup.setGraphic(iconbuilder.buildIcon("ON"));
        godown.setGraphic(iconbuilder.buildIcon("OFF"));
    }
    
    public IconStatus getIconStatus() {
        return iconbuilder;
    }     
    
    private void doRunSwitch(ActionEvent event, String status) {
        if (code == null) {
            MessageUtils.showError(MessageUtils.getRoot(this), getLabel(), resources.getString("message.nocode"));        
        } else {
            Map<String, Object> newparams = new HashMap<>();
            newparams.putAll(params);
            newparams.put("_status", status);
            code.run(newparams).exceptionallyFX((ex) -> {
                MessageUtils.showException(MessageUtils.getRoot(this), getLabel(), resources.getString("message.erroraction"), ex);
                return null;
            });  
        }        
    } 
}
