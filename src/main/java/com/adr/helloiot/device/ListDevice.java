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
package com.adr.helloiot.device;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adrian
 */
public class ListDevice {

    private final List<Device> devices;

    public ListDevice(List<Device> devices) {
        this.devices = devices;
    }

    public void forEach(DeviceProvider<? super Device> action) {
        for (Device d : devices) {
            action.accept(d);
        }
    }

    public Device getByName(String name) {      
        for (Device d: devices) {
            if (name.equals(d.getProperties().getProperty("name"))) {
                return d;
            }
        }
        return null;
    }

    public ListDevice type(String type) {
        return filter(device -> type.equals(device.getClass().getSimpleName()));
    }

    public ListDevice topic(String topic) {
        return filter(device -> topic.equals(device.getTopic()));
    }

    public ListDevice property(String property, String value) {
        return filter(device -> value.equals(device.getProperties().get(property)));
    }

    public ListDevice name(String name) {
        return property("name", name);
    }

    public ListDevice tagged(String tag) {
        return filter(device -> {
            String tags = device.getProperties().getProperty("tags");
            if (tags == null) {
                return false;
            }

            String[] tagsarray = tags.split("\\s+");
            for (int i = 0; i < tagsarray.length; i++) {
                if (tag.equals(tagsarray[i])) {
                    return true;
                }
            }
            return false;
        });
    }
    
    private ListDevice filter(DevicePredicate<Device> p) {
        List<Device> newlist = new ArrayList<>();
        for (Device d : devices) {
            if (p.test(d)) {
                newlist.add(d);
            }
        }
        return new ListDevice(newlist);
    }
}
