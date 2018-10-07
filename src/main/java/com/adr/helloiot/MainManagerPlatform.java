//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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
package com.adr.helloiot;

import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.MiniVarInt;
import com.adr.helloiotlib.format.MiniVarString;
import com.adr.helloiot.util.CryptUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application.Parameters;
import javafx.scene.layout.StackPane;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class MainManagerPlatform implements MainManager {

    private HelloIoTApp helloiotapp = null;

    @Override
    public void construct(StackPane root, Parameters params) {

        ConfigProperties configprops = new ConfigProperties();
        try {
            configprops.load(() -> getClass().getResourceAsStream("/META-INF/.helloiot-config.properties"));
        } catch (IOException ex) {
            throw new RuntimeException("Configuration file cannot be loaded.", ex);
        }

        Map<String, String> namedParams = params.getNamed();

        Map<String, MiniVar> config = new HashMap<>();
        config.put("mqtt.host", new MiniVarString(configprops.getProperty("mqtt.host", "localhost")));
        config.put("mqtt.port", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.port", "1883"))));
        config.put("mqtt.ssl", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("mqtt.ssl", "false"))));
        config.put("mqtt.websockets", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("mqtt.websockets", "false"))));
        config.put("mqtt.protocol", new MiniVarString(SSLProtocol.valueOfDefault(configprops.getProperty("mqtt.protocol", "TLSv12")).getDisplayName()));
        config.put("mqtt.keystore", new MiniVarString(configprops.getProperty("mqtt.keystore", "")));
        config.put("mqtt.keystorepassword", new MiniVarString(getNamedParam(namedParams, "mqtt.keystorepassword", "")));
        config.put("mqtt.truststore", new MiniVarString(configprops.getProperty("mqtt.truststore", "")));
        config.put("mqtt.truststorepassword", new MiniVarString(getNamedParam(namedParams, "mqtt.truststorepassword", "")));
        config.put("mqtt.username", new MiniVarString(getNamedParam(namedParams, "mqtt.username", "")));
        config.put("mqtt.password", new MiniVarString(getNamedParam(namedParams, "mqtt.password", "")));
        config.put("mqtt.clientid", new MiniVarString(configprops.getProperty("mqtt.clientid", CryptUtils.generateID())));
        config.put("mqtt.connectiontimeout", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT)))));
        config.put("mqtt.keepaliveinterval", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT)))));
        config.put("mqtt.maxinflight", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.maxinflight", Integer.toString(MqttConnectOptions.MAX_INFLIGHT_DEFAULT)))));
        config.put("mqtt.version", new MiniVarInt(Integer.parseInt(configprops.getProperty("mqtt.version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT))))); // MQTT_VERSION_DEFAULT = 0; MQTT_VERSION_3_1 = 3; MQTT_VERSION_3_1_1 = 4;
        config.put("client.broker", new MiniVarString(configprops.getProperty("client.broker", "0")));

        config.put("tradfri.host", new MiniVarString(configprops.getProperty("tradfri.host", "")));
        config.put("tradfri.identity", new MiniVarString(getNamedParam(namedParams, "tradfri.identity", "")));
        config.put("tradfri.psk", new MiniVarString(getNamedParam(namedParams, "tradfri.psk", "")));

        config.put("client.topicapp", new MiniVarString(configprops.getProperty("client.topicapp", "_LOCAL_/mainapp")));
        config.put("client.topicsys", new MiniVarString(configprops.getProperty("client.topicsys", "system")));

        config.put("app.clock", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("app.clock", "false"))));
        config.put("app.exitbutton", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("app.exitbutton", "true"))));
        config.put("app.retryconnection", MiniVarBoolean.TRUE);
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name())));

        try {
            helloiotapp = new HelloIoTApp(config);
        } catch (HelloIoTException ex) {
            throw new RuntimeException("HelloIoT application cannot be loaded.", ex);
        }

        // Add all devices and units
        helloiotapp.addServiceDevicesUnits();

        try {
            helloiotapp.addFXMLFileDevicesUnits(configprops.getProperty("devicesunits"));
        } catch (HelloIoTException ex) {
            Logger.getLogger(MainManagerPlatform.class.getName()).log(Level.SEVERE, null, ex);
        }
        

//        TopicInfoBuilder topicinfobuilder = new TopicInfoBuilder();
//        int topicinfosize = Integer.parseInt(configprops.getProperty("topicinfo.size", "0"));
//        int i = 0;
//        while (i++ < topicinfosize) {
//            TopicInfo topicinfo = topicinfobuilder.fromProperties(new ConfigSubProperties(configprops, "topicinfo" + Integer.toString(i)));
//            TopicStatus ts = topicinfo.getTopicStatus();
//            helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
//        }

        helloiotapp.setOnDisconnectAction(event -> {
            root.getScene().getWindow().hide();
        });

        root.getChildren().add(helloiotapp.getMainNode().getNode());
        helloiotapp.startAndConstruct();
    }

    @Override
    public void destroy() {
        helloiotapp.stopAndDestroy();
        helloiotapp = null;
    }
    
    private final String getNamedParam(Map<String, String> namedparams, String key, String defaultValue) {
        String value;
        return ((value = namedparams.get(key)) != null) ? value : defaultValue;        
    }
}
