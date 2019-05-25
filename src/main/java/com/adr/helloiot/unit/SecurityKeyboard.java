//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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

import com.adr.fonticon.IconFontGlyph;
import com.adr.fonticon.IconBuilder;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

/**
 *
 * @author adrian
 */
public class SecurityKeyboard {

    private Node content;

    @FXML
    private Button btndelete;
    @FXML
    private PasswordField password;

    public SecurityKeyboard() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adr/helloiot/fxml/securitykeyboard.fxml"));
        loader.setController(this);

        try {
            content = loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setPassword(String value) {
        password.setText(value);
    }

    public String getPassword() {
        return password.getText();
    }

    public Node getNode() {
        return content;
    }

    @FXML
    public void initialize() {
        btndelete.setGraphic(IconBuilder.create(IconFontGlyph.FA_SOLID_TIMES_CIRCLE, 22.0).styleClass("icon-fill").build());
        password.setText("");
    }

    @FXML
    void onDelete(ActionEvent event) {
        password.setText("");
    }

    @FXML
    void onOne(ActionEvent event) {
        password.setText(password.getText() + "1");
    }

    @FXML
    void onTwo(ActionEvent event) {
        password.setText(password.getText() + "2");
    }

    @FXML
    void onThree(ActionEvent event) {
        password.setText(password.getText() + "3");
    }

    @FXML
    void onFour(ActionEvent event) {
        password.setText(password.getText() + "4");
    }

    @FXML
    void onFive(ActionEvent event) {
        password.setText(password.getText() + "5");
    }

    @FXML
    void onSix(ActionEvent event) {
        password.setText(password.getText() + "6");
    }

    @FXML
    void onSeven(ActionEvent event) {
        password.setText(password.getText() + "7");
    }

    @FXML
    void onEight(ActionEvent event) {
        password.setText(password.getText() + "8");
    }

    @FXML
    void onNine(ActionEvent event) {
        password.setText(password.getText() + "9");
    }

    @FXML
    void onZero(ActionEvent event) {
        password.setText(password.getText() + "0");
    }
}
