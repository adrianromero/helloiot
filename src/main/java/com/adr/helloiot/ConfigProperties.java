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
package com.adr.helloiot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 *
 * @author adrian
 */
public class ConfigProperties {

    private final Properties config;
 
    public ConfigProperties() {
        config = new Properties();
    }

    public void clear() {
        config.clear();
    }

    public void load(IOSupplier<InputStream> supplierin) throws IOException {
        config.clear();
        try (InputStream in = supplierin.get()) {
            config.load(in);
        }
    }

    public void save(IOSupplier<OutputStream> supplierout) throws IOException {

        // Hack to save properties ordered.
        Properties tmp = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };
        tmp.putAll(config);
        try (OutputStream out = supplierout.get()) {
            tmp.store(out, "HelloIoT");
        }
    }

    public String getProperty(String key) {
        return config.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        if (value == null) {
            config.remove(key);
        } else {
            config.setProperty(key, value);
        }
    }
}
