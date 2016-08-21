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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.utils.AbstractController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

/**
 *
 * @author adrian
 */
public class SecurityKeyboard extends VBox implements AbstractController {
    
    @FXML private Button btndelete;
    @FXML private PasswordField password;
    
    public SecurityKeyboard() {
        load("/com/adr/helloiot/fxml/securitykeyboard.fxml");
    }  
    
    public void setPassword(String value) {
        password.setText(value);
    }
    
    public String getPassword() {
        return password.getText();
    }

    @FXML public void initialize() {
        btndelete.setGraphic(IconBuilder.create(FontAwesome.FA_TIMES_CIRCLE_O, 32.0).build());   
        password.setText("");
    }
    
    @FXML void onDelete(ActionEvent event) {
        password.setText("");
    }

    @FXML void onOne(ActionEvent event) {
        password.setText(password.getText() + "1");
    }
    
    @FXML void onTwo(ActionEvent event) {
        password.setText(password.getText() + "2");
    }
    
    @FXML void onThree(ActionEvent event) {
        password.setText(password.getText() + "3");
    }
    
    @FXML void onFour(ActionEvent event) {
        password.setText(password.getText() + "4");
    }
    
    @FXML void onFive(ActionEvent event) {
        password.setText(password.getText() + "5");
    }
    
    @FXML void onSix(ActionEvent event) {
        password.setText(password.getText() + "6");
    }
    
    @FXML void onSeven(ActionEvent event) {
        password.setText(password.getText() + "7");
    }
    
    @FXML void onEight(ActionEvent event) {
        password.setText(password.getText() + "8");
    }

    @FXML void onNine(ActionEvent event) {
        password.setText(password.getText() + "9");
    }

    @FXML void onZero(ActionEvent event) {
        password.setText(password.getText() + "0");
    }     
}
