//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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
package com.adr.helloiot;

import com.adr.helloiot.local.BridgeLocal;
import com.adr.helloiot.mqtt.BridgeMQTT;
import com.adr.helloiot.properties.VarProperties;
import com.adr.helloiotlib.format.MiniVarBoolean;
import com.adr.helloiotlib.format.MiniVarString;
import java.io.IOException;
import java.util.Map;
import javafx.application.Application.Parameters;
import javafx.scene.layout.StackPane;

public class MainManagerPlatform implements MainManager {

    private HelloIoTApp helloiotapp = null;

    @Override
    public void construct(StackPane root, Parameters params) {
        
        BridgeConfig[] bridgeconfigs = new BridgeConfig[] {
            // new BridgeConfig(new BridgeTradfri(), "TRÅDFRI/", "tradfri."),
            // new BridgeConfig(new BridgeTime(), "SYSTEM/time/", "time."),
            new BridgeConfig(new BridgeLocal(), "_LOCAL_/mainapp/", "local."),
            new BridgeConfig(new BridgeMQTT(), "", "mqtt.")};      

        ConfigProperties configprops = new ConfigProperties();
        try {
            configprops.load(() -> getClass().getResourceAsStream("/META-INF/.helloiot-config.properties"));
        } catch (IOException ex) {
            throw new RuntimeException("Configuration file cannot be loaded.", ex);
        }

        // Mix named parameters into configprops
        Map<String, String> named = params.getNamed();
        named.forEach((String key, String value) -> {
            configprops.setProperty(key, value);
        });   
        
        VarProperties config = new VarProperties();
        
        for (BridgeConfig bc : bridgeconfigs) {
            config.readConfiguration(bc, configprops);
        }

        config.put("app.topicsys", new MiniVarString(configprops.getProperty("app.topicsys", "system/")));
        config.put("app.topicapp", new MiniVarString(configprops.getProperty("app.topicapp", "_LOCAL_/mainapp/")));
        config.put("app.exitbutton", new MiniVarBoolean(Boolean.parseBoolean(configprops.getProperty("app.exitbutton", "true"))));
        config.put("app.retryconnection", MiniVarBoolean.TRUE);
        Style.changeStyle(root, Style.valueOf(configprops.getProperty("app.style", Style.PRETTY.name())));

        try {
            helloiotapp = new HelloIoTApp(bridgeconfigs, config);
        } catch (HelloIoTException ex) {
            throw new RuntimeException("HelloIoT application cannot be loaded.", ex);
        }

        // Add all devices and units
        helloiotapp.addServiceDevicesUnits();
        
        helloiotapp.setOnDisconnectAction(event -> {
            root.getScene().getWindow().hide();
        });

        root.getChildren().add(helloiotapp.getMainNode().getNode());
        helloiotapp.startAndConstruct();
    }

    @Override
    public void destroy() {
        helloiotapp.stopAndDestroy();
        helloiotapp = null;
    }
}
