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

package com.adr.helloiot;

import com.adr.helloiot.device.StatusSwitch;
import com.adr.helloiot.util.CompletableAsync;
import com.google.common.base.Strings;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletionException;
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
    
    private final static String STATUS_TOPIC_SUFFIX = "/status";
    
    private final static Logger logger = Logger.getLogger(MQTTManager.class.getName());
       
    private MqttAsyncClient mqttClient;
    private DB dbClient;
    private ConcurrentMap<String, byte[]> mapClient;

    private final String url;
    private final String username;
    private final String password;
    private final int timeout;
    private final int keepalive;
    private final Properties sslproperties;
    private final String topicprefix;
    private final String topicapp;
    private final int qos;   
    
    private Consumer<Throwable> connectionLost = null;
    
    private final Set<String> topicsubscriptions;
    private final Map<String, List<Subscription>> subscriptions;
    
    public MQTTManager(String url, String topicapp) {
        this(url, null, null, 30, 60, 1, null, "", topicapp);
    }
    
    public MQTTManager(String url, String username, String password, int timeout, int keepalive, int qos, Properties sslproperties, String topicprefix, String topicapp) {
        
        this.mqttClient = null;
        
        this.url = url;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.keepalive = keepalive;
        this.qos = qos;        
        this.sslproperties = sslproperties;
        this.topicprefix = topicprefix;
        this.topicapp = topicapp;
        
        this.subscriptions = new HashMap<>();
        this.topicsubscriptions = new HashSet<>();
    }

    public CompletableAsync<Void> open() {
        
        String[] listtopics = topicsubscriptions.stream().map(s -> s.startsWith(SYS_PREFIX) ? s : topicprefix + s).toArray(size -> new String[size]);
        int[] listqos = new int[topicsubscriptions.size()];
        Arrays.fill(listqos, qos);       
        
        return CompletableAsync.runAsync(() -> {
            if (mqttClient == null) {
                try {
                    mqttClient = new MqttAsyncClient(url, MqttAsyncClient.generateClientId()); 
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    if (!getStatusTopic().startsWith(LOCAL_PREFIX)) {
                        try {
                            options.setWill(topicprefix + getStatusTopic(), StatusSwitch.OFF.getBytes("UTF-8"), 1, true);
                            logger.log(Level.INFO, "Set will status OFF.");
                        } catch (UnsupportedEncodingException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    if (!Strings.isNullOrEmpty(username)) {
                        options.setUserName(username);
                        options.setPassword(password.toCharArray());
                    }
                    options.setConnectionTimeout(timeout);
                    options.setKeepAliveInterval(keepalive);
                    options.setSSLProperties(sslproperties);
                    mqttClient.connect(options).waitForCompletion(1000);    
                    mqttClient.setCallback(this);
                    mqttClient.subscribe(listtopics, listqos).waitForCompletion(1000);

                    if (!getStatusTopic().startsWith(LOCAL_PREFIX)) {
                        publish(getStatusTopic(), StatusSwitch.ON, true); 
                        logger.log(Level.INFO, "Publish status ON.");
                    }
                    
                    dbClient = DBMaker.fileDB(new File(System.getProperty("user.home"), ".helloiot-" + getClient() + ".mapdb")).make();
                    mapClient = dbClient.hashMap("map", Serializer.STRING, Serializer.BYTE_ARRAY).createOrOpen();
                    
                    mapClient.forEach((topic, payload) -> {
                        try {
                            MqttMessage mm = new MqttMessage(payload);
                            mm.setQos(qos);
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
                    throw new CompletionException(ex);
                }
            }
        });
    }
    
    public CompletableAsync<Void> close() {
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
                    if (!getStatusTopic().startsWith(LOCAL_PREFIX)) {
                        publish(getStatusTopic(), StatusSwitch.OFF, true);
                        logger.log(Level.INFO, "Publish status OFF.");
                    }
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
    
    public String getClient() {
        return topicapp.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String getStatusTopic() {
        return topicapp + STATUS_TOPIC_SUFFIX;
    }

    public Subscription subscribe(String topic) {
        
        if (mqttClient != null) {
            throw new RuntimeException("Status incorrect. All subscriptions must be done before connection.");
        }
        
        // To be subscribed in MQTT
        topicsubscriptions.add(topic);
        
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
    
    public void publishEvent(String topic, String message) {
        if (mqttClient != null) {
            try {                
                publish(topic, message, false);
            } catch (MqttException ex) {
                throw new CompletionException(ex);
            }                
        }
    }
    
    public void publishStatus(String topic, String message) {
        if (mqttClient != null) {
            try {
                publish(topic, message, true);
            } catch (MqttException ex) {
                throw new CompletionException(ex);
            }
        }
    }
    
    private void publish(String topic, String message, boolean isStatus) throws MqttException {
        // To be executed in Executor thread
        try {
            MqttMessage mm = new MqttMessage(message.getBytes("UTF-8"));
            mm.setQos(qos);
            mm.setRetained(isStatus);
            if (topic.startsWith(LOCAL_PREFIX)) {
                CompletableAsync.runAsync(() -> {
                    try {
                        if (isStatus) {
                            mapClient.put(topic, mm.getPayload());
                            dbClient.commit();
                        }
                        messageArrived(topicprefix + topic, mm);
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Cannot publish locally.", ex);
                    }
                });
                logger.log(Level.INFO, "Publishing message to local.");
            } else {
                mqttClient.publish(topicprefix + topic, mm);
                logger.log(Level.INFO, "Publishing message to broker.");
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void distributeMessage(String topic, String message) {
        distributeWilcardMessage(topic, topic, message);
        distributeRecursiveMessage(topic, topic.length() - 1,  message);
    }
    
    private void distributeRecursiveMessage(String topic, int starting, String message) {
        int i = topic.lastIndexOf('/', starting);
        if (i < 0) {
            distributeWilcardMessage("#" , topic, message);
        } else {
            distributeWilcardMessage(topic.substring(0, i) + "/#", topic, message);
            distributeRecursiveMessage(topic, i - 1, message);
        }   
    }
   
    private void distributeWilcardMessage(String subscriptiontopic, String topic, String message) {
        List<Subscription> subs = subscriptions.get(subscriptiontopic);
        if (subs != null) {
            for (Subscription s: subs) {
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
        try {
            if (topic.startsWith(topicprefix)){
                distributeMessage(topic.substring(topicprefix.length()), new String(mm.getPayload(), "UTF-8"));
            } else if (topic.startsWith(SYS_PREFIX)) {
                distributeMessage(topic, new String(mm.getPayload(), "UTF-8"));
            } else {
                throw new RuntimeException("Bad topic prefix.");
            }
                        
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
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
}
