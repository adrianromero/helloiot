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
package com.adr.helloiot.tradfri;

import com.adr.hellocommon.utils.FXMLUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author adrian
 */
public class FindTradfri {
    
    private Node root;
    @FXML private Label host;
    @FXML private TextField psk;    

    public FindTradfri(String host) {
        root = FXMLUtil.load(this, "/com/adr/helloiot/fxml/findtradfri.fxml", "com/adr/helloiot/fxml/findtradfri");
        this.host.setText(host);
    }
    
    public String getPSK() {
        return psk.getText();
    }
    
    public Node getNode() {
        return root;
    }
}
