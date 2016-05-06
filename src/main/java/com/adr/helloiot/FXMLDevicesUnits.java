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

import com.adr.helloiot.device.Device;
import com.adr.helloiot.unit.Unit;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author adrian
 */
public abstract class FXMLDevicesUnits implements ApplicationDevicesUnits {

    private DevicesUnits devicesunits = null;

    @Override
    public void init(Properties config) {    
        devicesunits = new DevicesUnits();
    }
    
    protected final void load(URL fxml, ResourceBundle resources) {
        
        if (fxml == null) {
            return;
        }

        // Load from FXML
        try {
            FXMLLoader loader;
            loader = new FXMLLoader(fxml);
            if (resources != null) {
                loader.setResources(resources);
            } 
            DevicesUnits du = loader.load();
            devicesunits.getDevices().addAll(du.getDevices());
            devicesunits.getUnits().addAll(du.getUnits());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } 
    }

    @Override
    public final List<Device> getDevices() {
        return devicesunits.getDevices();
    }

    @Override
    public final List<Unit> getUnits() {
        return devicesunits.getUnits();
    }
}
