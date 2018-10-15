//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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
package com.adr.helloiotext.home;

import com.adr.helloiot.HelloIoTApp;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiot.unit.ReceiverBase;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class SecurityManager extends ReceiverBase {
    
    private final static Logger logger = Logger.getLogger(SecurityManager.class.getName());
    
    private final DeviceSimple secactions;
    private final DeviceSimple secstatus;
    private final TransmitterSimple secalarm;
    
    public SecurityManager(DeviceSimple secactions, DeviceSimple secstatus, TransmitterSimple secalarm) {
        this.secactions = secactions;
        this.secstatus = secstatus;
        this.secalarm = secalarm;
    } 
    
    @Subscribe
    public void receivedStatus(EventMessage message) {        
        processSync(message);
    }
    
    private void processSync(EventMessage message) {
        
        String action = getDevice().getFormat().value(message.getMessage()).asString();
        String status = secstatus.varStatus().asString();
        
        DeviceSimple unitpage = (DeviceSimple) app.getDevice(HelloIoTApp.SYS_UNITPAGE_ID);
        DeviceSimple buzzer = (DeviceSimple) app.getDevice(HelloIoTApp.SYS_BUZZER_ID);
        DeviceSwitch beeper = (DeviceSwitch) app.getDevice(HelloIoTApp.SYS_BEEPER_ID);

        if ("ARMING".equals(action)
                && !"ARMING".equals(status)
                && !"ARMED".equals(status)) {
            // Arming security   
            logger.info("Arming security");  

            secactions.sendStatus("ARMED", 10000);
            secstatus.sendStatus("ARMING");
                   
            unitpage.sendStatus("security_arming");
            beeper.sendON();                
        } else if ("ARMED".equals(action)) {
            // Armed security
            logger.info("Armed security");            

            secactions.cancelTimer();
            secstatus.sendStatus("ARMED");
            
            try {
                app.getAllDevices().tagged("armedoff").forEach(d -> {
                    ((DeviceSimple) d).sendStatus("OFF");
                });
            } catch (InterruptedException ex) {
            }
            
            unitpage.sendStatus("security");
            beeper.sendOFF();
            buzzer.sendStatus("BEEP1");        
        } else if ("DISARMED".equals(action)) {
            // Disarmed security, back to normal.
            logger.info("Disarmed security");                        
            
            secactions.cancelTimer();      
            secstatus.sendStatus("READY");
       
            
            unitpage.sendStatus("main");
            beeper.sendOFF();
            buzzer.sendStatus("BEEP1");
            
        } else if ("FIRING".equals(action)
                && !"FIRING".equals(status)
                && !"FIRED".equals(status)) {   // Intrussion detected, firing security...
            logger.info("Firing security");             

            secactions.sendStatus("FIRED", 10000);
            secstatus.sendStatus("FIRING");
            
            unitpage.sendStatus("security");
            beeper.sendON();            
        } else if ("FIRED".equals(action)) {
            // Intrussion detected. Block everything and call the police.
            logger.info("Fired security");     
            
            secactions.cancelTimer();      
            secstatus.sendStatus("FIRED");
            secalarm.sendStatus("1");
            
            unitpage.sendStatus("security_locked");
            beeper.sendON();     
        }
    }
}
