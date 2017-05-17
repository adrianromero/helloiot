//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.utils.FXMLUtil;
import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class ClientLoginNode {

    @FXML
    private ResourceBundle resources;

    @FXML
    private BorderPane rootpane;
    @FXML
    private Button nextbutton;

    @FXML
    private TextField host;
    @FXML
    private TextField port;
    @FXML
    private CheckBox ssl;
    @FXML
    private CheckBox websockets;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField clientid;
    @FXML
    private RadioButton versiondefault;
    @FXML
    private RadioButton version311;
    @FXML
    private RadioButton version31;
    @FXML
    private CheckBox cleansession;

    @FXML
    private TextField timeout;
    @FXML
    private TextField keepalive;
    @FXML
    private RadioButton qos0;
    @FXML
    private RadioButton qos1;
    @FXML
    private RadioButton qos2;
    @FXML
    private RadioButton brokernone;
    @FXML
    private RadioButton brokermosquitto;

    @FXML
    private CheckBox mainpage;

    @FXML Button adddeviceunit;
    @FXML Button removedeviceunit;
    @FXML Button updeviceunit;
    @FXML Button downdeviceunit;
    @FXML ListView<TopicInfo> devicesunitslist;
    @FXML ScrollPane deviceunitform;
    
    @FXML ChoiceBox<String> edittype;
    @FXML StackPane topicinfocontainer;
    @FXML ToolBar unitstoolbar;
    
    TopicInfoNode editnode = null;
 
    private TopicInfoBuilder topicinfobuilder;
    
    private String topicapp;
    private String topicprefix;
    private boolean updating = false;

    ClientLoginNode() {
        FXMLUtil.load(this, "/com/adr/helloiot/fxml/clientlogin.fxml", "com/adr/helloiot/fxml/clientlogin");
    }

    @FXML
    public void initialize() {

        nextbutton.setGraphic(IconBuilder.create(FontAwesome.FA_PLAY, 18.0).styleClass("icon-fill").build());

        adddeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_PLUS, 18.0).build());
        removedeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_MINUS, 18.0).build());
        updeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_UP, 18.0).build());
        downdeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_DOWN, 18.0).build());

        edittype.setItems(FXCollections.observableArrayList("Publication/Subscription", "Subscription", "Publication", "Code"));
        edittype.getSelectionModel().clearSelection();
        edittype.valueProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });

        constructSampleButtons();

        devicesunitslist.setCellFactory((ListView<TopicInfo> list) -> new ListCell<TopicInfo>() {
            @Override
            public void updateItem(TopicInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(item.getGraphic());
                    String label = item.getLabel();
                    setText((label == null || label.isEmpty()) ? resources.getString("label.empty") : label);
                }
            }
        });
        devicesunitslist.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TopicInfo> ov, TopicInfo old_val, TopicInfo new_val) -> {
            updateDevicesUnitsList();
        });
        updateDevicesUnitsList();

        Platform.runLater(host::requestFocus);
    }
    
    private void constructSampleButtons() {
        unitstoolbar.getItems().addAll(
                new Label(resources.getString("label.samplestitle")),
                createSamplesButton("samples.lights", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 18.0).build(), "com/adr/helloiot/samples/lights.fxml"),
                createSamplesButton("samples.chart", IconBuilder.create(FontAwesome.FA_BAR_CHART, 18.0).build(), "com/adr/helloiot/samples/chart.fxml"),
                createSamplesButton("samples.numbers", IconBuilder.create(FontAwesome.FA_DASHBOARD, 18.0).build(), "com/adr/helloiot/samples/numbers.fxml"));
    }

    public Node getNode() {
        return rootpane;
    }

    private void updateCurrentTopic() {
        if (!updating) {
            int index = devicesunitslist.getSelectionModel().getSelectedIndex();        
            TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
            String type = edittype.getValue();
            if (!topic.getType().equals(type)) {
                // This is just an optimization. Most of the times we can reuse current TopicInfo
                // Create a new TopicInfo, we cannot reuse current one
                topic = topicinfobuilder.create(type);
                
                updating = true;
                                
                TopicInfoNode node = topic.getEditNode();
                if (editnode != null && node != editnode) {
                    editnode.useUpdateCurrent(null);
                    topicinfocontainer.getChildren().remove(editnode.getNode());
                    editnode = null;
                }
                if (editnode == null) {
                    editnode = node;
                    editnode.useUpdateCurrent(this::updateCurrentTopic);
                    topicinfocontainer.getChildren().add(editnode.getNode());                
                }           
                // TopicInfo -> TopicInfoNode
                topic.writeToEditNode(); 
                updating = false;
            } else {      
                // TopicInfoNode -> TopicInfo
                topic.readFromEditNode();
            }

            devicesunitslist.getItems().set(index, topic);
            devicesunitslist.getSelectionModel().select(topic);
        }
    }

    private void updateDevicesUnitsList() {
        TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        if (topic == null) {
            removedeviceunit.setDisable(true);
            deviceunitform.setDisable(true);
            updeviceunit.setDisable(true);
            downdeviceunit.setDisable(true);

            updating = true;    
            edittype.getSelectionModel().clearSelection();
            if (editnode != null) {
                editnode.useUpdateCurrent(null);
                topicinfocontainer.getChildren().remove(editnode.getNode());
                editnode = null;
            }            
            updating = false;
        } else {
            removedeviceunit.setDisable(false);
            deviceunitform.setDisable(false);
            updeviceunit.setDisable(index <= 0);
            downdeviceunit.setDisable(index >= devicesunitslist.getItems().size() - 1);

            updating = true;
            edittype.getSelectionModel().select(topic.getType());
            
            TopicInfoNode node = topic.getEditNode();
            if (editnode != null && node != editnode) {
                editnode.useUpdateCurrent(null);
                topicinfocontainer.getChildren().remove(editnode.getNode());
                editnode = null;
            }
            if (editnode == null) {
                editnode = node;
                editnode.useUpdateCurrent(this::updateCurrentTopic);
                topicinfocontainer.getChildren().add(editnode.getNode());                
            }          
            // TopicInfo -> TopicInfoNode
            topic.writeToEditNode(); 
            updating = false;
        }
    }

    @FXML
    void onAddDeviceUnit(ActionEvent event) {
        TopicInfo t = topicinfobuilder.create();
        devicesunitslist.getItems().add(t);
        devicesunitslist.getSelectionModel().select(t);
    }

    @FXML
    void onRemoveDeviceUnit(ActionEvent event) {
        TopicInfo t = devicesunitslist.getSelectionModel().getSelectedItem();
        devicesunitslist.getItems().remove(t);

    }

    @FXML
    void onUpDeviceUnit(ActionEvent event) {
        TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        devicesunitslist.getItems().remove(index);
        devicesunitslist.getItems().add(index - 1, topic);
        devicesunitslist.getSelectionModel().select(index - 1);
    }

    @FXML
    void onDownDeviceUnit(ActionEvent event) {
        TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
        int index = devicesunitslist.getSelectionModel().getSelectedIndex();
        devicesunitslist.getItems().remove(index);
        devicesunitslist.getItems().add(index + 1, topic);
        devicesunitslist.getSelectionModel().select(index + 1);
    }
    
    public void setOnNextAction(EventHandler<ActionEvent> exitevent) {
        nextbutton.setOnAction(exitevent);
    }

    public String getHost() {
        return host.getText();
    }

    public void setHost(String value) {
        host.setText(value);
    }

    public String getPort() {
        return port.getText();
    }

    public void setPort(String value) {
        port.setText(value);
    }

    public boolean isSSL() {
        return ssl.isSelected();
    }

    public void setSSL(boolean value) {
        ssl.setSelected(value);
    }

    public boolean isWebSockets() {
        return websockets.isSelected();
    }

    public void setWebSockets(boolean value) {
        websockets.setSelected(value);
    }

    public String getUserName() {
        return username.getText();
    }

    public String getURL() {
        String protocol = websockets.isSelected()
                ? (ssl.isSelected() ? "wss" : "ws")
                : (ssl.isSelected() ? "ssl" : "tcp");
        return protocol + "://" + host.getText() + ":" + port.getText();
    }

    public void setUserName(String value) {
        username.setText(value);
    }

    public String getPassword() {
        return password.getText();
    }

    public String getClientID() {
        return clientid.getText();
    }

    public void setClientID(String value) {
        clientid.setText(value);
    }

    public int getConnectionTimeout() {
        return Integer.parseInt(timeout.getText());
    }

    public void setConnectionTimeout(int value) {
        timeout.setText(Integer.toString(value));
    }

    public int getKeepAliveInterval() {
        return Integer.parseInt(keepalive.getText());
    }

    public void setKeepAliveInterval(int value) {
        keepalive.setText(Integer.toString(value));
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

    public void setDefaultQoS(int value) {
        switch (value) {
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
    }

    public String getTopicPrefix() {
        return topicprefix;
    }

    public void setTopicPrefix(String value) {
        topicprefix = value;
    }

    public String getTopicApp() {
        return topicapp;
    }

    public void setTopicApp(String value) {
        topicapp = value;
    }

    public int getBrokerPane() {
        if (brokermosquitto.isSelected()) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setBrokerPane(int value) {
        switch (value) {
        case 1:
            brokermosquitto.setSelected(true);
            break;
        default:
            brokernone.setSelected(true);
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

    public void setVersion(int value) {
        switch (value) {
        case MqttConnectOptions.MQTT_VERSION_3_1_1:
            version311.setSelected(true);
            break;
        case MqttConnectOptions.MQTT_VERSION_3_1:
            version31.setSelected(true);
            break;
        default:
            versiondefault.setSelected(true);
        }
    }

    public ObservableList<TopicInfo> getTopicInfoList() {
        return devicesunitslist.getItems();
    }

    public void setTopicInfoList(TopicInfoBuilder topicinfobuilder, ObservableList<TopicInfo> list) {
        this.topicinfobuilder = topicinfobuilder;
        devicesunitslist.setItems(list);
        if (list.size() > 0) {
            devicesunitslist.getSelectionModel().select(0);
        }
    }

    public boolean isCleanSession() {
        return cleansession.isSelected();
    }

    public void setCleanSession(boolean value) {
        cleansession.setSelected(value);
    }

    public boolean isMainPage() {
        return mainpage.isSelected();
    }

    public void setMainPage(boolean value) {
        mainpage.setSelected(value);
    }
    
    public Button createSamplesButton(String key, Node graphic, String fxml) {
        Button b = new Button(resources.getString(key), graphic);
        b.setFocusTraversable(false);        
        b.setOnAction(e -> {
            try {
                TopicInfo t = topicinfobuilder.create("Code");
                BaseSubProperties props = new BaseSubProperties();
                props.setProperty(".name", resources.getString(key));
                props.setProperty(".code", Resources.toString(Resources.getResource(fxml), StandardCharsets.UTF_8));
                t.load(props);
                
                devicesunitslist.getItems().add(t);
                devicesunitslist.getSelectionModel().select(t);
            } catch (IOException ex) {
                Logger.getLogger(ClientLoginNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return b;
    }
}
