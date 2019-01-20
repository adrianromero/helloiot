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
package com.adr.helloiot.device;

import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiotlib.app.EventMessage;
import java.util.Objects;

/**
 *
 * @author adrian
 */
public class DeviceSimulator extends DeviceSubscribe {
    
     public DeviceSimulator() {
        MQTTProperty.setRetained(this, true);
    }   
     
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.devicesimulator");
    }
    
    @Override
    protected void consumeMessage(EventMessage message) {
        if (Objects.equals(this.getTopic(), getTopicPublish())) {
            return; // No need to mirror the message if topics are equal
        }
        EventMessage sendmessage = new EventMessage(getTopicPublish(), message.getMessage(), getMessageProperties());
        manager.publish(sendmessage);
    }    
}
