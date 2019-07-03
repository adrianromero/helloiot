//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.app.TopicManager;
import com.adr.helloiotlib.format.MiniVarLong;
import com.adr.helloiotlib.format.StringFormatLong;
import java.time.Clock;
import java.util.Timer;
import java.util.TimerTask;

public class SystemDeviceTimer extends Device {
    
    protected TopicManager manager;
    private Timer timer;
    private final Clock clock = Clock.systemUTC();
    
    public SystemDeviceTimer() {
        MQTTProperty.setRetained(this, false);
        MQTTProperty.setQos(this, 1);
        setFormat(StringFormatLong.INSTANCE);
    }
    
    @Override
    public final void construct(TopicManager manager) {
        this.manager = manager;
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private long lastepochsecond = clock.instant().getEpochSecond();
            @Override
            public void run() {
                long epochsecond = clock.instant().getEpochSecond();
                while (epochsecond - lastepochsecond > 0L) {
                    lastepochsecond += 1L;
                    byte[] payload = getFormat().devalue(new MiniVarLong(lastepochsecond));
                    manager.publish(new EventMessage(getTopicPublish(), payload, getMessageProperties()));
                }
            }
        }, 0L, 100L);        
    }

    @Override
    public final void destroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }        
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.systemtimer");
    }
}
