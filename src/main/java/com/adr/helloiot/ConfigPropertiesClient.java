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

import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javafx.application.Application;

/**
 *
 * @author adrian
 */
public class ConfigPropertiesClient extends ConfigProperties {
    
    private static final String CONFIG_PROPERTIES = ".helloiot-config.properties";
    
    private final File configfile;
    private final Map<String, String> named;

    public ConfigPropertiesClient(Application.Parameters params) {
        // read the configuration properties 
        List<String> unnamed = params.getUnnamed();
        if (unnamed.isEmpty()) {
            configfile = HelloPlatform.getInstance().getFile(CONFIG_PROPERTIES);
        } else {
            String param = unnamed.get(0);
            if (Strings.isNullOrEmpty(param)) {
                configfile = HelloPlatform.getInstance().getFile(CONFIG_PROPERTIES);
            } else {
                configfile = new File(param);
            }
        }
        // Get named params
        this.named = params.getNamed();
    }

    @Override
    protected Map<String, String> getParameters() {
        return named;
    }

    @Override
    protected InputStream openInputStream() throws IOException {
        return new FileInputStream(configfile);
    }

    @Override
    protected OutputStream openOutputStream() throws IOException {
        return new FileOutputStream(configfile);
    }
}
