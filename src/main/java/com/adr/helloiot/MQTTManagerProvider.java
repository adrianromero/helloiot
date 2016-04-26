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

package com.adr.helloiot;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 *
 * @author adrian
 */
public class MQTTManagerProvider implements Provider<MQTTManager> {
    
    private final static Logger logger = Logger.getLogger(MQTTManagerProvider.class.getName());
    
    private final String url;
    private final String username;
    private final String password;
    private final int timeout;
    private final int keepalive;
    private final int qos;
    private final String topicprefix;            
    private final String topicapp;

    
    @Inject
    public MQTTManagerProvider(
            @Named("mqtt.url") String url, 
            @Named("mqtt.username") String username, 
            @Named("mqtt.password") String password, 
            @Named("mqtt.connectiontimeout") String timeout, 
            @Named("mqtt.keepaliveinterval") String keepalive, 
            @Named("mqtt.qos") String qos,
            @Named("mqtt.topicprefix") String topicprefix,
            @Named("mqtt.topicapp") String topicapp) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.timeout = Integer.parseInt(timeout);
        this.keepalive = Integer.parseInt(keepalive);
        this.qos = Integer.parseInt(qos);
        this.topicprefix = topicprefix;
        this.topicapp = topicapp;
    }
    
    @Override
    public MQTTManager get() {          
        return new MQTTManager(url, username, password, timeout, keepalive, qos, null, topicprefix, topicapp);
    } 
}
