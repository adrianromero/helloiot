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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javafx.application.Application.Parameters;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class MainManager {

    private HelloIoTApp helloiotapp;
    
    protected Properties getConfigProperties(Parameters params) {
        
        Properties config = new Properties();
        
        // read the configuration properties 
        List<String> unnamed = params.getUnnamed();    
        File configfile;
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
        try (InputStream in = new FileInputStream(configfile)) {            
            config.load(in);
        } catch (IOException ex) {
            throw new RuntimeException("Properties file name is not correct: " + configfile.toString());
        }
        
        // read the parameters
        config.putAll(params.getNamed());
        
        return config;
    }    
    
    public void construct(StackPane root, Parameters params) {
        helloiotapp = new HelloIoTApp(getConfigProperties(params));
        helloiotapp.getMQTTNode().setOnExitAction(event -> {
            root.getScene().getWindow().hide();            
        });
        
        root.getChildren().add(helloiotapp.getMQTTNode());
        helloiotapp.start();        
    }
    
    public void destroy() {  
        helloiotapp.stopAndDestroy();
        helloiotapp = null;
    }
}
