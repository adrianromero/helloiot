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

import com.google.common.base.Strings;
import java.io.IOException;
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

        ConfigProperties configprops = new ConfigPropertiesPlatform();
        try {
            configprops.load();
        } catch (IOException ex) {
            throw new RuntimeException("Configuration file cannot be loaded.", ex);
        }

        ApplicationConfig config = new ApplicationConfig();
        config.mqtt_url = configprops.getProperty("mqtt.url", "tcp://localhost:1883");
        config.mqtt_username = configprops.getProperty("mqtt.username", "");
        config.mqtt_password = configprops.getProperty("mqtt.password", "");
        config.mqtt_clientid = configprops.getProperty("mqtt.clientid", "");
        config.mqtt_connectiontimeout = Integer.parseInt(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT)));
        config.mqtt_keepaliveinterval = Integer.parseInt(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT)));
        config.mqtt_defaultqos = Integer.parseInt(configprops.getProperty("mqtt.defaultqos", "1"));
        config.mqtt_version = Integer.parseInt(configprops.getProperty("mqtt.version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT))); // MQTT_VERSION_DEFAULT = 0; MQTT_VERSION_3_1 = 3; MQTT_VERSION_3_1_1 = 4;
        config.mqtt_cleansession = Boolean.parseBoolean(configprops.getProperty("mqtt.cleansession", Boolean.toString(MqttConnectOptions.CLEAN_SESSION_DEFAULT)));
        
        config.tradfri_host = configprops.getProperty("tradfri.host", "");
        config.tradfri_psk = configprops.getProperty("tradfri.psk", "");

        config.topicapp = configprops.getProperty("client.topicapp", "_LOCAL_/mainapp");
        config.topicsys = configprops.getProperty("client.topicsys", "system");

        config.app_clock = Boolean.parseBoolean(configprops.getProperty("app.clock", "true"));
        config.app_exitbutton = Boolean.parseBoolean(configprops.getProperty("app.exitbutton", "true"));
        config.app_retryconnection = true;

        helloiotapp = new HelloIoTApp(config);

        // Add all devices and units
        helloiotapp.addServiceDevicesUnits();
        String devproperty = configprops.getProperty("devicesunits", null);
        if (!Strings.isNullOrEmpty(devproperty)) {
            for (String s : devproperty.split(",")) {
                try {
                    helloiotapp.addFXMLFileDevicesUnits(s);
                } catch (HelloIoTException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        helloiotapp.setOnDisconnectAction(event -> {
            root.getScene().getWindow().hide();
        });

        root.getChildren().add(helloiotapp.getMQTTNode().getNode());
        helloiotapp.startAndConstruct();
    }

    @Override
    public void destroy() {
        helloiotapp.stopAndDestroy();
        helloiotapp = null;
    }
}
