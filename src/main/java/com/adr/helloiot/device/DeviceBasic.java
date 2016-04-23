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
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author adrian
 */
public class DeviceBasic extends DeviceBase {

    private final AtomicReference<String> status = new AtomicReference<>(null); 

    // Overwrite this  method
    @Override
    public String getDeviceName() {
        return resources.getString("devicename.devicebasic");
    } 
    
    @Override
    protected void consumeMessage(EventMessage message) {
        status.set(message.getMessage());
    }    
    
    public String readStatus() {
        return status.get();
    }             
}
