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

import com.adr.helloiot.scripting.Script;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public abstract class HelloPlatform {

    private static HelloPlatform instance = null;

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
    }

    public static final HelloPlatform getInstance() {
        if (instance == null) {
            initInstance();
        }
        return instance;
    }

    public abstract File getFile(String file);

    public abstract boolean isFullScreen();

    public abstract Script getNewScript();
}
