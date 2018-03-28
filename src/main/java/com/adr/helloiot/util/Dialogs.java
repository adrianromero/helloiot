//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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
package com.adr.helloiot.util;

import com.adr.hellocommon.dialog.DialogView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class Dialogs {
    
    private Dialogs() {}

    public static DialogView createLoading(String label) {
        Label l = new Label(label);
        l.getStyleClass().add("dialog-label");
        l.setAlignment(Pos.CENTER);
        l.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);

        ProgressBar p = new ProgressBar();
        p.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);

        VBox box = new VBox();
        box.setSpacing(5.0);
        box.setPadding(new Insets(0.0, 0.0, 50.0, 0.0));
        box.getChildren().addAll(l, p);

        DialogView dialog = new DialogView();
        dialog.setMaster(true);
        dialog.setContent(box);
        return dialog;
    }
}
