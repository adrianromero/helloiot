//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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

import com.adr.helloiot.scripting.Nashorn;
import com.adr.helloiot.scripting.Script;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelloPlatformDesktop extends HelloPlatform {
    
    @Override
    public String getHome() {
       return System.getProperty("HELLOIOT_HOME"); 
    }
        
    @Override
    public File getFile(String file) throws IOException {
        String home = getHome();
        String homepath = (home == null || home.isEmpty()) 
                ? System.getProperty("user.home")
                : home;   
        Path root = Paths.get(homepath, ".helloiot");
        
        Files.createDirectories(root);
        
        return root.resolve(file).toFile();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public Script getNewScript() {
        return new Nashorn();
    }
    
    @Override
    public void keepON() {
    }    
}
