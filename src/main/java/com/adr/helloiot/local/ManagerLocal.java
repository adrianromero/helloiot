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
package com.adr.helloiot.local;

import com.adr.helloiot.HelloPlatform;
import com.adr.helloiot.ManagerProtocol;
import com.adr.helloiotlib.app.EventMessage;
import com.adr.helloiotlib.format.StringFormatIdentity;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiotlib.format.MiniVar;
import com.adr.helloiotlib.format.MiniVarBoolean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class ManagerLocal implements ManagerProtocol {

    private final static Logger logger = Logger.getLogger(ManagerLocal.class.getName());
    
    private final String fileid;
    
    private Consumer<EventMessage> consumer;    
    private ConcurrentMap<String, byte[]> mapClient;

    public ManagerLocal(String fileid) {
        this.fileid = fileid;
        mapClient = null;
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
        
        readMapClient();                      
        for (Map.Entry<String, byte[]> entry : mapClient.entrySet()) {
            try {
                Map<String, MiniVar> props = new HashMap<>();
                props.put("mqtt.retained", MiniVarBoolean.TRUE);
                EventMessage messagelocal = new EventMessage(entry.getKey(), entry.getValue(), props);
                consumer.accept(messagelocal);
                logger.log(Level.INFO, "Init status: {0}", messagelocal.getTopic());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Cannot publish locally.", ex);
            }                        
        }        
        
    }
    
    @Override
    public void disconnect() {
        // To be invoked by executor thread
        if (mapClient != null) {
            try {
                writeMapClient();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Cannot save client map.", ex);
            }
            mapClient = null;
        }          
    }
    
    @Override
    public void publish(EventMessage message) {
        
        // To be executed in Executor thread
        if (mapClient == null) {
            return;
        }
        
        logger.log(Level.INFO, "Publishing message to local. {0}", message.getTopic());
        CompletableAsync.runAsync(() -> {
            try {
                if (message.getProperty("mqtt.retained").asBoolean()) {
                    mapClient.put(message.getTopic(), message.getMessage());
                }
                consumer.accept(message);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Cannot publish message to local. " + message.getTopic(), ex);
            }
        });        
    }

    @SuppressWarnings("unchecked")
    private void readMapClient() {
        mapClient = null;
        File dbfile = HelloPlatform.getInstance().getFile("localmsg-" + fileid + ".map"); 
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dbfile))) {  
            mapClient = (ConcurrentMap<String, byte[]>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            logger.log(Level.WARNING, String.format("Creating map. Local map file not found: %s.", dbfile));
        }
        
        if (mapClient == null) {
            mapClient = new ConcurrentHashMap<>();
            
            byte[] payloadfirst = StringFormatIdentity.INSTANCE.devalue(StringFormatIdentity.INSTANCE.parse("_first"));
            Map<String, MiniVar> props = new HashMap<>();
            props.put("mqtt.retained", MiniVarBoolean.TRUE);
            EventMessage messagefirst = new EventMessage("unitpage", payloadfirst, props);
            
            mapClient.put(messagefirst.getTopic(), messagefirst.getMessage());
            consumer.accept(messagefirst);
        }
    }
    
    private void writeMapClient() throws IOException {
        File dbfile = HelloPlatform.getInstance().getFile("localmsg-" + fileid + ".map"); 
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dbfile))) {
            out.writeObject(mapClient); 
        }      
    }    
}
