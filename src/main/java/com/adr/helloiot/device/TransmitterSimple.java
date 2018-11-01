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

import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.app.TopicManager;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author adrian
 */
public class TransmitterSimple extends Device implements DeviceSend {
    
    protected TopicManager manager;
    private ScheduledFuture<?> sf = null;
    private final Object sflock = new Object();  
    
    @Override
    public final void construct(TopicManager manager) {
        this.manager = manager;
    }

    @Override
    public final void destroy() {
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.transmittersimple");
    }

    @Override
    public void sendStatus(MiniVar event) {
        cancelTimer();
        EventMessage message = new EventMessage(getTopicPublish(), getFormat().devalue(event), getMessageProperties());
        manager.publish(message);
    }

    @Override
    public void sendStatus(String event) {
        sendStatus(getFormat().parse(event));
    }
    
    @Override
    public void sendStatus(MiniVar status, long delay) {
        synchronized (sflock) {
            cancelTimer();
            sf = CompletableAsync.scheduleTask(delay, () -> {
                TransmitterSimple.this.sendStatus(status);
            });
        }
    }

    @Override
    public void sendStatus(String status, long delay) {
        sendStatus(getFormat().parse(status), delay);
    }

    @Override
    public boolean hasTimer() {
        synchronized (sflock) {
            return sf != null;
        }
    }

    @Override
    public void cancelTimer() {
        synchronized (sflock) {
            if (sf != null) {
                sf.cancel(false);
                sf = null;
            }
        }
    }    
}
