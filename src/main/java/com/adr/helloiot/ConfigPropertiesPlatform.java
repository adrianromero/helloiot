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
package com.adr.helloiot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author adrian
 */
public class ConfigPropertiesPlatform extends ConfigProperties {

    @Override
    protected Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    protected InputStream openInputStream() throws IOException {
        return ConfigPropertiesPlatform.class.getResourceAsStream("/META-INF/.helloiot-config.properties");
    }

    @Override
    protected OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported."); // Platform does not allow to save properties
    }
    
}
