//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018-2019 Adrián Romero Corchado.
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
package com.adr.helloiot.weather;

import com.adr.helloiot.ManagerProtocol;
import com.adr.helloiot.properties.VarProperties;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.format.MiniVar;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class ManagerTime implements ManagerProtocol {

    private final static Logger logger = Logger.getLogger(ManagerTime.class.getName());
    
    private Consumer<EventMessage> consumer;    
    private Timer timer;

    public ManagerTime(VarProperties properties) {
    }

    @Override
    public void registerTopicsManager(Consumer<EventMessage> consumer, Consumer<Throwable> lost) {
        this.consumer = consumer;
    }
    
    @Override
    public void registerSubscription(String topic, Map<String, MiniVar> messageProperties) {
        // DO NOTHING
    }
    
    @Override
    public void connect() {
        
        disconnect();
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private long last = (System.currentTimeMillis() / 1000L) * 1000L;
            @Override
            public void run() {
                long millis = System.currentTimeMillis();
                while (millis - last > 1000L) {
                    last += 1000L;
                    byte[] payload = Long.toString(last).getBytes(StandardCharsets.UTF_8);
                    consumer.accept(new EventMessage("current", payload));
                }
            }
        }, 0L, 100L);
        
    }
    
    @Override
    public void disconnect() {       
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    @Override
    public void publish(EventMessage message) {
        // DO NOTHING
    }  
}
