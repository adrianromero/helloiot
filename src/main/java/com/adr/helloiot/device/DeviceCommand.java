//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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

import com.adr.helloiot.TopicsManager;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.graphic.IconText;

/**
 *
 * @author adrian
 */
public class DeviceCommand extends DeviceSimple {

    protected TopicsManager manager;
    private String value;
    
    @Override
    public String getDeviceName() {
        return "devicename.command";
    }    

    public IconStatus getIconStatus() {
        return new IconText();
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override 
    public MiniVar nextStatus() {
        return getFormat().parse(value);
    }
    
    @Override
    public MiniVar rollNextStatus() {
        return getFormat().parse(value);
    }
    
    @Override
    public MiniVar prevStatus() {
        return getFormat().parse(value);
    }
    
    @Override
    public MiniVar rollPrevStatus() {
        return getFormat().parse(value);
    }
    
    @Override
    public boolean hasPrevStatus() {
        return true;
    }

    @Override
    public boolean hasNextStatus() {
        return true;
    }
}
