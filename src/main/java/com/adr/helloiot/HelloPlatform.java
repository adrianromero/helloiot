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

import com.adr.helloiot.scripting.Script;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 *
 * @author adrian
 */
public abstract class HelloPlatform {

    private final static String APP_PROPERTIES = ".helloiot-app.properties";    
    private final static double MAX_PHONE_DIAGONAL = 6.5;
    
    private static HelloPlatform instance = null;
    
    private boolean phone;    
    private Properties appproperties;

    private static void initInstance() {

        String name;
        if ("android".equals(System.getProperty("javafx.platform"))) {
            name = "com.adr.helloiot.HelloPlatformAndroid";
        } else {
            name = "com.adr.helloiot.HelloPlatformDefault";
        }

        try {
            instance = (HelloPlatform) Class.forName(name).getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException
                | IllegalArgumentException
                | NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | ClassNotFoundException ex) {
            Logger.getLogger(HelloPlatform.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        instance.init();
    }

    public static final HelloPlatform getInstance() {
        if (instance == null) {
            initInstance();
        }
        return instance;
    }
    
    private void init() {
        // Init phone status
        phone = getDiagonal() <= MAX_PHONE_DIAGONAL;       
        // Load the properties
        appproperties = new Properties();     
        try (InputStream in = new FileInputStream(HelloPlatform.getInstance().getFile(APP_PROPERTIES))) {
            appproperties.load(in);
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }          
    }
    
    public double getDiagonal() {       
        Rectangle2D rect = Screen.getPrimary().getBounds();   
        double dpi = Screen.getPrimary().getDpi();
        double width = rect.getWidth() / dpi;
        double height = rect.getHeight() / dpi;
        return Math.sqrt(width * width + height * height); 
    }
    
    
    public boolean isPhone() {
        return phone;
    }

    public void saveAppProperties() {
        // Save the properties...
        try (OutputStream out = new FileOutputStream(HelloPlatform.getInstance().getFile(APP_PROPERTIES))) {
            appproperties.store(out, "HelloIoT properties");
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }
    }   
    
    public String getProperty(String key, String defaultValue) {
        return appproperties.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        appproperties.setProperty(key, value);
    }

    public abstract File getFile(String file);

    public abstract boolean isFullScreen();

    public abstract Script getNewScript();

}
