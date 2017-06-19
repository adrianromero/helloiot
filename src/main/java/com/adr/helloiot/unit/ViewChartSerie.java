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

package com.adr.helloiot.unit;

import com.adr.helloiot.device.DeviceNumber;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

/**
 *
 * @author adrian
 */
public class ViewChartSerie {
    
    private final static int SIZE = 60;

    private String label = null;
    private DeviceNumber device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);      
    private final ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
    
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
        updateStatus(null);
    }

    public void destroy() {
        device.unsubscribeStatus(messageHandler);
    }
    
    public XYChart.Series<Number, Number> createSerie() {
        return new XYChart.Series<>(label, data);
    }

    private void updateStatus(byte[] status) {
        
        if (status == null) {
            return;
        }
        
        currentlock.lock();
        try {            
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

            if (data.size() < SIZE) {
                data.add(new XYChart.Data<>(SIZE - data.size(), 0.0));
            }

            for (XYChart.Data<Number, Number> xydata : data) {
                Number aux = xydata.getYValue();
                xydata.setYValue(d);
                d = aux;
            }            
        } finally {
            currentlock.unlock();
        }
    }
}
