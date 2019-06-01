//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
package com.adr.helloiot.properties;

import com.adr.helloiot.BridgeConfig;
import com.adr.helloiot.ConfigProperties;
import com.adr.helloiot.ConfigSubProperties;
import com.adr.helloiotlib.format.MiniVar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adrian
 */
public class VarProperties {
    
    private final Map<String, MiniVar> map;
    private final String prefix;
    
    public VarProperties() {
        this.map = new HashMap<>();
        this.prefix = "";
    }
    
    public VarProperties(VarProperties config, String prefix) {
        this.map = config.map;
        this.prefix = config.prefix + prefix;
    }
    
    public MiniVar get(String name) {
        return map.get(prefix + name);
    }  
    
    public void put(String name, MiniVar value) {
        map.put(prefix + name, value);
    }
    
    public final void readConfiguration(BridgeConfig bridgeconfig, ConfigProperties configprops) {
        bridgeconfig.getBridge().readConfiguration(
                new VarProperties(this, bridgeconfig.getPrefix()), 
                new ConfigSubProperties(configprops, bridgeconfig.getPrefix()));
    }
}
