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

import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.format.MiniVar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author adrian
 */
public class DeviceSet extends DeviceSimple {

    private final AtomicInteger valuesindex = new AtomicInteger(-1);

    private final ArrayList<String> values = new ArrayList<>();
    
    @Override
    protected void consumeMessage(EventMessage message) {
        super.consumeMessage(message);      
        valuesindex.set(values.indexOf(getFormat().format(getFormat().value(message.getMessage()))));
    }
    
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.deviceset");
    }

    public List<String> getValues() {
        return values;
    }
    
    @Override 
    public MiniVar nextStatus() {
        int i = valuesindex.get();
        if (i < 0) {
            return getFormat().parse(values.get(1)); // go to second element. It is a design decision
        } else if (i < values.size() - 1) {
            return getFormat().parse(values.get(i + 1));
        } else {
            return getFormat().parse(values.get(values.size() - 1)); // stay in last
        }        
    }
    
    @Override
    public MiniVar rollNextStatus() {
        int i = valuesindex.get();
        if (i < 0) {
            return getFormat().parse(values.get(1)); // go to second element. It is a design decision
        } else if (i < values.size() - 1) {
            return getFormat().parse(values.get(i + 1));
        } else {
            return getFormat().parse(values.get(0)); // roll to first
        }
    }
    
    @Override
    public MiniVar prevStatus() {
        int i = valuesindex.get();
        if (i < 0) {
            return getFormat().parse(values.get(0)); // go to first element
        } else if (i > 0) {
            return getFormat().parse(values.get(i - 1));
        } else {
            return getFormat().parse(values.get(0)); // stay in first
        }  
    }
    
    @Override
    public MiniVar rollPrevStatus() {
        int i = valuesindex.get();
        if (i < 0) { 
            return getFormat().parse(values.get(0)); // go to first element
        } else if (i > 0) {
            return getFormat().parse(values.get(i - 1));
        } else {
            return getFormat().parse(values.get(values.size() - 1)); // roll to last
        }  
    }
    
    @Override
    public boolean hasPrevStatus() {
        return valuesindex.get() > 0;
    }

    @Override
    public boolean hasNextStatus() {
        int i = valuesindex.get();
        return i >= 0 && i < (values.size() - 1);
    }
}
