//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
package com.adr.helloiotlib.format;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;

public class StringFormatLong extends StringFormatPath {

    private final static Logger logger = Logger.getLogger(StringFormatLong.class.getName());
    
    public static StringFormat INSTANCE = new StringFormatLong();

    public StringFormatLong(String jsonpath) {
        super(jsonpath);
    }

    public StringFormatLong() {
        super(null);
    }

    @Override
    public String toString() {
        return "LONG";
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_RIGHT;
    }

    @Override
    protected MiniVar valueImpl(String value) {
        if (value == null || value.isEmpty()) {
            return MiniVarLong.NULL;
        } else {
            try {
                return new MiniVarLong(Long.parseLong(value));
            } catch (NumberFormatException ex) {
                logger.warning(() -> "Cannot parse Long: " + value);
                return MiniVarLong.NULL;
            }
        }
    }

    @Override
    public String format(MiniVar value) {
        if (value.isEmpty()) {
            return "";
        } else {
            return Long.toString(value.asLong());
        }
    }
    
    @Override
    public MiniVar parse(String formattedvalue) {
        if (formattedvalue == null || formattedvalue.isEmpty()) {
            return MiniVarLong.NULL;
        } else {
            long l;
            try {
                l = Long.parseLong(formattedvalue);
            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, null, ex);
                throw new IllegalArgumentException(ex);
            }
            return new MiniVarLong(l);
        }
    }
    
    @Override
    public byte[] devalue(MiniVar formattedvalue) {
        return formattedvalue.asString().getBytes(StandardCharsets.UTF_8);
    }
}
