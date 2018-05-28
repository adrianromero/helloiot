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
package com.adr.helloiot.unitsensor;

import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.device.DeviceSend;
import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.util.CompletableAsync;
import java.util.concurrent.ScheduledFuture;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public abstract class SensorTimer implements Unit {

    private long millis = 10000L;
    private ScheduledFuture<?> sf = null;

    private DeviceSend device = null;

    protected abstract void execEvent(DeviceSend device);

    @Override
    public void construct(HelloIoTAppPublic app) {
        if (sf == null) {
            sf = CompletableAsync.scheduleTask(0, millis, () -> {
                execEvent(device);
            });
        }
    }

    @Override
    public void destroy() {
        if (sf != null) {
            sf.cancel(false);
            sf = null;
        }
    }

    @Override
    public Node getNode() {
        return null;
    }

    public void setDevice(DeviceSend device) {
        this.device = device;
    }

    public DeviceSend getDevice() {
        return device;
    }

    public void setInterval(long millis) {
        this.millis = millis;
    }

    public long getInterval() {
        return millis;
    }
}
