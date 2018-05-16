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

import com.adr.helloiot.GroupManagers;
import com.adr.helloiot.ManagerProtocol;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.device.format.MiniVarBoolean;
import com.adr.helloiot.device.format.StringFormatSwitch;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author adrian
 */
public class ManagerMQTT implements MqttCallback, ManagerProtocol {

    public final static String SYS_PREFIX = "$SYS/";

    private final static Logger logger = Logger.getLogger(ManagerMQTT.class.getName());

    private final String url;
    private final String username;
    private final String password;
    private final String clientid;
    private final int timeout;
    private final int keepalive;
    private final int defaultqos;
    private final int version;
    private final int maxinflight;
    private final String topicsys;
    private final Properties sslproperties;

    // Manager
    private GroupManagers group;
    private Consumer<Throwable> lost;
    // MQTT
    private MqttAsyncClient mqttClient;
    private final List<String> worktopics = new ArrayList<>();
    private final List<Integer> workqos = new ArrayList<>();
    private final ResourceBundle resources;

    public ManagerMQTT(String url, String username, String password, String clientid, int timeout, int keepalive, int defaultqos, int version, int maxinflight, String topicsys, Properties sslproperties) {

        this.url = url;
        this.username = username;
        this.password = password;
        this.clientid = clientid;
        this.timeout = timeout;
        this.keepalive = keepalive;
        this.defaultqos = defaultqos;
        this.version = version;
        this.maxinflight = maxinflight;
        this.topicsys = topicsys;
        this.sslproperties = sslproperties;

        this.mqttClient = null;
        this.resources = ResourceBundle.getBundle("com/adr/helloiot/resources/helloiot");
    }

    @Override
    public void registerTopicsManager(GroupManagers group, Consumer<Throwable> lost) {
        this.group = group;
        this.lost = lost;
    }

    @Override
    public void registerSubscription(String topic, int qos) {
        worktopics.add(topic);
        workqos.add(qos < 0 ? defaultqos : qos);
    }

    @Override
    public void connect() {

        String[] listtopics = worktopics.toArray(new String[worktopics.size()]);
        int[] listqos = new int[workqos.size()];
        for (int i = 0; i < workqos.size(); i++) {
            listqos[i] = workqos.get(i);
        }

        try {
            mqttClient = new MqttAsyncClient(url, clientid, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            if (!Strings.isNullOrEmpty(username)) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }
            options.setConnectionTimeout(timeout);
            options.setKeepAliveInterval(keepalive);
            options.setMqttVersion(version);
            options.setCleanSession(true);
            options.setAutomaticReconnect(false);
            options.setMaxInflight(maxinflight);
            options.setSSLProperties(sslproperties);
            options.setWill(topicsys + "/app/" + clientid, new StringFormatSwitch().devalue(MiniVarBoolean.FALSE), defaultqos, true);
            mqttClient.connect(options).waitForCompletion(1000);
            mqttClient.setCallback(this);
            mqttClient.subscribe(listtopics, listqos);
            statusPublish(MiniVarBoolean.TRUE);
        } catch (MqttException ex) {
            logger.log(Level.WARNING, null, ex);
            throw new RuntimeException(String.format(resources.getString("exception.mqtt"), url), ex);
        }
    }

    private void statusPublish(MiniVar value) throws MqttException {
        MqttMessage mm = new MqttMessage(new StringFormatSwitch().devalue(value));
        mm.setQos(defaultqos);
        mm.setRetained(true);
        mqttClient.publish(topicsys + "/app/" + clientid, mm).waitForCompletion();
    }

    @Override
    public void disconnect() {
        // To be invoked by executor thread
        if (mqttClient != null) {
            if (mqttClient.isConnected()) {
                try {
                    statusPublish(MiniVarBoolean.FALSE);
                    mqttClient.setCallback(null);
                    String[] listtopics = worktopics.toArray(new String[worktopics.size()]);
                    mqttClient.unsubscribe(listtopics);
                    mqttClient.disconnect();
                    mqttClient.close();
                } catch (MqttException ex) {
                    logger.log(Level.WARNING, null, ex);
                }
            }
            mqttClient = null;
        }
    }

    @Override
    public void publish(String topic, int qos, byte[] message, boolean isRetained) {

        // To be executed in Executor thread
        if (mqttClient == null) {
            return;
        }

        logger.log(Level.INFO, "Publishing message to broker. {0}", topic);
        try {
            MqttMessage mm = new MqttMessage(message);
            mm.setQos(qos < 0 ? defaultqos : qos);
            mm.setRetained(isRetained);
            mqttClient.publish(topic, mm);
        } catch (MqttException ex) {
            // TODO: Review in case of paho exception too much publications              
            logger.log(Level.WARNING, "Cannot publish message to broker. " + topic, ex);
            // throw new RuntimeException(ex); 
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        lost.accept(thrwbl);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        group.distributeMessage(topic, mm.getPayload());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        //throw new UnsupportedOperationException("Not supported yet."); 
    }
}
