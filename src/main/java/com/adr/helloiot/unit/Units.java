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

import com.adr.helloiot.EventMessage;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;

/**
 *
 * @author adrian
 */
public class Units {
    
    public static Object messageHandler(StatusListener listener) {
        return new StatusSubscription(listener);
    }
      
    @FunctionalInterface
    public static interface StatusListener {
        public void updateStatus(byte[] status);        
    }
            
    private static class StatusSubscription {
        private final StatusListener listener;
        
        public StatusSubscription(StatusListener listener) {
            this.listener = listener;
        }
        
        @Subscribe
        public void receivedStatus(EventMessage message) {
            Platform.runLater(() -> listener.updateStatus(message.getMessage()));   
        }       
    }
}
