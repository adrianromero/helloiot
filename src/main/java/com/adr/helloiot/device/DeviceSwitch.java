//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.helloiot.device.format.StringFormatSwitch;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author adrian
 */
public class DeviceSwitch extends DeviceSimple {

    public final static byte[] ON = "ON".getBytes(StandardCharsets.UTF_8);
    public final static byte[] OFF = "OFF".getBytes(StandardCharsets.UTF_8);

    private byte[] on = ON;
    private byte[] off = OFF;

    public DeviceSwitch() {
        setFormat(new StringFormatSwitch(this));
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.deviceswitch");
    }

    public byte[] getOn() {
        return on;
    }

    public void setOn(byte[] on) {
        this.on = on;
    }

    public byte[] getOff() {
        return off;
    }

    public void setOff(byte[] off) {
        this.off = off;
    }

    @Override
    public byte[] nextStatus() {
        return on;
    }
    
    @Override
    public byte[] rollNextStatus() {
        return Arrays.equals(on, readStatus()) ? off : on;
    }
    
    @Override
    public byte[] prevStatus() {
        return off;
    }
    
    @Override
    public byte[] rollPrevStatus() {
        return Arrays.equals(on, readStatus()) ? off : on;
    }
    
    @Override
    public boolean hasPrevStatus() {
        return Arrays.equals(on, readStatus());
    }

    @Override
    public boolean hasNextStatus() {
        return !Arrays.equals(on, readStatus());
    }

    public void sendON() {
        sendStatus(on);
    }

    public void sendOFF() {
        sendStatus(off);
    }

    public void sendSWITCH() {
        sendStatus(nextStatus());
    }

    public void sendON(long duration) {

        if (Arrays.equals(on, readStatus()) && !hasTimer()) {
            // If  already on and not with a timer then do nothing
            return;
        }

        sendStatus(on);
        sendStatus(off, duration);
    }
}
