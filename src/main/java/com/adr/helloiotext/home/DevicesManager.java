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

import com.adr.helloiot.unit.ReceiverBase;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.device.DeviceProvider;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiotlib.device.ListDevice;
import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.StringFormatDecimal;
import com.google.common.eventbus.Subscribe;

/**
 *
 * @author adrian
 */
public class DevicesManager extends ReceiverBase {
    
    private DeviceSimple secactions;
    private DeviceSimple secstatus;
    
    public DevicesManager(DeviceSimple secactions, DeviceSimple secstatus) {
        this.secactions = secactions;
        this.secstatus = secstatus;
    }
    
    @Subscribe
    public void receivedStatus(EventMessage message) {        
        processSync(message);
    }
    
    private void processSync(EventMessage message) {
        
        String security = secstatus.varStatus().asString();
        
        if ("ARMED".equals(security)) { // Firing security if armed...          
            secactions.sendStatus("FIRING");
        }
        
        ListDevice strm = app.getAllDevices();

        String[] commands = message.getTopic().split("/");

        String selector = commands[commands.length - 3];
        String parameter = commands[commands.length - 2];
        String action = commands[commands.length - 1];

        if ("tagged".equals(selector)) {
            strm = strm.tagged(parameter);
        } else if("type".equals(selector)) {
            strm = strm.type(parameter);
        } else if("name".equals(selector)) {
            strm = strm.name(parameter);
        } else {
            throw new RuntimeException("Selector not found: " + selector);
        }

        DeviceProvider<? super Device> consumer;
        if ("status".equals(action)) {
            consumer = d -> {
                DeviceSimple device = (DeviceSimple) d;
                device.sendStatus(device.getFormat().value(message.getMessage()));
            };
        } else if("statustimer".equals(action)) {
            MiniVar messagevar = StringFormatDecimal.INTEGER.value(message.getMessage());
            long l = messagevar.isEmpty() ? 10000L : messagevar.asInt();
            consumer = d -> {
                DeviceSimple device = (DeviceSimple) d;
                device.sendStatus(device.getFormat().value(message.getMessage()), l);                    
            };            
        } else if("next".equals(action)) {
            consumer = d -> {                  
                ((DeviceSimple) d).nextStatus();
            };            
        } else if("prev".equals(action)) {
            consumer = d -> {
                ((DeviceSimple) d).prevStatus();
            };            
        } else if("switch".equals(action)) {
            consumer = d -> {
                ((DeviceSwitch) d).sendSWITCH();
            };           
        } else if("on".equals(action)) {
            consumer = d -> {
                ((DeviceSwitch) d).sendON();
            };           
        } else if("off".equals(action)) {
            consumer = d -> {
                ((DeviceSwitch) d).sendOFF();
            };           
        } else if("ontimer".equals(action)) {
            MiniVar messagevar = StringFormatDecimal.INTEGER.value(message.getMessage());
            long l = messagevar.isEmpty() ? 10000L : messagevar.asInt();
            consumer = d -> {
                ((DeviceSwitch) d).sendON(l);
            };           
        } else {
            throw new RuntimeException("Action not found: " + action);
        }

        try {
            strm.forEach(consumer);
        } catch (InterruptedException ex) {
        }       
    }
 }
