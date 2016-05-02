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

import com.adr.helloiot.EventMessage;
import com.adr.helloiot.MQTTManager;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatIdentity;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.graphic.IconText;
import com.google.common.eventbus.EventBus;

/**
 *
 * @author adrian
 */
public abstract class DeviceBase extends Device {
   
    public static final StringFormat FORMAT = new StringFormatIdentity();
    
    protected MQTTManager mqttHelper;
    private MQTTManager.Subscription mqttstatus = null;
    
    private final EventBus statusbus = new EventBus();
    
    public StringFormat getFormat() {
        return FORMAT;
    }
    
    public IconStatus getIconStatus() {
        return new IconText(getFormat());
    }
    
    protected void consumeMessage(EventMessage message) {
    }
    
    @Override
    public final void construct(MQTTManager mqttHelper) {
        this.mqttHelper = mqttHelper;
        mqttstatus = mqttHelper.subscribe(getTopic(), getQos());
        mqttstatus.setConsumer((message) -> {
            consumeMessage(message); 
            statusbus.post(message);
        });
    }
    
    @Override
    public void destroy() {
        mqttHelper.unsubscribe(mqttstatus);
        mqttstatus = null;
    }   
    
    public void subscribeStatus(Object observer) {
        statusbus.register(observer);
    }
    
    public void unsubscribeStatus(Object observer) {
        statusbus.unregister(observer);   
    } 
}
