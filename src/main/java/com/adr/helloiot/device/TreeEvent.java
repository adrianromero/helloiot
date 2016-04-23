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
import com.adr.helloiot.util.CompletableAsync;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author adrian
 */
public class TreeEvent extends Device {
    
    protected MQTTManager mqttManager;
    private ScheduledFuture<?> sf = null;
    private final Object sflock = new Object();  
    
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.treeevent");
    }
    
    @Override
    public final void construct(MQTTManager mqttManager) {
        this.mqttManager = mqttManager;
    }
    @Override
    public final void destroy() {       
    }    
    
    public final void sendEvent(String branch, String message) {
        cancelTimer();
        mqttManager.publishEvent(getTopic() + "/" + branch, message);
    }  
    public void sendEvent(String branch, String message, long delay) {            
        synchronized (sflock) {
            cancelTimer();  
            sf = CompletableAsync.scheduleTask(delay, () -> {
                sendEvent(branch, message);
            });
        }
    }
    public final void sendEvent(String branch) {
        sendEvent(branch, "");
    }  
    public void sendEvent(String branch, long delay) {
        TreeEvent.this.sendEvent(branch, "", delay);
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
