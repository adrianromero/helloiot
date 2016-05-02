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
import com.adr.helloiot.util.CompletableAsync;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


/**
 *
 * @author adrian
 */
public class TreeStatus extends Device {
    
    protected MQTTManager mqttManager;
    private MQTTManager.Subscription mqttstatus = null;
    
    private final Map<String, String> status = new ConcurrentHashMap<>();
    
    private ScheduledFuture<?> sf = null;    
    private final Object sflock = new Object();  
    
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.treestatus");
    }    
 
    protected void consumeMessage(EventMessage message) {
        status.put(message.getTopic(), message.getMessage());
    }
    
    @Override
    public final void construct(MQTTManager mqttManager) {
        this.mqttManager = mqttManager;
        mqttstatus = mqttManager.subscribe(getTopic() + "/#", getQos());
        mqttstatus.setConsumer(this::consumeMessage);
    }
    
    @Override
    public void destroy() {
        mqttManager.unsubscribe(mqttstatus);
        mqttstatus = null;
    }   
    
    public String readStatus(String branch) {
        return status.get(getTopic() + "/" + branch);
    }
    
    public void sendStatus(String branch, String message) {
        cancelTimer();
        mqttManager.publishStatus(getTopic() + "/" + branch, getQos(), message);
    }
    
    public void sendStatus(String branch, String message, long delay) {            
        synchronized (sflock) {
            cancelTimer();  
            sf = CompletableAsync.scheduleTask(delay, () -> {
                TreeStatus.this.sendStatus(branch, message);
            });
        }
    }    
    public void cancelTimer() {
        synchronized (sflock) {
            if (sf != null) {
                sf.cancel(false);
                sf = null;
            }
        }
    }    
}
