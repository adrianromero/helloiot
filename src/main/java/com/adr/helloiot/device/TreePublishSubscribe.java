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
package com.adr.helloiot.device;

import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.app.TopicSubscription;
import com.adr.helloiotlib.format.MiniVar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.adr.helloiotlib.app.TopicManager;

/**
 *
 * @author adrian
 */
public class TreePublishSubscribe extends TreePublish {

    private TopicSubscription status = null;
    private final Map<String, MiniVar> statusmap = new ConcurrentHashMap<>();

    public TreePublishSubscribe() {
        super();
        MQTTProperty.setRetained(this, true);
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.treepublishsubscribe");
    }

    protected void consumeMessage(EventMessage message) {
        statusmap.put(message.getTopic(), getFormat().value(message.getMessage()));
    }

    @Override
    public void construct(TopicManager manager) {
        super.construct(manager);
        
        String topic = getTopic() == null || getTopic().isEmpty() 
                ? "#"
                : getTopic() + "/#";        
        status = manager.subscribe(topic, getMessageProperties());
        status.setConsumer(this::consumeMessage);
    }

    @Override
    public void destroy() {
        super.destroy();
        manager.unsubscribe(status);
        status = null;
    }

    public MiniVar readMessage(String branch) {
        MiniVar value;
        return ((value = statusmap.get(getTopic() + "/" + branch)) != null) ? value : getFormat().value(null);
    }

    public String loadMessage(String branch) {
        return getFormat().format(readMessage(branch));
    }
}
