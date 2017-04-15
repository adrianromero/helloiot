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

import com.adr.hellocommon.dialog.DialogView;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.util.CryptUtils;
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
public abstract class ButtonBase extends Tile implements Unit {

    protected Button button;
    protected boolean confirm = false;
    protected String securitykey = null;
    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/basic");

    protected HelloIoTAppPublic app;

    @Override
    public Node constructContent() {

        button = new Button();
        button.setContentDisplay(ContentDisplay.TOP);
        button.getStyleClass().add("buttonbase");
        VBox.setVgrow(button, Priority.SOMETIMES);
        button.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        button.setFocusTraversable(false);
        button.setOnAction(this::onScriptAction);
        return button;
    }

    @Override
    public void construct(HelloIoTAppPublic app) {
        Unit.super.construct(app);
        this.app = app;
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

    public void setGraphic(Node graphic) {
        button.setGraphic(graphic);
    }

    public Node getGraphic() {
        return button.getGraphic();
    }

    void onScriptAction(ActionEvent event) {
        if (confirm) {
            MessageUtils.showConfirm(MessageUtils.getRoot(this), getLabel(), resources.getString("message.confirm"), this::doSecurityAction);
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
            dialog.setTitle(getLabel());
            dialog.setContent(sec.getNode());
            dialog.setActionOK((ActionEvent evok) -> {
                if (CryptUtils.validatePassword(sec.getPassword(), app.loadSYSStatus(securitykey))) {
                    doRun(evok);
                } else {
                    evok.consume();
                    sec.setPassword("");
                    MessageUtils.showWarning(MessageUtils.getRoot(this), getLabel(), resources.getString("message.wrongpassword"));
                }
            });
            dialog.addButtons(dialog.createCancelButton(), dialog.createOKButton());
            dialog.show(MessageUtils.getRoot(this));
        }

    }

    protected abstract void doRun(ActionEvent event);
}
