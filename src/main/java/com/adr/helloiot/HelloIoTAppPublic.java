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
package com.adr.helloiot;

import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceBasic;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.device.ListDevice;

/**
 *
 * @author adrian
 */
public class HelloIoTAppPublic {

    private final HelloIoTApp app;

    public HelloIoTAppPublic(HelloIoTApp app) {
        this.app = app;
    }

    public ListDevice getAllDevices() {
        return new ListDevice(app.getDevices());
    }

    public Device getDevice(String id) {
        return app.getDevice(id);
    }

    public byte[] readSYSStatus(String branch) {
        return app.readSYSStatus(branch);
    }

    public String loadSYSStatus(String branch) {
        return app.loadSYSStatus(branch);
    }

    public void sendSYSStatus(String branch, String message) {
        app.sendSYSStatus(branch, message);
    }

    public void sendSYSStatus(String branch, byte[] message) {
        app.sendSYSStatus(branch, message);
    }

    public final void sendSYSEvent(String branch, String message) {
        app.sendSYSEvent(branch, message);
    }

    public final void sendSYSEvent(String branch, byte[] message) {
        app.sendSYSEvent(branch, message);
    }

    public void sendSYSEvent(String branch, String message, long delay) {
        app.sendSYSEvent(branch, message, delay);
    }

    public void sendSYSEvent(String branch, byte[] message, long delay) {
        app.sendSYSEvent(branch, message, delay);
    }

    public final void sendSYSEvent(String branch) {
        app.sendSYSEvent(branch);
    }

    public void cancelSYSEventTimer() {
        app.cancelSYSEventTimer();
    }

    public DeviceSimple unitPage() {
        return app.getUnitPage();
    }

    public DeviceSwitch beeper() {
        return app.getBeeper();
    }

    public DeviceSimple buzzer() {
        return app.getBuzzer();
    }
}
