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

import javafx.application.Application.Parameters;
import javafx.scene.layout.StackPane;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class MainManagerClient implements MainManager {

    private HelloIoTApp helloiotapp = null;
    private ClientLoginNode clientlogin = null;
    
    private Parameters params = null;
    private StackPane root = null;
    

    private void showLogin() {
        clientlogin = new ClientLoginNode();
        clientlogin.setURL("tcp://localhost:1883");
        clientlogin.setUserName("");
        clientlogin.setConnectionTimeout(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT);
        clientlogin.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);
        clientlogin.setDefaultQoS(1);
        clientlogin.setVersion(MqttConnectOptions.MQTT_VERSION_DEFAULT);
        clientlogin.setCleanSession(MqttConnectOptions.CLEAN_SESSION_DEFAULT);
        clientlogin.setBrokerPane(0); //none
        clientlogin.setTopicPrefix("");
        clientlogin.setTopicApp("_LOCAL_/_sys_helloIoT/mainapp");
         
        clientlogin.setOnNextAction(e -> {                
            showApplication();
            hideLogin(); 
        });
        root.getChildren().add(clientlogin);        
    }
    
    private void hideLogin() {
        if (clientlogin != null) {
            root.getChildren().remove(clientlogin);
            clientlogin = null;
        }        
    }
    
    private void showApplication() {

        ApplicationConfig config = new ApplicationConfig();
        config.mqtt_url = clientlogin.getURL();
        config.mqtt_username = clientlogin.getUserName();
        config.mqtt_password = clientlogin.getPassword();
        config.mqtt_connectiontimeout = clientlogin.getConnectionTimeout();
        config.mqtt_keepaliveinterval = clientlogin.getKeepAliveInterval();
        config.mqtt_defaultqos = clientlogin.getDefaultQoS();
        config.mqtt_version = clientlogin.getVersion();
        config.mqtt_cleansession = clientlogin.isCleanSession();
        config.mqtt_topicprefix = clientlogin.getTopicPrefix();
        config.mqtt_topicapp = clientlogin.getTopicApp();
        
        config.app_clock = true;
        config.app_exitbutton = true;
        config.app_retryconnection = false;       
        
        helloiotapp = new HelloIoTApp(config);
        
        if (clientlogin.getBrokerPane() == 1) {
            helloiotapp.addFXMLFileDevicesUnits("mosquitto");
        }
        
        helloiotapp.addFXMLFileDevicesUnits("application"); // TODO: Remove

        
        helloiotapp.setOnExitAction(event -> {
            showLogin();            
            hideApplication();            
        });

        root.getChildren().add(helloiotapp.getMQTTNode());
        helloiotapp.startAndConstruct();        
    }
    
    private void hideApplication() {
        if (helloiotapp != null) {
            helloiotapp.stopAndDestroy();
            root.getChildren().remove(helloiotapp.getMQTTNode());
            helloiotapp = null;     
        }        
    }
    
    @Override
    public void construct(StackPane root, Parameters params) {
        this.root = root;
        this.params = params;
        
        showLogin();
    }
    
    @Override
    public void destroy() {
        hideLogin();
        hideApplication();
    }
}
