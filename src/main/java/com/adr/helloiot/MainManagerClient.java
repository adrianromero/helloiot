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
        clientlogin.setConnectionTimeout(30);
        clientlogin.setKeepAliveInterval(60);
        clientlogin.setDefaultQoS(1);
         
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
        config.mqtt_topicprefix = "";
        config.mqtt_topicapp = "_LOCAL_/_sys_helloIoT/mainapp";
        
        config.app_clock = true;
        config.app_exitbutton = true;
        config.app_retryconnection = false;       
        
        helloiotapp = new HelloIoTApp(config);
        
        helloiotapp.addFXMLFileDevicesUnits("application"); // TODO: Remove
        helloiotapp.addFXMLFileDevicesUnits("mosquitto");
        
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
