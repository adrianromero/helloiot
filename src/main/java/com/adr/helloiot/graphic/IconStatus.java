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
package com.adr.helloiot.graphic;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconFontList;
import com.adr.helloiot.device.format.ValueFormatValue;
import java.util.ResourceBundle;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public interface IconStatus {

    public static final ResourceBundle RESOURCES = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

    public Node buildIcon(ValueFormatValue value);

    public static IconStatus valueOf(String value) {
        if ("NULL".equals(value)) {
            return new IconNull();
        } else if ("TEXT".equals(value)) {
            return new IconText();
        } else if ("BULB".equals(value)) {
            return new Bulb();
        } else if (value.startsWith("BULB/")) {
            return new Bulb(FontAwesome.valueOf(value.substring(5)));
        } else if (value.startsWith("BULBTEXT/")) {
            return new Bulb(new IconFontList(value.substring(9), "ROBOTO BOLD"));
        } else if ("PADLOCK".equals(value)) {
            return new Padlock();
        } else if ("TOGGLE".equals(value)) {
            return new IconToggle();
        } else if ("POWER".equals(value)) {
            return new Power();
        } else if (value.startsWith("POWER/")) {
            return new Power(FontAwesome.valueOf(value.substring(6)));
        } else if (value.startsWith("POWERTEXT/")) {
            return new Power(new IconFontList(value.substring(10), "ROBOTO BOLD"));
        } else if ("COLOR".equals(value)) {
            return new IconColor();
        } else if (value.startsWith("COLOR/")) {
            return new IconColor(FontAwesome.valueOf(value.substring(6)));
        } else if ("BRIGHTNESS".equals(value)) {
            return new IconBrightness();
        } else if (value.startsWith("BRIGHTNESS/")) {
            return new IconBrightness(FontAwesome.valueOf(value.substring(11)));      
        } else {
            throw new IllegalArgumentException("Cannot create IconStatus: " + value);
        }
    }

}
