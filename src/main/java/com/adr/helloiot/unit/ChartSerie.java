//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
package com.adr.helloiot.unit;

import com.adr.helloiotlib.unit.Units;
import com.adr.helloiot.device.DeviceNumber;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChartSerie {

    public final static int SIZE = 30;
    
    private String label = null;
    private String styleClass = "unitchartserie";
    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);      
    private final List<Double> data = new LinkedList<>();
    
    private ChartSerieListener listener = null;
    
    private boolean hasValues = false;
    private double current = 0.0;
    private double last = 0.0;
    private double currentcount = 0.0;
    private final Lock currentlock = new ReentrantLock();
    
    public DeviceNumber getDevice() {
        return device;
    }

    public void setDevice(DeviceNumber device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }        
    }
    
    public void setListener(ChartSerieListener listener) {
        this.listener = listener;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public List<Double> getData() {
        return Collections.unmodifiableList(data);
    }
    
    public void construct() {
        data.clear();
        if (listener != null) {
            listener.handleData();
        }
        
        device.subscribeStatus(messageHandler);
        // Do not update status all values come from messages
    }

    public void destroy() {
        device.unsubscribeStatus(messageHandler);
        data.clear();
        if (listener != null) {
            listener.handleData();
        }
    }

    private void updateStatus(byte[] status) {
        
        if (status == null) {
            return;
        }
        
        currentlock.lock();
        try {
            if (device.getFormat().value(status).isEmpty()) {
                return; // ignore empty values instead of defaulting to zero
            }
            last = device.getFormat().value(status).asDouble();
            if (currentcount == 0.0) {
                currentcount = 1.0;
                current = last;
            } else  {
                currentcount += 1.0;
                current = current * ((currentcount - 1.0) / currentcount) + last / currentcount;
            }
            hasValues = true;
        } finally {
            currentlock.unlock();
        }
    }   
    
    private double tickValue() {
        double d = currentcount == 0.0 ? last : current;
        currentcount = 0.0;
        current = 0.0;    
        
        // Adjust Value
        d = (d - device.getLevelMin()) / (device.getLevelMax() - device.getLevelMin());

        // Clamp value between 0 and 1;
        d = Math.max(Math.min(d, 1.0), 0.0);
        
        return d;
    }
    
    public void tick() {
        
        currentlock.lock();
        try {
            if (!hasValues) {
                return;
            }
            
            double d = tickValue();

            if (data.isEmpty()) {
                for (int i = 0; i < SIZE; i++) {
                    data.add(d);
                }
            } else {
                data.add(d);
                data.remove(0);
            }
        } finally {
            currentlock.unlock();
        }

        if (listener != null) {
            listener.handleData();
        }      
    }
}
