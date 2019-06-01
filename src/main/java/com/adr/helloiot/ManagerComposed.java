//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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

import com.adr.helloiot.properties.VarProperties;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.format.MiniVar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author adrian
 */
public class ManagerComposed implements ManagerProtocol {  
    
    private final static Logger LOGGER = Logger.getLogger(ManagerComposed.class.getName());
    
    private final List<Pair<String, ManagerProtocol>> managers = new LinkedList<>();

    public void addManagerProtocol(BridgeConfig bridgeconfig, VarProperties config) {
        VarProperties props = new VarProperties(config, bridgeconfig.getPrefix());
        if (bridgeconfig.getBridge().hasManager(props)) {
            managers.add(new Pair<String, ManagerProtocol>(
                bridgeconfig.getRoot(), 
                bridgeconfig.getBridge().createManager(props)));
        }
    }

    @Override
    public void registerTopicsManager(GroupManagers group, Consumer<Throwable> lost) {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            managerpair.getValue().registerTopicsManager(new SubGroupManagers(group, managerpair.getKey()), lost);
        }
    }

    @Override
    public void registerSubscription(String topic,  Map<String, MiniVar> messageProperties) {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            if (topic.startsWith(managerpair.getKey())) {
                managerpair.getValue().registerSubscription(topic.substring(managerpair.getKey().length()), messageProperties);
                return;
            }
        }        
        LOGGER.warning(String.format("Topic not registered. It does not exist any topic manager for topic \"%s\"", topic));
    }

    @Override
    public void connect() {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            managerpair.getValue().connect();
        }
    }

    @Override
    public void disconnect() {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            managerpair.getValue().disconnect();
        }
    }

    @Override
    public void publish(EventMessage message) {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            if (message.getTopic().startsWith(managerpair.getKey())) {
                managerpair.getValue().publish(message.clone(message.getTopic().substring(managerpair.getKey().length())));
                return;
            }
        } 
        LOGGER.warning(String.format("Message not published. It does not exist any topic manager for topic \"%s\"", message.getTopic()));
    }
    
    private static class SubGroupManagers implements GroupManagers {
        private final GroupManagers group;
        private final String key;
        public SubGroupManagers(GroupManagers group, String key) {
            this.group = group;
            this.key = key;
        }
        @Override
        public void distributeMessage(EventMessage message) {
            group.distributeMessage(message.clone(key + message.getTopic()));
        }
    }
}
