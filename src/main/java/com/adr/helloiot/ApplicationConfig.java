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
package com.adr.helloiot;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class ApplicationConfig {

    public boolean app_exitbutton = false;
    public boolean app_clock = true;
    public boolean app_retryconnection = false;

    public String mqtt_url = "tcp://localhost:1883";
    public String mqtt_username = "";
    public String mqtt_password = "";
    public String mqtt_clientid = "";
    public int mqtt_connectiontimeout = MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT;
    public int mqtt_keepaliveinterval = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;
    public int mqtt_defaultqos = 1;
    public int mqtt_version = MqttConnectOptions.MQTT_VERSION_DEFAULT; // 0
    public boolean mqtt_cleansession = MqttConnectOptions.CLEAN_SESSION_DEFAULT;
    public String mqtt_topicprefix = "";
    public String mqtt_topicapp = "_LOCAL_/_sys_helloIoT/mainapp";
}
