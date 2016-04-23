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

import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatDecimal;

/**
 *
 * @author adrian
 */
public class Temperature extends TransmitterNumber {

    public static final StringFormat TEMPERATUREFORMAT = new StringFormatDecimal("0.0°");
    
    public Temperature() {
        setLevelMin(-40.0);
        setLevelMax(70.0);
    } 
    
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.temperature");
    }
    
    @Override
    public StringFormat getFormat() {
        return TEMPERATUREFORMAT;
    }  
    
    @Override
    public String getUnit() {
        return "°C";
    }    
}
