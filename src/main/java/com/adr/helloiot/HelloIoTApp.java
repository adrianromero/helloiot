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
import com.adr.hellocommon.dialog.MessageUtils;
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
import com.adr.helloiot.util.CryptUtils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class HelloIoTApp {
        
    private final static Logger LOGGER = Logger.getLogger(HelloIoTApp.class.getName());
    
    public final static String SYS_HELLOIOT = "_sys_helloIoT/";
    public final static String SYS_VALUE_TOPIC = SYS_HELLOIOT + "sysvalue" ;
    public final static String SYS_VALUE_ID = "SYSVALUESID" ;   
    public final static String SYS_EVENT_TOPIC = SYS_HELLOIOT + "sysevent" ;   
    public final static String SYS_EVENT_ID = "SYSEVENTSID" ;  
      
    public final static String SYS_UNITPAGE_ID = "SYSUNITPAGEID";
    public final static String SYS_BEEPER_ID = "SYSBEEPERID";
    public final static String SYS_BUZZER_ID = "SYSBUZZERID";    
    
    private final List<Unit> appunits = new ArrayList<>();    
    private final List<Device> appdevices = new ArrayList<>();    
    
    private final ApplicationConfig config;
    private final MQTTManager mqttmanager;
    private final MQTTMainNode mqttnode;
    private final ResourceBundle resources;
    
    private HelloIoTAppPublic apppublic = null;
    private DeviceSimple unitpage;
    private DeviceSwitch beeper;
    private TransmitterSimple buzzer;
    
    private EventHandler<ActionEvent> exitevent = null;
    private final Runnable styleConnection;
    
    public HelloIoTApp(ApplicationConfig config) {
        
        this.config = config;

        // Load resources
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");      
        
        // System devices units
        addSystemDevicesUnits(config.mqtt_topicapp);
        
        // MQTT Manager   
        mqttmanager = new MQTTManager(
                config.mqtt_url,
                config.mqtt_username, 
                config.mqtt_password,
                config.mqtt_connectiontimeout, 
                config.mqtt_keepaliveinterval,
                config.mqtt_defaultqos, 
                config.mqtt_version,
                config.mqtt_cleansession,
                null, 
                config.mqtt_topicprefix);
        mqttmanager.setOnConnectionLost(t -> {
            LOGGER.log(Level.WARNING, "Connection lost to broker.", t);
            Platform.runLater(() -> {
                stopUnits();
                startAndConstruct(); 
            });                
        });      
        
        styleConnection = config.app_retryconnection ? this::tryConnection : this::oneConnection;

        mqttnode = new MQTTMainNode(
                this, 
                Platform.isSupported(ConditionalFeature.MEDIA) ? new StandardClipFactory(): new SilentClipFactory(),
                config.app_clock,
                config.app_exitbutton);
    }
    
    public void addDevicesUnits(List<Device> devices, List<Unit> units) {
        appdevices.addAll(devices);
        appunits.addAll(units);         
    }
    
    public void addServiceDevicesUnits() {
        ServiceLoader<ApplicationDevicesUnits> devicesunitsloader = ServiceLoader.load(ApplicationDevicesUnits.class);
        devicesunitsloader.forEach(c -> {
            addDevicesUnits(c.getDevices(), c.getUnits());              
        });        
    }
    
    private void addSystemDevicesUnits(String topicapp) {
        TreeEvent sysevents = new TreeEvent();
        sysevents.setTopic(SYS_EVENT_TOPIC);
        sysevents.setId(SYS_EVENT_ID);
        
        TreeStatus sysstatus = new TreeStatus();
        sysstatus.setTopic(SYS_VALUE_TOPIC);
        sysstatus.setId(SYS_VALUE_ID);
     
        DeviceSimple unitpage = new DeviceSimple();
        unitpage.setTopic(topicapp + "/unitpage");
        unitpage.setId(SYS_UNITPAGE_ID);
         
        DeviceSwitch beeper = new DeviceSwitch();
        beeper.setTopic(topicapp + "/beeper");
        beeper.setId(SYS_BEEPER_ID);
        
        TransmitterSimple buzzer = new TransmitterSimple();
        buzzer.setTopic(topicapp + "/buzzer");
        buzzer.setId(SYS_BUZZER_ID);
    
        addDevicesUnits(Arrays.asList(sysevents, sysstatus, unitpage, beeper, buzzer), Collections.emptyList());        
    }
    
    public void addFXMLFileDevicesUnits(String filedescriptor) {
        
        try {
            URL fxmlurl = new File(filedescriptor + ".fxml").toURI().toURL();
            
            ResourceBundle fxmlresources;
            try {
                File file = new File(filedescriptor);
                URL[] urls = {file.getAbsoluteFile().getParentFile().toURI().toURL()};
                ClassLoader loader = new URLClassLoader(urls);
                fxmlresources = ResourceBundle.getBundle(file.getName(), Locale.getDefault(), loader);  
            } catch (MalformedURLException | MissingResourceException ex) {
                Logger.getLogger(HelloIoTApp.class.getName()).log(Level.SEVERE, null, ex);
                fxmlresources = null;
            }               

            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(fxmlurl);
            if (fxmlresources != null) {
                fxmlloader.setResources(fxmlresources);
            } 
            DevicesUnits du = fxmlloader.load();
            addDevicesUnits(du.getDevices(), du.getUnits());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }     
    }
    
    public void setOnExitAction(EventHandler<ActionEvent> exitevent) {
        this.exitevent = exitevent;
        mqttnode.setOnExitAction(exitevent);
    }    
    
    public void startAndConstruct() {
        
        // External services
        List<UnitPage> appunitpages = new ArrayList<>();
        
        ServiceLoader<ApplicationUnitPages> unitpagesloader = ServiceLoader.load(ApplicationUnitPages.class);
        unitpagesloader.forEach(c -> {
            appunitpages.addAll(c.getUnitPages());
        });
        // Add "main" unit page if needed
        if (!appunitpages.stream().anyMatch(p -> "main".equals(p.getName()))) {
            UnitPage main = new UnitPage("main", IconBuilder.create(FontAwesome.FA_HOME, 24.0).build(), resources.getString("page.main"));
            main.setOrder(0);     
            appunitpages.add(main);
        }        
        mqttnode.construct(appunitpages);
        
        // Construct All
        for (Unit s: appunits) {
            s.construct(this.getAppPublic());
        }
        for (Device d: appdevices) {
            d.construct(mqttmanager);
        }  
        
        mqttnode.showConnecting();
        styleConnection.run();
    }
    
    private void oneConnection() {
        mqttmanager.open().thenAcceptFX((v) -> {
            // success
            mqttnode.hideConnecting();
            startUnits();
        }).exceptionallyFX(ex -> {
            mqttnode.hideConnecting();
            MessageUtils.showError(MessageUtils.getRoot(mqttnode), resources.getString("title.errorconnection"), ex.getLocalizedMessage(), ev -> {
                exitevent.handle(new ActionEvent());
            });
            return null;
        });         
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
        for (Unit s: appunits) {
            s.destroy();
        }         
        for (Device d: appdevices) {
            d.destroy();
        }  
        
        mqttnode.destroy();
    }
    
    private void startUnits() {
        
        initFirstTime();          
   
        for (Unit s: appunits) {
            s.start();
        }               
    }
    
    private void stopUnits() {
        for (Unit s: appunits) {
            s.stop();
        }        
    }

    public MQTTMainNode getMQTTNode() {
        return mqttnode;
    }
    
    public DeviceSimple getUnitPage() {
        if (unitpage == null) {
            unitpage = ((DeviceSimple) getDevice(SYS_UNITPAGE_ID));
        }
        return unitpage;
    }
    public DeviceSwitch getBeeper() {
        if (beeper == null) {
            beeper = ((DeviceSwitch) getDevice(SYS_BEEPER_ID));
        }
        return beeper;
    }
    public TransmitterSimple getBuzzer() {
        if (buzzer == null) {
            buzzer = ((TransmitterSimple) getDevice(SYS_BUZZER_ID));
        }
        return buzzer;
    }
    public List<Unit> getUnits() {
        return appunits;
    }
    public List<Device> getDevices() {
        return appdevices;
    }
    public Device getDevice(String id) {
        for (Device d : appdevices) {
            if (id.equals(d.getId())) {
                return d;
            }
        }
        return null;
    } 
    
    public byte[] readSYSStatus(String branch) {
        return ((TreeStatus) getDevice(SYS_VALUE_ID)).readStatus(branch);
    }
    public String loadSYSStatus(String branch) {
        return ((TreeStatus) getDevice(SYS_VALUE_ID)).loadStatus(branch);
    }
    public void sendSYSStatus(String branch, String message) {
        ((TreeStatus) getDevice(SYS_VALUE_ID)).sendStatus(branch, message);
    }
    public void sendSYSStatus(String branch, byte[] message) {
        ((TreeStatus) getDevice(SYS_VALUE_ID)).sendStatus(branch, message);
    }
   public final void sendSYSEvent(String branch, String message) {
        ((TreeEvent) getDevice(SYS_EVENT_ID)).sendEvent(branch, message);
    }  
   public final void sendSYSEvent(String branch, byte[] message) {
        ((TreeEvent) getDevice(SYS_EVENT_ID)).sendEvent(branch, message);
    }  
    public void sendSYSEvent(String branch, String message, long delay) {            
        ((TreeEvent) getDevice(SYS_EVENT_ID)).sendEvent(branch, message, delay);
    }
    public void sendSYSEvent(String branch, byte[] message, long delay) {            
        ((TreeEvent) getDevice(SYS_EVENT_ID)).sendEvent(branch, message, delay);
    }
    public final void sendSYSEvent(String branch) {
        ((TreeEvent) getDevice(SYS_EVENT_ID)).sendEvent(branch);
    }   
    public void cancelSYSEventTimer() {
        ((TreeEvent) getDevice(SYS_EVENT_ID)).cancelTimer();
    }

    public HelloIoTAppPublic getAppPublic() {
        if (apppublic == null) {
            apppublic = new HelloIoTAppPublic(this);
        }
        return apppublic;
    }
    
    private void initFirstTime() {   

        // Check if it is first time  
        File freshfile = new File(System.getProperty("user.home"), ".helloiot-" + CryptUtils.hashMD5(config.mqtt_url + config.mqtt_topicapp));
        if (!freshfile.exists()) {
            // This is the first time initialization
            LOGGER.log(Level.INFO, "Executing unit page initialization.");
            getUnitPage().sendStatus("main");
            LOGGER.log(Level.INFO, "Finished unitpage initialization.");   
        }
        
        try {
            freshfile.createNewFile();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Cannot create configuration file.", ex);
        }        
    }    
}
