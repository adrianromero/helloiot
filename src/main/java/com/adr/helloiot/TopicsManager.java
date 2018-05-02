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

/**
 *
 * @author adrian
 */
public final class TopicsManager {

    private final static Logger logger = Logger.getLogger(TopicsManager.class.getName());

    private Consumer<Throwable> lostCallback = null;

    private final Set<TopicQos> topicsubscriptions;
    private final Map<String, List<Subscription>> subscriptions;
    private final ResourceBundle resources;    
    
    private final ManagerProtocol protocol;

    public TopicsManager(ManagerProtocol protocol) {

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
            protocol.registerSubscription(tq.getTopic(), tq.getQos());
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

    public Subscription subscribe(String topic, int qos) {

//        if (notConnected) {
//            throw new RuntimeException("Status incorrect. All subscriptions must be done before connection.");
//        }

        // To be subscribed
        topicsubscriptions.add(new TopicQos(topic, qos));

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
        protocol.publish(topic, qos, message, isRetained);
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
