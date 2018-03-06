//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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
package com.adr.helloiot.mqtt;

import com.adr.hellocommon.utils.FXMLUtil;
import com.adr.helloiot.ConfigProperties;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.device.format.MiniVarBoolean;
import com.adr.helloiot.device.format.MiniVarInt;
import com.adr.helloiot.device.format.MiniVarString;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class ConnectMQTT {
    
    @FXML private GridPane root;
    @FXML private Label labelhost;
    @FXML private TextField host;
    @FXML private Label labelport;
    @FXML private TextField port;
    @FXML private CheckBox ssl;
    @FXML private CheckBox websockets;
    @FXML private Label labelusername;
    @FXML private TextField username;
    @FXML private Label labelpassword;
    @FXML private PasswordField password;
    @FXML private Label labelclientid;
    @FXML private TextField clientid;
    @FXML private Label labelversion;
    @FXML private RadioButton versiondefault;
    @FXML private RadioButton version311;
    @FXML private RadioButton version31;
    @FXML private CheckBox cleansession;
    @FXML private Label labelextendedproperties;
    @FXML private Label labeltimeout;
    @FXML private TextField timeout;
    @FXML private Label labelkeepalive;
    @FXML private TextField keepalive;
    @FXML private Label labeldefaultqos;
    @FXML private RadioButton qos0;
    @FXML private RadioButton qos1;
    @FXML private RadioButton qos2;
    @FXML private Label labelbrokerpane;
    @FXML private RadioButton brokernone;
    @FXML private RadioButton brokermosquitto;    
 
    
    public ConnectMQTT() {
        FXMLUtil.load(this, "/com/adr/helloiot/fxml/connectmqtt.fxml", "com/adr/helloiot/fxml/connectmqtt");
    }    
    
    @FXML
    public void initialize() {
        host.textProperty().addListener((ov, old_val, new_val) ->  disableMQTT(new_val.isEmpty()));
        disableMQTT(host.getText().isEmpty());
    }
    
    public void loadConfig(ConfigProperties configprops) {
        host.setText(configprops.getProperty("mqtt.host", "localhost"));
        port.setText(configprops.getProperty("mqtt.port", "1883"));
        ssl.setSelected(Boolean.parseBoolean(configprops.getProperty("mqtt.ssl", "false")));
        websockets.setSelected(Boolean.parseBoolean(configprops.getProperty("mqtt.websockets", "false")));
        username.setText(configprops.getProperty("mqtt.username", ""));
        password.setText(configprops.getProperty("mqtt.password", ""));
        clientid.setText(configprops.getProperty("mqtt.clientid", ""));
        timeout.setText(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT)));
        keepalive.setText(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT)));
        switch (Integer.parseInt(configprops.getProperty("mqtt.defaultqos", "1"))) {
        case 1:
            qos1.setSelected(true);
            break;
        case 2:
            qos2.setSelected(true);
            break;
        default:
            qos0.setSelected(true);
            break;
        }
        switch (Integer.parseInt(configprops.getProperty("mqtt.version", Integer.toString(MqttConnectOptions.MQTT_VERSION_DEFAULT)))) {
        case MqttConnectOptions.MQTT_VERSION_3_1_1:
            version311.setSelected(true);
            break;
        case MqttConnectOptions.MQTT_VERSION_3_1:
            version31.setSelected(true);
            break;
        default:
            versiondefault.setSelected(true);
        }        
        cleansession.setSelected(Boolean.parseBoolean(configprops.getProperty("mqtt.cleansession", Boolean.toString(MqttConnectOptions.CLEAN_SESSION_DEFAULT))));
        
        switch (Integer.parseInt(configprops.getProperty("client.broker", "0"))) {
        case 1:
            brokermosquitto.setSelected(true);
            break;
        default:
            brokernone.setSelected(true);
        }        
    }    
    
    public void saveConfig(ConfigProperties configprops) {
        configprops.setProperty("mqtt.host", host.getText());
        configprops.setProperty("mqtt.port", port.getText());
        configprops.setProperty("mqtt.ssl", Boolean.toString(ssl.isSelected()));
        configprops.setProperty("mqtt.websockets", Boolean.toString(websockets.isSelected()));
        configprops.setProperty("mqtt.clientid", clientid.getText());
        configprops.setProperty("mqtt.connectiontimeout", timeout.getText());
        configprops.setProperty("mqtt.keepaliveinterval", keepalive.getText());     
        configprops.setProperty("mqtt.defaultqos", Integer.toString(getDefaultQoS()));     
        configprops.setProperty("mqtt.version", Integer.toString(getVersion()));
        configprops.setProperty("mqtt.cleansession", Boolean.toString(cleansession.isSelected()));    
        
        configprops.setProperty("client.broker", Integer.toString(getBrokerPane()));        
    }
    
    public void applyConfig(Map<String, MiniVar> config) {
        config.put("mqtt.host", new MiniVarString(host.getText()));
        config.put("mqtt.port", new MiniVarInt(Integer.parseInt(port.getText())));
        config.put("mqtt.ssl", new MiniVarBoolean(ssl.isSelected()));
        config.put("mqtt.websockets", new MiniVarBoolean(websockets.isSelected()));
        config.put("mqtt.username", new MiniVarString(username.getText()));
        config.put("mqtt.password", new MiniVarString(password.getText()));
        config.put("mqtt.clientid", new MiniVarString(clientid.getText()));
        config.put("mqtt.connectiontimeout", new MiniVarInt(Integer.parseInt(timeout.getText())));
        config.put("mqtt.keepaliveinterval", new MiniVarInt(Integer.parseInt(keepalive.getText())));
        config.put("mqtt.defaultqos", new MiniVarInt(getDefaultQoS()));
        config.put("mqtt.version", new MiniVarInt(getVersion()));
        config.put("mqtt.cleansession", new MiniVarBoolean(cleansession.isSelected()));  
        
        config.put("client.broker", new MiniVarInt(getBrokerPane()));
        
        username.setText("");
        password.setText("");        
    }  
    
    public int getDefaultQoS() {
        if (qos1.isSelected()) {
            return 1;
        } else if (qos2.isSelected()) {
            return 2;
        } else {
            return 0;
        }
    }    
    
    public int getVersion() {
        if (version311.isSelected()) {
            return MqttConnectOptions.MQTT_VERSION_3_1_1;
        } else if (version31.isSelected()) {
            return MqttConnectOptions.MQTT_VERSION_3_1;
        } else {
            return MqttConnectOptions.MQTT_VERSION_DEFAULT;
        }
    }
    
    public int getBrokerPane() {
        if (brokermosquitto.isSelected()) {
            return 1;
        } else {
            return 0;
        }
    }
    
    private void disableMQTT(boolean value) {
        labelport.setDisable(value);
        port.setDisable(value);
        ssl.setDisable(value);
        websockets.setDisable(value);
        labelusername.setDisable(value);
        username.setDisable(value);
        labelpassword.setDisable(value);
        password.setDisable(value);
        labelclientid.setDisable(value);
        clientid.setDisable(value);
        cleansession.setDisable(value);
        labelversion.setDisable(value);
        versiondefault.setDisable(value);
        version31.setDisable(value);
        version311.setDisable(value);
        labelextendedproperties.setDisable(value);
        labeltimeout.setDisable(value);
        timeout.setDisable(value);
        labelkeepalive.setDisable(value);
        keepalive.setDisable(value);
        labeldefaultqos.setDisable(value);
        qos0.setDisable(value);
        qos1.setDisable(value);
        qos2.setDisable(value);
        labelbrokerpane.setDisable(value);
        brokernone.setDisable(value);
        brokermosquitto.setDisable(value);
    }
    
    public Node getNode() {
        return root;
    }      
}
