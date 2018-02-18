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

import com.adr.helloiot.tradfri.ManagerTradfri;
import java.util.function.Consumer;

/**
 *
 * @author adrian
 */
public class ManagerComposed implements ManagerProtocol {

    public final static String LOCAL_PREFIX = "_LOCAL_/";
    public final static String TRADFRI_PREFIX = "TRÅDFRI/";

    private final ManagerLocal managerlocal;
    private final ManagerTradfri managertradfri;
    private final ManagerMQTT managermqtt;

    public ManagerComposed(ManagerLocal managerlocal, ManagerTradfri managertradfri, ManagerMQTT managermqtt) {
        this.managerlocal = managerlocal;
        this.managertradfri = managertradfri;
        this.managermqtt = managermqtt;
    }

    @Override
    public void registerTopicsManager(GroupManagers group, Consumer<Throwable> lost) {
        managerlocal.registerTopicsManager(group, lost);
        managertradfri.registerTopicsManager(group, lost);
        managermqtt.registerTopicsManager(group, lost);
    }

    @Override
    public void registerSubscription(String topic, int qos) {
        if (topic.startsWith(LOCAL_PREFIX)) {
            managerlocal.registerSubscription(topic, qos);
        } else if (topic.startsWith(TRADFRI_PREFIX)) {
            managertradfri.registerSubscription(topic, qos);
        } else {
            managermqtt.registerSubscription(topic, qos);
        }
    }

    @Override
    public void connect() {
        managermqtt.connect();
        managertradfri.connect();
        managerlocal.connect();
    }

    @Override
    public void disconnect() {
        managermqtt.disconnect();
        managertradfri.disconnect();
        managerlocal.disconnect();
    }

    @Override
    public void publish(String topic, int qos, byte[] message, boolean isRetained) {
        if (topic.startsWith(LOCAL_PREFIX)) {
            managerlocal.publish(topic, qos, message, isRetained);
        } else if (topic.startsWith(TRADFRI_PREFIX)) {
            managertradfri.publish(topic, qos, message, isRetained);
        } else {
            managermqtt.publish(topic, qos, message, isRetained);
        }
    }
}
