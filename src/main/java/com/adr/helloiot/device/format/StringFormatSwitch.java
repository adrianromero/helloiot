//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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

import java.nio.charset.StandardCharsets;
import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatSwitch extends StringFormatPath {

    private String[] values = {"0", "1"};
    private String[] pattern = {"OFF", "ON"};

    public StringFormatSwitch() {
        this(null);
    }

    public StringFormatSwitch(String jsonpath) {
        super(jsonpath);
    }

    public String getValues() {
        return String.join(",", values);
    }

    public void setValues(String value) {
        values = value.split(",");
    }

    public String getPattern() {
        return String.join(",", pattern);
    }

    public void setPattern(String value) {
        pattern = value.split(",");
    }

    @Override
    public String toString() {
        return "SWITCH";
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }

    @Override
    protected MiniVar valueImpl(String value) {
        if (value == null || value.isEmpty()) {
            return MiniVarBoolean.NULL;
        } else {
            return new MiniVarBoolean(values[1].equals(value));
        }
    }

    @Override
    public String format(MiniVar value) {
        if (value.isEmpty()) {
            return "";
        } else {
            return pattern[value.asBoolean() ? 1 : 0];
        }
    }

    @Override
    public MiniVar parse(String formattedvalue) {
        if (formattedvalue == null || formattedvalue.isEmpty()) {
            return MiniVarBoolean.NULL;
        } else {
            return new MiniVarBoolean(pattern[1].equals(formattedvalue));
        }
    }

    @Override
    public byte[] devalue(MiniVar formattedvalue) {
        String s = formattedvalue.asBoolean() ? values[1] : values[0];
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
