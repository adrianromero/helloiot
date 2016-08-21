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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.client.TopicStatus;
import com.adr.helloiot.unit.UnitPage;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Application.Parameters;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
        config.app_exitbutton = false;
        config.app_retryconnection = false;
        
        helloiotapp = new HelloIoTApp(config);
                
        // add sample panes
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");   
        
        if (clientlogin.getBrokerPane() == 1) {
            UnitPage info = new UnitPage("info", IconBuilder.create(FontAwesome.FA_INFO, 24.0).build(), resources.getString("page.info"));
            helloiotapp.addUnitPages(Arrays.asList(info));            
            helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/mosquitto");
        }
  
        if (clientlogin.isLightsPane()) {
            helloiotapp.addUnitPages(Arrays.asList(
                new UnitPage("light", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 24.0).build(), resources.getString("page.light")))
            );
            helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/samplelights");
        }
        
        if (clientlogin.isGaugesPane()) {
            helloiotapp.addUnitPages(Arrays.asList(
                new UnitPage("temperature", IconBuilder.create(FontAwesome.FA_DASHBOARD, 24.0).build(), resources.getString("page.temperature")))
            );
            helloiotapp.addFXMLFileDevicesUnits("local:com/adr/helloiot/panes/sampletemperature");
        }

        TopicStatus ts = TopicStatus.buildTopicStatus("hello/test1");
        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
        
        ts = TopicStatus.buildTopicStatus("hello/test1", "HEXADECIMAL");
        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
        
        ts = TopicStatus.buildTopicStatus("hello/test1", "INTEGER");
        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
        
        ts = TopicStatus.buildTopicStatus("hello/test1", "BASE64");
        helloiotapp.addDevicesUnits(ts.getDevices(), ts.getUnits());
        
        EventHandler<ActionEvent> showloginevent = (event -> {
            showLogin();            
            hideApplication();            
        });
        helloiotapp.setOnDisconnectAction(showloginevent);
        helloiotapp.getMQTTNode().setToolbarButton(showloginevent, IconBuilder.create(FontAwesome.FA_SIGN_OUT, 18.0).build(), resources.getString("label.disconnect"));

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
