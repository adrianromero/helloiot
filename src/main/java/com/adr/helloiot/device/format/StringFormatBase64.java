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

import java.util.Base64;
import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatBase64 extends StringFormat {

    public static final StringFormat INSTANCE = new StringFormatBase64();

    @Override
    public String toString() {
        return "BASE64";
    }

    @Override
    public String format(byte[] value) {
        MiniVar v = value(value);
        if (v.isEmpty()) {
            return "";
        } else {
            return v.asString();
        }
    }

    @Override
    public MiniVar value(byte[] value) {
        if (value == null) {
            return MiniVarString.NULL;
        } else {
            return new MiniVarString(Base64.getMimeEncoder().encodeToString(value));
        }
    }
    
    @Override
    public byte[] parse(String formattedvalue) {
        return devalue(new MiniVarString(formattedvalue));
    }
    
    @Override
    public byte[] devalue(MiniVar formattedvalue) {
        return Base64.getMimeDecoder().decode(formattedvalue.asString());
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }
}
