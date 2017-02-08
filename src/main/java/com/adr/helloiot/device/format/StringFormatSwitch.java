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

package com.adr.helloiot.device.format;

import com.adr.helloiot.device.DeviceSwitch;
import java.util.Arrays;
import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatSwitch implements StringFormat {

    private final DeviceSwitch device;
    
    public StringFormatSwitch(DeviceSwitch device) {
        this.device = device;
    }
    
    @Override
    public String toString() {
        return "SWITCH";
    }
    
    @Override
    public String format(byte[] value) {
        return Arrays.equals(device.getOn(), value) ? "ON" : "OFF";
    }

    @Override
    public byte[] parse(String formattedvalue) {
        return "ON".equals(formattedvalue) ? device.getOn() : device.getOff();
    }
    
    @Override
    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }    
}
