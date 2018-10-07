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

import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.MiniVarString;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiotlib.app.EventMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import com.adr.helloiotlib.app.TopicManager;

/**
 *
 * @author adrian
 */
public class TreePublish extends Device {

    protected TopicManager manager;
    private ScheduledFuture<?> sf = null;
    private final Object sflock = new Object();

    public TreePublish() {
        super();
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.treepublish");
    }

    @Override
    public void construct(TopicManager manager) {
        this.manager = manager;
    }

    @Override
    public void destroy() {
    }

    public final void sendMessage(String branch, MiniVar value) {
        cancelTimer();
        
        Map<String, MiniVar> props = new HashMap<>();
        EventMessage message = new EventMessage(getTopicPublish() + "/" + branch, getFormat().devalue(value), getMessageProperties());
        manager.publish(message);    
    }

    public final void sendMessage(String branch, String message) {
        sendMessage(branch, getFormat().parse(message));
    }

    public void sendMessage(String branch, MiniVar message, long delay) {
        synchronized (sflock) {
            cancelTimer();
            sf = CompletableAsync.scheduleTask(delay, () -> {
                sendMessage(branch, message);
            });
        }
    }

    public void sendMessage(String branch, String message, long delay) {
        sendMessage(branch, getFormat().parse(message), delay);
    }

    public final void sendMessage(String branch) {
        sendMessage(branch, MiniVarString.NULL);
    }

    public void sendMessage(String branch, long delay) {
        sendMessage(branch, MiniVarString.NULL, delay);
    }

    public void cancelTimer() {
        synchronized (sflock) {
            if (sf != null) {
                sf.cancel(false);
                sf = null;
            }
        }
    }
}
