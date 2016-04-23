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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class PropertiesDevicesUnits extends FXMLDevicesUnits {

    protected URL getConfigApplication(String filedescriptor) {
        try {
            return new File(filedescriptor + ".fxml").toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDevicesUnits.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    protected ResourceBundle getConfigResources(String filedescriptor) {
        try {            
            File file = new File(filedescriptor);
            URL[] urls = {file.getAbsoluteFile().getParentFile().toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            return ResourceBundle.getBundle(file.getName(), Locale.getDefault(), loader);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDevicesUnits.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }    

    @Override
    public void init(Properties config) {    
        super.init(config);
        
        String devproperty = config.getProperty("devicesunits");
        if (Strings.isNullOrEmpty(devproperty)) {
            return;
        }
        
        for (String s: devproperty.split(",")) {
            load(getConfigApplication(s), getConfigResources(s));    
        }
    }
}
