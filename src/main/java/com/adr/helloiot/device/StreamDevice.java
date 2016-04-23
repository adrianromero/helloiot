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

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author adrian
 */
public class StreamDevice {

    private final Stream<Device> devices;    
    
    public StreamDevice(Stream<Device> devices) {
        this.devices = devices;
    }
    
    public void forEach(Consumer<? super Device> action) {
        devices.forEach(action);
    }
     
    public Device getByName(String name) {
        return property("name", name).devices.findAny().get();
    }
            
    public StreamDevice type(String type) {
        return new StreamDevice(devices.filter(device -> type.equals(device.getClass().getSimpleName())));        
    }    
    
    public StreamDevice topic(String topic) {
        return new StreamDevice(devices.filter(device -> topic.equals(device.getTopic())));        
    }
    
    public StreamDevice property(String property, String value) {
        return new StreamDevice(devices.filter(device -> value.equals(device.getProperties().get(property))));
    }
    
    public StreamDevice name(String name) {
        return property("name", name);
    }
    
    public StreamDevice tagged(String tag) {
        return new StreamDevice(devices.filter(device -> {
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
        }));
    }  
}
