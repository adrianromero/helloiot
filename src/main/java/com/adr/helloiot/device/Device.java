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

import com.adr.helloiot.MQTTManager;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatIdentity;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author adrian
 */
public abstract class Device {
   
    public static final StringFormat FORMAT = new StringFormatIdentity();
    
    protected ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/resources/devices");
    
    private String id = null; // can be null
    private String subscriptiontopic = null;       
    private int qos = -1;
    private final Properties properties = new Properties();
    
    // The device generic device name
    public abstract String getDeviceName();
    
    public StringFormat getFormat() {
        return FORMAT;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public final String getTopic() {
        return subscriptiontopic;
    }

    public final void setTopic(String topic) {
        this.subscriptiontopic = topic;
    }
    
    public final int getQos() {
        return qos;
    }

    public final void setQos(int qos) {
        this.qos = qos;
    }
    
    public final Properties getProperties() {
        return properties;
    }
    
    // Runtime methods
    public abstract void construct(MQTTManager mqttHelper);
    public abstract void destroy();  
}
