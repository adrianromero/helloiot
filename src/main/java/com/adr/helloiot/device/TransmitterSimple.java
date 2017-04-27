//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.helloiot.MQTTManager;

/**
 *
 * @author adrian
 */
public class TransmitterSimple extends Device {
    
    protected MQTTManager mqttHelper;
    
    @Override
    public final void construct(MQTTManager mqttManager) {
        this.mqttHelper = mqttManager;
    }

    @Override
    public final void destroy() {
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.transmittersimple");
    }

    public void sendEvent(byte[] event) {
        mqttHelper.publish(getTopicPublish(), getQos(), event, isRetained());
    }

    public void sendEvent(String event) {
        sendEvent(getFormat().parse(event));
    }
}
