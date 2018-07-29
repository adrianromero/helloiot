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
package com.adr.helloiot.mqtt;

import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.MiniVarInt;

/**
 *
 * @author adrian
 */
public class MQTTProperties {
    
    private MQTTProperties() {
    }
    
    public static void setQoS(Device device, int value) {
        device.getMessageProperties().put("mqtt.qos", new MiniVarInt(value));
    }

    public static int getQoS(Device device) {
        return device.getMessageProperties().get("mqtt.qos").asInt();
    }
    
    public static void setRetained(Device device, boolean value) {
        device.getMessageProperties().put("mqtt.retained", value ? MiniVarBoolean.TRUE : MiniVarBoolean.FALSE);
    }

    public static boolean isRetained(Device device) {
        return device.getMessageProperties().get("mqtt.retained").asBoolean();
    }    
}
