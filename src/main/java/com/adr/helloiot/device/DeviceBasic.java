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
import com.adr.helloiot.device.format.MiniVar;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author adrian
 */
public class DeviceBasic extends DeviceSubscribe {

    private final AtomicReference<MiniVar> status = new AtomicReference<>(null);

    public DeviceBasic() {
        setRetained(true);
    }

    // Overwrite this  method
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.devicebasic");
    }

    @Override
    protected void consumeMessage(EventMessage message) {
        status.set(getFormat().value(message.getMessage()));
    }
    
    public MiniVar varStatus() {
        return status.updateAndGet(s -> s == null ? getFormat().value(null) : s);
    }

    public String formatStatus() {
        return getFormat().format(status.get());
    }
}
