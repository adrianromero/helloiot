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

import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.TreeEvent;
import com.adr.helloiot.device.TreeStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class HelloIoTApp {
    
    private final List<Unit> units = new ArrayList<>();    
    private final List<Device> devices = new ArrayList<>();    
    
    private MQTTManager mqtthelper;
    private Properties properties;
    private File fileproperties;
    
    private HelloIoTAppPublic apppublic = null;
    private DeviceSimple unitpage;
    private DeviceSwitch beeper;
    private TransmitterSimple buzzer;
    
    public void loadNamespace(Device[] appdevices, Unit[] appunits) {
        Collections.addAll(devices, appdevices);
        Collections.addAll(units, appunits);
    }
       
    public void construct(MQTTManager mqtthelper) {
        
        this.mqtthelper = mqtthelper;
        
        // Load the properties
        properties = new Properties();
        properties.setProperty("window.height", "600.0");
        properties.setProperty("window.width", "800.0");
        properties.setProperty("window.maximized", "false");
        fileproperties = new File(System.getProperty("user.home"), ".helloiot-" + mqtthelper.getClient() + ".properties");
        try (InputStream in = new FileInputStream(fileproperties)) {            
            properties.load(in);
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }    
        
        // Construct All
        for (Unit s: units) {
            s.construct(this.getAppPublic());
        }
        for (Device d: devices) {
            d.construct(mqtthelper);
        }      
    }
    
    public void start() {
        
        File f = new File(System.getProperty("user.home"), ".helloiot-" + mqtthelper.getClient());  
        boolean fexists = f.exists();
        
        initFirstTime(fexists);        
        
        if (!fexists) {                      
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(HelloIoTApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }     
   
        for (Unit s: units) {
            s.start();
        }               
    }
    
    public void stop() {
        for (Unit s: units) {
            s.stop();
        }        
    }
    
    public void destroy() {
        
        // Save the properties...
        try (OutputStream out = new FileOutputStream(fileproperties)) {            
            properties.store(out, "HelloIoT properties");
        } catch (IOException ex) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.WARNING, ex.getMessage());
        }         
        
        // Destroy All
        for (Unit s: units) {
            s.destroy();
        }         
        for (Device d: devices) {
            d.destroy();
        }        
    }
    
    public MQTTManager getMQTTHelper() {
        return mqtthelper;
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public DeviceSimple getUnitPage() {
        if (unitpage == null) {
            unitpage = ((DeviceSimple) getDevice(SystemDevicesUnits.SYS_UNITPAGE_ID));
        }
        return unitpage;
    }
    public DeviceSwitch getBeeper() {
        if (beeper == null) {
            beeper = ((DeviceSwitch) getDevice(SystemDevicesUnits.SYS_BEEPER_ID));
        }
        return beeper;
    }
    public TransmitterSimple getBuzzer() {
        if (buzzer == null) {
            buzzer = ((TransmitterSimple) getDevice(SystemDevicesUnits.SYS_BUZZER_ID));
        }
        return buzzer;
    }
    public List<Unit> getUnits() {
        return units;
    }
    public List<Device> getDevices() {
        return devices;
    }
    public Device getDevice(String id) {
        for (Device d : devices) {
            if (id.equals(d.getId())) {
                return d;
            }
        }
        return null;
    } 
    
    public byte[] readSYSStatus(String branch) {
        return ((TreeStatus) getDevice(SystemDevicesUnits.SYS_VALUE_ID)).readStatus(branch);
    }
    public String loadSYSStatus(String branch) {
        return ((TreeStatus) getDevice(SystemDevicesUnits.SYS_VALUE_ID)).loadStatus(branch);
    }
    public void sendSYSStatus(String branch, String message) {
        ((TreeStatus) getDevice(SystemDevicesUnits.SYS_VALUE_ID)).sendStatus(branch, message);
    }
    public void sendSYSStatus(String branch, byte[] message) {
        ((TreeStatus) getDevice(SystemDevicesUnits.SYS_VALUE_ID)).sendStatus(branch, message);
    }
   public final void sendSYSEvent(String branch, String message) {
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).sendEvent(branch, message);
    }  
   public final void sendSYSEvent(String branch, byte[] message) {
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).sendEvent(branch, message);
    }  
    public void sendSYSEvent(String branch, String message, long delay) {            
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).sendEvent(branch, message, delay);
    }
    public void sendSYSEvent(String branch, byte[] message, long delay) {            
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).sendEvent(branch, message, delay);
    }
    public final void sendSYSEvent(String branch) {
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).sendEvent(branch);
    }  
    public void sendSYSEvent(String branch, long delay) {
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).sendEvent(branch, delay);
    }     
    public void cancelSYSEventTimer() {
        ((TreeEvent) getDevice(SystemDevicesUnits.SYS_EVENT_ID)).cancelTimer();
    }

    public HelloIoTAppPublic getAppPublic() {
        if (apppublic == null) {
            apppublic = new HelloIoTAppPublic(this);
        }
        return apppublic;
    }
    
    private void initFirstTime(boolean initexists) {   
        
        if (!initexists) {
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.INFO, "Executing unit page initialization.");
            getUnitPage().sendStatus("main");
            Logger.getLogger(HelloIoTApp.class.getName()).log(Level.INFO, "Finished unitpage initialization.");   
        }
    }    
}
