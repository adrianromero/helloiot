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

import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.StringFormatSwitch;

/**
 *
 * @author adrian
 */
public class DeviceSwitch extends DeviceSimple {

    public DeviceSwitch() {
        setFormat(new StringFormatSwitch());
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.deviceswitch");
    }


    @Override
    public MiniVar nextStatus() {
        return MiniVarBoolean.TRUE;
    }
    
    @Override
    public MiniVar rollNextStatus() {
        return new MiniVarBoolean(!varStatus().asBoolean());
    }
    
    @Override
    public MiniVar prevStatus() {
        return MiniVarBoolean.FALSE;
    }
    
    @Override
    public MiniVar rollPrevStatus() {
        return new MiniVarBoolean(!varStatus().asBoolean());
    }
    
    @Override
    public boolean hasPrevStatus() {
        return varStatus().asBoolean();
    }

    @Override
    public boolean hasNextStatus() {
        return !varStatus().asBoolean();
    }

    public void sendON() {
        sendStatus(MiniVarBoolean.TRUE);
    }

    public void sendOFF() {
        sendStatus(MiniVarBoolean.FALSE);
    }

    public void sendSWITCH() {
        sendStatus(rollNextStatus());
    }

    public void sendON(long duration) {

        if (varStatus().asBoolean() && !hasTimer()) {
            // If  already on and not with a timer then do nothing
            return;
        }

        sendStatus(MiniVarBoolean.TRUE);
        sendStatus(MiniVarBoolean.FALSE, duration);
    }
}
