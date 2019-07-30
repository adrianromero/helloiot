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
package com.adr.helloiot.weather;

import com.adr.helloiot.Bridge;
import com.adr.helloiot.ConnectUI;
import com.adr.helloiot.ManagerProtocol;
import com.adr.helloiot.SubProperties;
import com.adr.helloiot.properties.VarProperties;

public class BridgeTime implements Bridge  {

    @Override
    public ConnectUI createConnectUI() {
        return null;
    }

    @Override
    public boolean hasManager(VarProperties properties) {
        return true;
    }

    @Override
    public ManagerProtocol createManager(VarProperties properties) {
        return new ManagerTime(properties);
    }

    @Override
    public void readConfiguration(VarProperties config, SubProperties configprops) {
    } 
}
