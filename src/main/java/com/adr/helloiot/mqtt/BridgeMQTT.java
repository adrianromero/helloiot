//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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

import com.adr.helloiot.Bridge;
import com.adr.helloiot.ConnectUI;
import com.adr.helloiot.ManagerProtocol;
import com.adr.helloiot.SSLProtocol;
import com.adr.helloiot.SubProperties;
import com.adr.helloiot.properties.VarProperties;
import com.adr.helloiot.util.CryptUtils;
import com.adr.helloiot.util.HTTPUtils;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.MiniVarInt;
import com.adr.helloiotlib.format.MiniVarString;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class BridgeMQTT implements Bridge {

    @Override
    public boolean hasManager(VarProperties properties) {
        return HTTPUtils.getAddress(properties.get("host").asString()) != null;
    }

    @Override
    public ManagerProtocol createManager(VarProperties properties) {
        return new ManagerMQTT(properties);
    }
    
    @Override
    public ConnectUI createConnectUI() {
        return new ConnectMQTT();
    }
    
    @Override
    public void readConfiguration(VarProperties config, SubProperties configprops) {
        config.put("host", new MiniVarString(configprops.getProperty("host", "localhost")));
        config.put("port", new MiniVarInt(Integer.parseInt(configprops.getProperty("port", "1883"))));
        config.put("ssl", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("ssl", "false"))));
        config.put("websockets", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("websockets", "false"))));
        config.put("protocol", new MiniVarString(SSLProtocol.valueOfDefault(configprops.getProperty("protocol", "TLSv12")).getDisplayName()));
        config.put("keystore", new MiniVarString(configprops.getProperty("keystore", "")));
        config.put("keystorepassword", new MiniVarString(configprops.getProperty("keystorepassword", "")));
        config.put("truststore", new MiniVarString(configprops.getProperty("truststore", "")));
        config.put("truststorepassword", new MiniVarString(configprops.getProperty("truststorepassword")));
        config.put("username", new MiniVarString(configprops.getProperty("username", "")));
        config.put("password", new MiniVarString(configprops.getProperty("password", "")));
        config.put("clientid", new MiniVarString(configprops.getProperty("clientid", CryptUtils.generateID())));
        config.put("connectiontimeout", new MiniVarInt(Integer.parseInt(configprops.getProperty("connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT)))));
        config.put("keepaliveinterval", new MiniVarInt(Integer.parseInt(configprops.getProperty("keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT)))));
        config.put("maxinflight", new MiniVarInt(Integer.parseInt(configprops.getProperty("maxinflight", Integer.toString(MqttConnectOptions.MAX_INFLIGHT_DEFAULT)))));
        config.put("version", new MiniVarInt(Integer.parseInt(configprops.getProperty("version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT))))); // MQTT_VERSION_DEFAULT = 0; MQTT_VERSION_3_1 = 3; MQTT_VERSION_3_1_1 = 4;
        config.put("topicsys", new MiniVarString(configprops.getProperty("topicsys", "system/")));
        config.put("broker", new MiniVarString(configprops.getProperty("broker", "0")));         
    }    
}
