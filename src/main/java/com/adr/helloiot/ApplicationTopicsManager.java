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

import com.adr.helloiotlib.app.TopicSubscription;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiot.util.CompletableAsync;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adr.helloiotlib.app.TopicManager;
import com.adr.helloiotlib.format.MiniVar;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author adrian
 */
public final class ApplicationTopicsManager implements TopicManager {

    private final static Logger logger = Logger.getLogger(TopicManager.class.getName());

    private Consumer<Throwable> lostCallback = null;

    private final Set<TopicQos> topicsubscriptions;
    private final Map<String, List<TopicSubscription>> subscriptions;
    private final ResourceBundle resources;    
    
    private final ManagerProtocol protocol;

    public ApplicationTopicsManager(ManagerProtocol protocol) {

        this.resources = ResourceBundle.getBundle("com/adr/helloiot/resources/helloiot");
        this.subscriptions = new HashMap<>();
        this.topicsubscriptions = new HashSet<>();
        this.protocol = protocol;
        protocol.registerTopicsManager(this::distributeMessage, this::connectionLost);
    }
    
    public ListenableFuture<?> open() {

        for (TopicQos tq : topicsubscriptions) {
            if (tq.getTopic() == null || tq.getTopic().isEmpty()) {
                return CompletableAsync.runAsync(() -> {
                    throw new RuntimeException(resources.getString("exception.topicscannotbeempty"));
                });
            }
            protocol.registerSubscription(tq.getTopic(), tq.getMessageProperties());
        }

        return CompletableAsync.runAsync(() -> {         
            try {
                protocol.connect();
            } catch (Exception ex) {
                closeinternal();
                throw(ex);
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
        protocol.disconnect();  
    }

    @Override
    public TopicSubscription subscribe(String topic, Map<String, MiniVar> properties) {

//        if (notConnected) {
//            throw new RuntimeException("Status incorrect. All subscriptions must be done before connection.");
//        }

        // To be subscribed
        topicsubscriptions.add(new TopicQos(topic, properties));

        // To be invoked in JavaFX Thread 
        List<TopicSubscription> subs = subscriptions.get(topic);
        if (subs == null) {
            subs = new ArrayList<>();
        }
        TopicSubscription s = new TopicSubscription(topic);
        subs.add(s);
        subscriptions.put(topic, subs);
        return s;
    }

    @Override
    public void unsubscribe(TopicSubscription s) {
        // To be invoked in JavaFX Thread 
        List<TopicSubscription> subs = subscriptions.get(s.getTopic());
        if (subs != null) {
            subs.remove(s);
            if (subs.isEmpty()) {
                subscriptions.remove(s.getTopic());
            }
        }
    }

    @Override
    public void publish(EventMessage message) {
        // To be executed in Executor thread
        protocol.publish(message);
    }

    public void setOnConnectionLost(Consumer<Throwable> callback) {
        // To be invoked in JavaFX Thread. 
        lostCallback = callback;
    }
    
    private void connectionLost(Throwable thrwbl) {
        if (lostCallback != null) {
            lostCallback.accept(thrwbl);
        } else {
            logger.log(Level.WARNING, "Ignored connection lost.", thrwbl);
        }        
    }

    private void distributeMessage(EventMessage message) {
        for (Map.Entry<String, List<TopicSubscription>> entry: subscriptions.entrySet()) {
            if (MqttTopic.isMatched(entry.getKey(), message.getTopic())) {
                List<TopicSubscription> subs = entry.getValue();
                if (subs != null) {
                    for (TopicSubscription s : subs) {
                        s.consume(message);
                    }
                }
            }
        }
    }

    private static class TopicQos {

        private final String topic;
        private final Map<String, MiniVar> messageProperties;

        public TopicQos(String topic, Map<String, MiniVar> messageProperties) {
            this.topic = topic;
            this.messageProperties = messageProperties;
        }

        public String getTopic() {
            return topic;
        }

        public Map<String, MiniVar> getMessageProperties() {
            return messageProperties;
        }
    }
}
