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

import com.adr.helloiot.EventMessage;
import com.adr.helloiot.TopicsManager;
import com.adr.helloiot.device.format.MiniVar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author adrian
 */
public class TreePublishSubscribe extends TreePublish {

    private TopicsManager.Subscription status = null;
    private final Map<String, MiniVar> statusmap = new ConcurrentHashMap<>();

    public TreePublishSubscribe() {
        super();
        setRetained(true); // by default retained
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.treepublishsubscribe");
    }

    protected void consumeMessage(EventMessage message) {
        statusmap.put(message.getTopic(), getFormat().value(message.getMessage()));
    }

    @Override
    public void construct(TopicsManager manager) {
        super.construct(manager);
        status = manager.subscribe(getTopic() + "/#", getQos());
        status.setConsumer(this::consumeMessage);
    }

    @Override
    public void destroy() {
        super.destroy();
        manager.unsubscribe(status);
        status = null;
    }

    public MiniVar readMessage(String branch) {
        return statusmap.getOrDefault(getTopic() + "/" + branch, getFormat().value(null));
    }

    public String loadMessage(String branch) {
        return getFormat().format(readMessage(branch));
    }
}
