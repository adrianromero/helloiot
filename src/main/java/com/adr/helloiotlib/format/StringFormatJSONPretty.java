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
package com.adr.helloiotlib.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.nio.charset.StandardCharsets;
import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatJSONPretty extends StringFormat {

    public static final StringFormat INSTANCE = new StringFormatJSONPretty();
    
    private JsonParser parser = new JsonParser();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String toString() {
        return "JSON";
    }

    @Override
    public String format(MiniVar value) {
        if (value.isEmpty()) {
            return "";
        } else {
            try {
                return gson.toJson(parser.parse(value.asString()));
            } catch (JsonSyntaxException | JsonIOException ex) {
                return "Not a valid JSON value.";
            }
        }
    }

    @Override
    public MiniVar value(byte[] value) {
        if (value == null) {
            return MiniVarBytes.NULL;
        } else {
            return new MiniVarString(new String(value, StandardCharsets.UTF_8));
        }
    }
    
    @Override
    public MiniVar parse(String formattedvalue) {
        try {
            return new MiniVarString(gson.toJson(parser.parse(formattedvalue)));
        } catch (JsonSyntaxException | JsonIOException ex) {
            throw new IllegalArgumentException("Not a valid JSON value.");
        }
    }
    
    @Override
    public byte[] devalue(MiniVar formattedvalue) {
        return formattedvalue.asString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }
}
