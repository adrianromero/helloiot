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

import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author adrian
 */
public abstract class StringFormatPath implements StringFormat {

    private String path;

    public StringFormatPath() {
        this(null);
    }

    public StringFormatPath(String jsonpath) {
        path = jsonpath;
    }

    public final void setPath(String path) {
        this.path = path;
    }

    public final String getPath() {
        return path;
    }

    protected abstract MiniVar valueImpl(String value);
   
    @Override
    public final MiniVar value(byte[] value) {
        if (path == null || path.isEmpty()) {
            // No JSON path -> Normal payload processing
            if (value == null || value.length == 0) {
                return valueImpl(null);
            }
            return valueImpl(new String(value, StandardCharsets.UTF_8));
        } else {
            // if value null or empty this will throw an exception
            // Note that this is a different behavior when path is null because here we expect a valid JSON.
            String v = JsonPath.<String>read(new String(value, StandardCharsets.UTF_8), path);
            if (v == null || v.isEmpty()) {
                return valueImpl(null);
            }
            return valueImpl(v);
        }
    }
}
