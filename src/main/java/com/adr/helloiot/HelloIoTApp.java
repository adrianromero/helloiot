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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.TreeEvent;
import com.adr.helloiot.device.TreeStatus;
import com.adr.helloiot.media.SilentClipFactory;
import com.adr.helloiot.media.StandardClipFactory;
import com.adr.helloiot.unit.UnitPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class HelloIoTApp {
        
    private final static Logger LOGGER = Logger.getLogger(HelloIoTApp.class.getName());
    
    private final List<Unit> units = new ArrayList<>();    
    private final List<Device> devices = new ArrayList<>();    
    
    private final MQTTManager mqttmanager;
    private final MQTTMainNode mqttnode;
    
    private HelloIoTAppPublic apppublic = null;
    private DeviceSimple unitpage;
    private DeviceSwitch beeper;
    private TransmitterSimple buzzer;
    
    public HelloIoTApp(Properties configproperties) {

        // Configuration
        Properties properties = new Properties();
        // default values    
        properties.setProperty("app.exitbutton", "false");
        properties.setProperty("app.clock", "true"); // do not show clock

        properties.setProperty("mqtt.url", "tcp://localhost:1883");
        properties.setProperty("mqtt.username", "");
        properties.setProperty("mqtt.password", "");
        properties.setProperty("mqtt.connectiontimeout", "30");
        properties.setProperty("mqtt.keepaliveinterval", "60");
        properties.setProperty("mqtt.defaultqos", "1");
        properties.setProperty("mqtt.topicprefix", "");
        properties.setProperty("mqtt.topicapp", "_LOCAL_/_sys_helloIoT/mainapp");
        
        properties.setProperty("devicesunits", ""); // do not load any fxml
        
        properties.putAll(configproperties);
               
        // External services
        List<UnitPage> appunitpages = new ArrayList<>();
        List<Device> appdevices = new ArrayList<>();
        List<Unit> appunits = new ArrayList<>();
        
        ServiceLoader<ApplicationUnitPages> unitpagesloader = ServiceLoader.load(ApplicationUnitPages.class);
        unitpagesloader.forEach(c -> {
            c.init(properties);
            appunitpages.addAll(c.getUnitPages());
        });
        // Add "main" unit page if needed
        if (!appunitpages.stream().anyMatch(p -> "main".equals(p.getName()))) {
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
            UnitPage main = new UnitPage("main", IconBuilder.create(FontAwesome.FA_HOME, 24.0).build(), resources.getString("page.main"));
            main.setOrder(0);     
            appunitpages.add(main);
        }
         
        ServiceLoader<ApplicationDevicesUnits> devicesunitsloader = ServiceLoader.load(ApplicationDevicesUnits.class);
        devicesunitsloader.forEach(c -> {
            c.init(properties);
            appdevices.addAll(c.getDevices());
            appunits.addAll(c.getUnits());                 
        });
        
        devices.addAll(appdevices);
        units.addAll(appunits);
        
        // MQTT Manager   
        mqttmanager = new MQTTManager(
                properties.getProperty("mqtt.url"),
                properties.getProperty("mqtt.username"), 
                properties.getProperty("mqtt.password"),
                Integer.parseInt(properties.getProperty("mqtt.connectiontimeout")), 
                Integer.parseInt(properties.getProperty("mqtt.keepaliveinterval")),
                Integer.parseInt(properties.getProperty("mqtt.defaultqos")), 
                null, 
                properties.getProperty("mqtt.topicprefix"), 
                properties.getProperty("mqtt.topicapp"));
        mqttmanager.setOnConnectionLost(t -> {
            LOGGER.log(Level.WARNING, "Connection lost to broker.", t);
            Platform.runLater(() -> {
                stopUnits();
                start(); 
            });                
        });        

        mqttnode = new MQTTMainNode(
                this, 
                Platform.isSupported(ConditionalFeature.MEDIA) ? new StandardClipFactory(): new SilentClipFactory(),
                appunitpages.toArray(new UnitPage[appunitpages.size()]),
                properties.getProperty("app.clock"),
                properties.getProperty("app.exitbutton"));
        
        // Construct All
        for (Unit s: units) {
            s.construct(this.getAppPublic());
        }
        for (Device d: devices) {
            d.construct(mqttmanager);
        }  
    }
    
    public void start() {
        mqttnode.showConnecting();
        tryConnection();                 
    }
    
    private void tryConnection() {
        mqttmanager.open().thenAcceptFX((v) -> {
            // success
            mqttnode.hideConnecting();
            startUnits();
        }).exceptionallyFX(ex -> {
            new Timeline(new KeyFrame(Duration.millis(2500), ev -> {
                tryConnection();
            })).play();  
            return null;
        }); 
    }
    
    
    public void stopAndDestroy() {
        stopUnits();
        mqttmanager.close();
                
        // Destroy all units
        for (Unit s: units) {
            s.destroy();
        }         
        for (Device d: devices) {
            d.destroy();
        }        
    }
    
    private void startUnits() {
        
        initFirstTime(mqttmanager.isFreshClient());          
   
        for (Unit s: units) {
            s.start();
        }               
    }
    
    private void stopUnits() {
        for (Unit s: units) {
            s.stop();
        }        
    }

    public MQTTMainNode getMQTTNode() {
        return mqttnode;
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
