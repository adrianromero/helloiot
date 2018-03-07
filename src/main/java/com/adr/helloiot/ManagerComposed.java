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

import java.util.LinkedList;
import java.util.List;
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

    public void addManagerProtocol(String prefix, ManagerProtocol manager) {
        managers.add(new Pair<String, ManagerProtocol>(prefix, manager));
    }

    @Override
    public void registerTopicsManager(GroupManagers group, Consumer<Throwable> lost) {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            managerpair.getValue().registerTopicsManager(group, lost);
        }
    }

    @Override
    public void registerSubscription(String topic, int qos) {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            if (topic.startsWith(managerpair.getKey())) {
                managerpair.getValue().registerSubscription(topic, qos);
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
    public void publish(String topic, int qos, byte[] message, boolean isRetained) {
        for (Pair<String, ManagerProtocol> managerpair : managers) {
            if (topic.startsWith(managerpair.getKey())) {
                managerpair.getValue().publish(topic, qos, message, isRetained);
                return;
            }
        } 
        LOGGER.warning(String.format("Message not published. It does not exist any topic manager for topic \"%s\"", topic));
    }
}
