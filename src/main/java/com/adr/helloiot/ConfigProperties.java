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
package com.adr.helloiot;

import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import javafx.application.Application;

/**
 *
 * @author adrian
 */
public class ConfigProperties {
    
    private final File configfile;
    private final Properties config;
    private final Application.Parameters params;
    
    public ConfigProperties(Application.Parameters params) {
        // read the configuration properties 
        List<String> unnamed = params.getUnnamed();    
        if (unnamed.isEmpty()) {
            configfile = new File("helloiot.properties");
        } else {
            String param = unnamed.get(0);
            if (Strings.isNullOrEmpty(param)) {
                configfile = new File("helloiot.properties");
            } else {
                configfile = new File(param); 
            }
        } 
        this.params = params;
        config = new Properties();     
    }
    
    public void load() throws IOException {
        config.clear();
        try (InputStream in = new FileInputStream(configfile)) {            
            config.load(in);
        }
        config.putAll(params.getNamed());   
    }
    
    public void save() throws IOException {       
        try (OutputStream out = new FileOutputStream(configfile)) {
            config.store(out, "HelloIoT");
        }
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
