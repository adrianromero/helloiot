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
package com.adr.helloiot.device.format;

import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatIdentity extends StringFormatPath {

    public static final StringFormat INSTANCE = new StringFormatIdentity();

    public StringFormatIdentity() {
        super();
    }

    public StringFormatIdentity(String jsonpath) {
        super(jsonpath);
    }

    @Override
    public String toString() {
        return "STRING";
    }

    @Override
    public String formatImpl(String value) {
        return value;
    }

    @Override
    public String parseImpl(String formattedvalue) {
        return formattedvalue;
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }
}
