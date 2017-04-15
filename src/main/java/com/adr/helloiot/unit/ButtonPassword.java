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
import com.adr.helloiot.util.CryptUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.event.ActionEvent;

/**
 *
 * @author adrian
 */
public class ButtonPassword extends ButtonBase implements Unit {

    private final Map<String, Object> params = new HashMap<>();

    @Override
    protected void doRun(ActionEvent event) {

        displayPasswordDialog(resources.getString("label.newpassword"), (String p1) -> {
            displayPasswordDialog(resources.getString("label.repeatpassword"), (String p2) -> {
                if (p1.equals(p2)) {
                    app.sendSYSStatus(securitykey, CryptUtils.hashsaltPassword(p2, CryptUtils.generateSalt()));
                    MessageUtils.showInfo(MessageUtils.getRoot(this), getLabel(), resources.getString("message.passworchangesuccess"));
                } else {
                    MessageUtils.showWarning(MessageUtils.getRoot(this), getLabel(), resources.getString("message.passworchangeerror"));
                }
            });
        });
    }

    void displayPasswordDialog(String title, Consumer<String> password) {
        SecurityKeyboard sec = new SecurityKeyboard();
        DialogView dialog = new DialogView();
        dialog.setTitle(title);
        dialog.setContent(sec.getNode());
        dialog.setActionOK((ActionEvent evok) -> {
            password.accept(sec.getPassword());
        });
        dialog.addButtons(dialog.createCancelButton(), dialog.createOKButton());
        dialog.show(MessageUtils.getRoot(this));
    }
}
