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

import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.CryptUtils;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
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
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

/**
 *
 * @author adrian
 */
public final class MQTTManager implements MqttCallback {

    public final static String SYS_PREFIX = "$SYS/";
    public final static String LOCAL_PREFIX = "_LOCAL_/";

    private final static Logger logger = Logger.getLogger(MQTTManager.class.getName());

    private MqttAsyncClient mqttClient;
    private DB dbClient;
    private ConcurrentMap<String, byte[]> mapClient;
    private final ResourceBundle resources;

    private final String url;
    private final String username;
    private final String password;
    private final String clientid;
    private final int timeout;
    private final int keepalive;
    private final Properties sslproperties;
    private final String topicprefix;
    private final int defaultqos;
    private final int version;
    private final boolean cleansession;

    private Consumer<Throwable> connectionLost = null;

    private final Set<TopicQos> topicsubscriptions;
    private final Map<String, List<Subscription>> subscriptions;

    public MQTTManager(String url) {
        this(url, null, null, null, MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT, MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT, 1, MqttConnectOptions.MQTT_VERSION_DEFAULT, MqttConnectOptions.CLEAN_SESSION_DEFAULT, null, "");
    }

    public MQTTManager(String url, String username, String password, String clientid, int timeout, int keepalive, int defaultqos, int version, boolean cleansession, Properties sslproperties, String topicprefix) {

        this.mqttClient = null;
        this.resources = ResourceBundle.getBundle("com/adr/helloiot/resources/helloiot");

        this.url = url;
        this.username = username;
        this.password = password;
        this.clientid = clientid;
        this.timeout = timeout;
        this.keepalive = keepalive;
        this.defaultqos = defaultqos;
        this.version = version;
        this.cleansession = cleansession;
        this.sslproperties = sslproperties;
        this.topicprefix = topicprefix;

        this.subscriptions = new HashMap<>();
        this.topicsubscriptions = new HashSet<>();
    }

    public ListenableFuture<?> open() {

        List<String> worktopics = new ArrayList<>();
        List<Integer> workqos = new ArrayList<>();
        for (TopicQos tq : topicsubscriptions) {
            if (tq.getTopic() == null || tq.getTopic().isEmpty()) {
                return CompletableAsync.runAsync(() -> {
                    throw new RuntimeException(new HelloIoTException(resources.getString("exception.topicscannotbeempty")));
                });
            }

            if (!tq.getTopic().startsWith(LOCAL_PREFIX)) {
                if (tq.getTopic().startsWith(SYS_PREFIX)) {
                    worktopics.add(tq.getTopic());
                } else {
                    worktopics.add(topicprefix + tq.getTopic());
                }
                workqos.add(tq.getQos());
            }
        }

        String[] listtopics = worktopics.toArray(new String[worktopics.size()]);
        int[] listqos = new int[workqos.size()];
        for (int i = 0; i < workqos.size(); i++) {
            listqos[i] = workqos.get(i);
        }

        return CompletableAsync.runAsync(() -> {
            if (mqttClient == null) {
                try {
                    mqttClient = new MqttAsyncClient(url, clientid == null || clientid.isEmpty() ? MqttAsyncClient.generateClientId() : clientid, new MemoryPersistence());
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    if (!Strings.isNullOrEmpty(username)) {
                        options.setUserName(username);
                        options.setPassword(password.toCharArray());
                    }
                    options.setConnectionTimeout(timeout);
                    options.setKeepAliveInterval(keepalive);
                    options.setMqttVersion(version);
                    options.setCleanSession(cleansession);
                    options.setSSLProperties(sslproperties);
                    mqttClient.connect(options).waitForCompletion(1000);
                    mqttClient.setCallback(this);
                    mqttClient.subscribe(listtopics, listqos);

                    File dbfile = new File(System.getProperty("user.home"), ".helloiot-" + CryptUtils.hashSHA512(url) + ".mapdb"); // dbfile is function of url only
                    dbClient = DBMaker.fileDB(dbfile).make();
                    mapClient = dbClient.hashMap("map", Serializer.STRING, Serializer.BYTE_ARRAY).createOrOpen();
//                    mapClient = new ConcurrentHashMap<>(); // and deserialize from disk

                    mapClient.forEach((topic, payload) -> {
                        try {
                            MqttMessage mm = new MqttMessage(payload);
                            mm.setQos(defaultqos);
                            mm.setRetained(true);
                            messageArrived(topicprefix + topic, mm);
                            logger.log(Level.INFO, "Init status: {0}", topic);
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, "Cannot publish locally.", ex);
                        }
                    });

                } catch (MqttException ex) {
                    closeinternal();
                    logger.log(Level.WARNING, null, ex);
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public ListenableFuture<?> close() {
        return CompletableAsync.runAsync(() -> {
            closeinternal();
        });
    }

    private void closeinternal() {
        // To be invoked by executor thread
        if (mqttClient != null) {

            mapClient = null;
            if (dbClient != null) {
                dbClient.close();
                dbClient = null;
            }

            if (mqttClient.isConnected()) {
                try {
                    mqttClient.setCallback(null);
                    mqttClient.disconnect();
                } catch (MqttException ex) {
                    logger.log(Level.WARNING, null, ex);
                }
            }
            mqttClient = null;
        }
    }

    public void setOnConnectionLost(Consumer<Throwable> callback) {
        // To be invoked in JavaFX Thread. 
        connectionLost = callback;
    }

    public Subscription subscribe(String topic, int qos) {

        if (mqttClient != null) {
            throw new RuntimeException("Status incorrect. All subscriptions must be done before connection.");
        }

        // To be subscribed in MQTT
        topicsubscriptions.add(new TopicQos(topic, qos < 0 ? defaultqos : qos));

        // To be invoked in JavaFX Thread 
        List<Subscription> subs = subscriptions.get(topic);
        if (subs == null) {
            subs = new ArrayList<>();
        }
        Subscription s = new Subscription(topic);
        subs.add(s);
        subscriptions.put(topic, subs);
        return s;
    }

    public void unsubscribe(Subscription s) {
        // To be invoked in JavaFX Thread 
        List<Subscription> subs = subscriptions.get(s.getTopic());
        if (subs != null) {
            subs.remove(s);
            if (subs.isEmpty()) {
                subscriptions.remove(s.getTopic());
            }
        }
    }

    public void publish(String topic, int qos, byte[] message, boolean isRetained) {

        // To be executed in Executor thread
        if (mqttClient == null) {
            return;
        }

        MqttMessage mm = new MqttMessage(message);
        mm.setQos(qos < 0 ? defaultqos : qos);
        mm.setRetained(isRetained);
        if (topic.startsWith(LOCAL_PREFIX)) {
            logger.log(Level.INFO, "Publishing message to local.");
            CompletableAsync.runAsync(() -> {
                try {
                    if (isRetained) {
                        mapClient.put(topic, mm.getPayload());
                        dbClient.commit();
                    }
                    messageArrived(topicprefix + topic, mm);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Cannot publish locally.", ex);
                }
            });

        } else {
            logger.log(Level.INFO, "Publishing message to broker.");
            try {
                mqttClient.publish(topicprefix + topic, mm);
            } catch (MqttException ex) {
                throw new RuntimeException(ex); // TODO: Review in cas of paho exception too much publications              
            }
        }
    }

    private void distributeMessage(String topic, byte[] message) {
        distributeWilcardMessage(topic, topic, message);
        distributeRecursiveMessage(topic, topic.length() - 1, message);
    }

    private void distributeRecursiveMessage(String topic, int starting, byte[] message) {
        int i = topic.lastIndexOf('/', starting);
        if (i < 0) {
            distributeWilcardMessage("#", topic, message);
        } else {
            distributeWilcardMessage(topic.substring(0, i) + "/#", topic, message);
            distributeRecursiveMessage(topic, i - 1, message);
        }
    }

    private void distributeWilcardMessage(String subscriptiontopic, String topic, byte[] message) {
        List<Subscription> subs = subscriptions.get(subscriptiontopic);
        if (subs != null) {
            for (Subscription s : subs) {
                s.consume(new EventMessage(topic, message));
            }
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        closeinternal();

        if (connectionLost != null) {
            connectionLost.accept(thrwbl);
        } else {
            logger.log(Level.WARNING, "Ignored connection lost.", thrwbl);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        if (topic.startsWith(topicprefix)) {
            distributeMessage(topic.substring(topicprefix.length()), mm.getPayload());
        } else if (topic.startsWith(SYS_PREFIX)) {
            distributeMessage(topic, mm.getPayload());
        } else {
            throw new RuntimeException("Bad topic prefix.");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        //throw new UnsupportedOperationException("Not supported yet."); 
    }

    public static class Subscription {

        private final String topic;
        private Consumer<EventMessage> consumer = null;

        private Subscription(String topic) {
            this.topic = topic;
        }

        public String getTopic() {
            return topic;
        }

        public void consume(EventMessage message) {
            if (consumer != null) {
                consumer.accept(message);
            }
        }

        public void setConsumer(Consumer<EventMessage> consumer) {
            this.consumer = consumer;
        }
    }

    private static class TopicQos {

        private final String topic;
        private final int qos;

        public TopicQos(String topic, int qos) {
            this.topic = topic;
            this.qos = qos;
        }

        public String getTopic() {
            return topic;
        }

        public int getQos() {
            return qos;
        }
    }
}
