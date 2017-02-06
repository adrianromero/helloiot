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

package com.adr.helloiot.device;

import java.util.Arrays;

/**
 *
 * @author adrian
 */
public class DeviceSwitch extends DeviceSimple {
    
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.deviceswitch");
    }
    
    @Override
    public byte[] nextStatus() {     
        boolean current = StatusSwitch.getFromBytes(readStatus());
        return StatusSwitch.getFromValue(!current);    
    }

    @Override
    public byte[] prevStatus() {
        return nextStatus();
    }

    @Override
    public boolean hasPrevStatus() {
        return true;
    }

    @Override
    public boolean hasNextStatus() {
        return true;
    }    
    
    public void sendON() {
        sendStatus(StatusSwitch.ON);
    }
    
    public void sendOFF() {
        sendStatus(StatusSwitch.OFF);               
    }
    
    public void sendSWITCH() {
        sendStatus(nextStatus());               
    }    
    
    public void sendON(long duration) {
        
        if (Arrays.equals(StatusSwitch.ON, readStatus()) && !hasTimer()) {
            // If  already on and not with a timer then do nothing
            return;
        }
        
        sendStatus(StatusSwitch.ON);
        sendStatus(StatusSwitch.OFF, duration);
    }
}
