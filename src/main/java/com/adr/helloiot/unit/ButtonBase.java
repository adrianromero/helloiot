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

import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.util.CryptUtils;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public abstract class ButtonBase extends Button implements Unit {

    protected boolean confirm = false;
    protected String securitykey = null;
    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic"); 
            
    protected HelloIoTAppPublic app;
           
    public ButtonBase() {
        
        setContentDisplay(ContentDisplay.TOP);
        this.getStyleClass().add("buttonbase");
        GridPane.setVgrow(this, Priority.SOMETIMES);
        GridPane.setHgrow(this, Priority.SOMETIMES);        
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setFocusTraversable(false);
        setDisable(true);
        setOnAction(this::onScriptAction);
    }
    
    @Override
    public void construct(HelloIoTAppPublic app) {
        Unit.super.construct(app);
        this.app = app;
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
    
    public void setConfirm(boolean value) {
        confirm = value;
    }
    
    public boolean getConfirm() {
        return confirm;
    }
    
    public void setSecurityKey(String value) {
        securitykey = value;
    }
    
    public String getSecurityKey() {
        return securitykey;
    }            
    
    void onScriptAction(ActionEvent event) {
        if (confirm) {
            MessageUtils.showConfirm(MessageUtils.getRoot(this), getText(), resources.getString("message.confirm"), this::doSecurityAction);    
        } else {
            doSecurityAction(event);
        }
    }
    
    void doSecurityAction(ActionEvent event) {
        if (securitykey == null) {
            doRun(event);
        } else {
            SecurityKeyboard sec = new SecurityKeyboard();
            DialogView dialog = new DialogView();
            dialog.setTitle(this.getText());
            dialog.setContent(sec);
            dialog.setActionOK((ActionEvent evok) -> {
                if (CryptUtils.validatePassword(sec.getPassword(), app.loadSYSStatus(securitykey))) {
                    doRun(evok);
                } else {
                    evok.consume();
                    sec.setPassword("");
                    MessageUtils.showWarning(MessageUtils.getRoot(this), this.getText(), resources.getString("message.wrongpassword"));
                }
            });
            dialog.addButtons(dialog.createCancelButton(), dialog.createOKButton());
            dialog.show(MessageUtils.getRoot(this));               
        }
        
    }
    
    protected abstract void doRun(ActionEvent event);
}
