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

import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.graphic.IconText;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.format.MiniVar;
import java.util.concurrent.atomic.AtomicReference;
import com.adr.helloiotlib.app.TopicManager;

/**
 *
 * @author adrian
 */
public class DeviceStatus extends DeviceSubscribe {

    private final AtomicReference<MiniVar> status = new AtomicReference<>(null);

    public DeviceStatus() {
        MQTTProperty.setRetained(this, true);
    }

    @Override
    public void construct(TopicManager manager) {
        status.set(getFormat().value(null));
        super.construct(manager);
    }

    @Override
    public void destroy() {
        super.destroy();
        status.set(getFormat().value(null));
    }

    // Overwrite this  method
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.devicestatus");
    }

    @Override
    protected void consumeMessage(EventMessage message) {
        status.set(getFormat().value(message.getMessage()));
    }
    
    public MiniVar varStatus() {
        MiniVar s = status.get();
        s = s == null ? getFormat().value(null) : s;
        status.set(s);
        return s;
    }

    public String formatStatus() {
        return getFormat().format(status.get());
    }
    
    public IconStatus getIconStatus() {
        return new IconText();
    }    
}
