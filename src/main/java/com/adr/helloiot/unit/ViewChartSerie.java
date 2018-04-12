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

package com.adr.helloiot.unit;

import com.adr.helloiot.device.DeviceNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;


/**
 *
 * @author adrian
 */
public class ViewChartSerie {
    
    public final static int SIZE = 60;

    private String label = null;
    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);      
    private final ObservableList<AreaChart.Data<Number, Number>> data = FXCollections.observableArrayList();
    
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public void construct() {
        device.subscribeStatus(messageHandler);
        // Do not update status all values come from messages
    }

    public void destroy() {
        device.unsubscribeStatus(messageHandler);
    }
    
    public AreaChart.Series<Number, Number> createSerie() {
        return new AreaChart.Series<>(label, data);
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
        } finally {
            currentlock.unlock();
        }
    }   
    
    public void tick() {
        
        currentlock.lock();
        try {
            Number d = currentcount == 0.0 ? last : current;
            currentcount = 0.0;
            current = 0.0;
            
            // Prepare the array Construct a new 
            if (data.isEmpty()) {
                List<AreaChart.Data<Number, Number>> initial = new ArrayList<>();
                for (int i = 0; i < SIZE + 1; i++) {
                    initial.add(new AreaChart.Data<>(i, d));
                }
                data.addAll(initial);
            } else {
                for (int i = 0; i < SIZE; i++) {
                    data.get(i).setYValue(data.get(i + 1).getYValue());
                }
                data.get(SIZE).setYValue(d);
            }
        } finally {
            currentlock.unlock();
        }
    }
}
