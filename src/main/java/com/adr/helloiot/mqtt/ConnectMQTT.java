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

import com.adr.helloiot.ConfigProperties;
import com.adr.helloiot.SSLProtocol;
import com.adr.helloiot.util.CryptUtils;
import com.adr.helloiot.util.FXMLNames;
import com.adr.helloiot.util.HTTPUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
    @FXML private Label labelprotocol;
    @FXML private ChoiceBox<SSLProtocol> protocol;
    @FXML private Label labelkeystore;
    @FXML private TextField keystore;
    @FXML private Label labelkeystorepassword;
    @FXML private PasswordField keystorepassword;
    @FXML private Label labeltruststore;
    @FXML private Label labeltruststorepassword;
    @FXML private TextField truststore;
    @FXML private PasswordField truststorepassword;   
    @FXML private Label labelcredentials;
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
    @FXML private Label labelextendedproperties;
    @FXML private Label labeltimeout;
    @FXML private TextField timeout;
    @FXML private Label labelkeepalive;
    @FXML private TextField keepalive;
    @FXML private Label labelmaxinflight;
    @FXML private TextField maxinflight;
    @FXML private Label labeldefaultqos;
    @FXML private RadioButton qos0;
    @FXML private RadioButton qos1;
    @FXML private RadioButton qos2;
    @FXML private Label labelbrokerpane;
    @FXML private RadioButton brokernone;
    @FXML private RadioButton brokermosquitto;

    public ConnectMQTT() {
        FXMLNames.load(this, "com/adr/helloiot/fxml/connectmqtt");            
    }

    @FXML 
    public void initialize() {
        protocol.setItems(FXCollections.observableArrayList(SSLProtocol.values()));
        host.textProperty().addListener((ov, old_val, new_val) -> disableMQTT(HTTPUtils.getAddress(new_val) == null, ssl.isSelected()));
        ssl.selectedProperty().addListener((ov, old_val, new_val) -> disableMQTT(HTTPUtils.getAddress(host.getText()) == null, new_val));
        disableMQTT(HTTPUtils.getAddress(host.getText()) == null, ssl.isSelected());
    }

    public void loadConfig(ConfigProperties configprops) {
        host.setText(configprops.getProperty("mqtt.host", "localhost"));
        port.setText(configprops.getProperty("mqtt.port", "1883"));
        ssl.setSelected(Boolean.parseBoolean(configprops.getProperty("mqtt.ssl", "false")));
        websockets.setSelected(Boolean.parseBoolean(configprops.getProperty("mqtt.websockets", "false")));
        protocol.getSelectionModel().select(SSLProtocol.valueOfDefault(configprops.getProperty("mqtt.protocol", "TLSv12")));
        keystore.setText(configprops.getProperty("mqtt.keystore", ""));
        keystorepassword.setText(configprops.getProperty("mqtt.keystorepassword", ""));
        truststore.setText(configprops.getProperty("mqtt.truststore", ""));
        truststorepassword.setText(configprops.getProperty("mqtt.truststorepassword", ""));
        username.setText(configprops.getProperty("mqtt.username", ""));
        password.setText(configprops.getProperty("mqtt.password", ""));
        clientid.setText(configprops.getProperty("mqtt.clientid", CryptUtils.generateID()));
        timeout.setText(configprops.getProperty("mqtt.connectiontimeout", Integer.toString(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT)));
        keepalive.setText(configprops.getProperty("mqtt.keepaliveinterval", Integer.toString(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT)));
        maxinflight.setText(configprops.getProperty("mqtt.maxinflight", Integer.toString(MqttConnectOptions.MAX_INFLIGHT_DEFAULT)));
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

        switch (configprops.getProperty("client.broker", "0")) {
        case "1":
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
        configprops.setProperty("mqtt.protocol", protocol.getSelectionModel().getSelectedItem().name());
        configprops.setProperty("mqtt.keystore", keystore.getText());
        configprops.setProperty("mqtt.keystorepassword", keystorepassword.getText());
        configprops.setProperty("mqtt.truststore", truststore.getText());
        configprops.setProperty("mqtt.truststorepassword", truststorepassword.getText());
        configprops.setProperty("mqtt.username", username.getText());
        configprops.setProperty("mqtt.password", password.getText());
        configprops.setProperty("mqtt.clientid", clientid.getText());
        configprops.setProperty("mqtt.connectiontimeout", timeout.getText());
        configprops.setProperty("mqtt.keepaliveinterval", keepalive.getText());
        configprops.setProperty("mqtt.maxinflight", maxinflight.getText());
        configprops.setProperty("mqtt.defaultqos", Integer.toString(getDefaultQoS()));
        configprops.setProperty("mqtt.version", Integer.toString(getVersion()));

        configprops.setProperty("client.broker", getBrokerPane());
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

    public String getBrokerPane() {
        return brokermosquitto.isSelected()
                ? "1"
                : "0";
    }

    private void disableMQTT(boolean value, boolean sslselected) {
        labelport.setDisable(value);
        port.setDisable(value);
        ssl.setDisable(value);
        websockets.setDisable(value);
        labelprotocol.setDisable(value || !sslselected);
        protocol.setDisable(value || !sslselected);
        labelkeystore.setDisable(value || !sslselected);
        keystore.setDisable(value || !ssl.isSelected());
        labelkeystorepassword.setDisable(value || !sslselected);
        keystorepassword.setDisable(value || !sslselected);
        labeltruststore.setDisable(value || !sslselected);
        truststore.setDisable(value || !sslselected);
        labeltruststorepassword.setDisable(value || !sslselected);
        truststorepassword.setDisable(value || !sslselected);
        labelcredentials.setDisable(value);
        labelusername.setDisable(value);
        username.setDisable(value);
        labelpassword.setDisable(value);
        password.setDisable(value);
        labelclientid.setDisable(value);
        clientid.setDisable(value);
        labelversion.setDisable(value);
        versiondefault.setDisable(value);
        version31.setDisable(value);
        version311.setDisable(value);
        labelextendedproperties.setDisable(value);
        labeltimeout.setDisable(value);
        timeout.setDisable(value);
        labelkeepalive.setDisable(value);
        keepalive.setDisable(value);
        labelmaxinflight.setDisable(value);
        maxinflight.setDisable(value);
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
