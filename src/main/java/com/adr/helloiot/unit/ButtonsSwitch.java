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
import com.adr.hellocommon.utils.AbstractController;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.device.StatusSwitch;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class ButtonsSwitch extends StackPane implements Unit, AbstractController {

    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic"); 

    private IconStatus iconbuilder;
    
    private final ButtonBase goup;
    private final ButtonBase godown;

    private final Map<String, Object> params = new HashMap<>();
    private ScriptCode code = null;
    
    public ButtonsSwitch() {   

        GridPane.setVgrow(this, Priority.SOMETIMES);
        GridPane.setHgrow(this, Priority.SOMETIMES);        
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);

        goup = new ButtonBase() {
            @Override
            protected void doRun(ActionEvent event) {
                doRunSwitch(event, StatusSwitch.ON);
            }     
        };
        VBox.setVgrow(goup, Priority.SOMETIMES);  
        goup.setContentDisplay(ContentDisplay.LEFT);
        
        godown = new ButtonBase() {
            @Override
            protected void doRun(ActionEvent event) {
                doRunSwitch(event, StatusSwitch.OFF);
            }     
        };
        VBox.setVgrow(godown, Priority.SOMETIMES);
        godown.setContentDisplay(ContentDisplay.LEFT);
        
        getChildren().add(new VBox(goup, godown));
        
        setIconStatus(IconStatus.valueOf("TEXT/ON/OFF"));
    }
    
    @Override
    public void construct(HelloIoTAppPublic app) {
        Unit.super.construct(app);
        code.construct(app);
        goup.construct(app);
        godown.construct(app);
    }

    @Override
    public void destroy() {
        Unit.super.destroy();
        goup.destroy();
        godown.destroy();
    }
    
    @Override
    public void start() {
        goup.start();
        godown.start();
    }

    @Override
    public void stop() {
        goup.stop();
        godown.stop();
    }

    @Override
    public Node getNode() {
        return this;
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
    
    public void setLabel(String value) {
        goup.setText(value);
        godown.setText(value);
    }
    
    public String getLabel() {
        return goup.getText();
    }
    
    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
        goup.setGraphic(iconbuilder.buildIcon(StatusSwitch.ON));
        godown.setGraphic(iconbuilder.buildIcon(StatusSwitch.OFF));
    }
    
    public IconStatus getIconStatus() {
        return iconbuilder;
    }     
    
    private void doRunSwitch(ActionEvent event, byte[] status) {
        if (code == null) {
            MessageUtils.showError(MessageUtils.getRoot(this), goup.getText(), resources.getString("message.nocode"));        
        } else {
            Map<String, Object> newparams = new HashMap<>();
            newparams.putAll(params);
            newparams.put("_status", status);
            code.run(newparams).exceptionallyFX((ex) -> {
                MessageUtils.showException(MessageUtils.getRoot(this), goup.getText(), resources.getString("message.erroraction"), ex);
                return null;
            });  
        }        
    } 
}
