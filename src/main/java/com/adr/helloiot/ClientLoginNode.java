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
import com.adr.hellocommon.utils.AbstractController;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatBase64;
import com.adr.helloiot.device.format.StringFormatDecimal;
import com.adr.helloiot.device.format.StringFormatHex;
import com.adr.helloiot.device.format.StringFormatIdentity;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 *
 * @author adrian
 */
public class ClientLoginNode extends BorderPane implements AbstractController {
    
    @FXML private ResourceBundle resources;
    
    @FXML
    private HBox headerbox;
    @FXML
    private Label headertitle;
    @FXML
    private Button nextbutton;


    @FXML
    private TextField url;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
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
    private TextField topicprefix;
    @FXML
    private TextField topicapp;
    @FXML
    private RadioButton brokernone;
    @FXML
    private RadioButton brokermosquitto;  
    
    @FXML
    private CheckBox gaugespane;
    @FXML
    private CheckBox lightspane;    
    @FXML
    private CheckBox mainpage;    
    
    @FXML Button adddeviceunit;
    @FXML Button removedeviceunit;
    @FXML Button updeviceunit;
    @FXML Button downdeviceunit;
    @FXML ListView<TopicInfo> devicesunitslist;
    @FXML ScrollPane deviceunitform;
    
    @FXML TextField edittopic;
    @FXML TextField edittopicpub;
    @FXML ChoiceBox<String> edittype;
    @FXML ChoiceBox<StringFormat> editformat;
    @FXML ChoiceBox<Integer> editqos;
    @FXML ChoiceBox<Boolean> editretained;
    @FXML CheckBox editmultiline;
    
    private boolean updating = false;

    ClientLoginNode() {
        load("/com/adr/helloiot/fxml/clientlogin.fxml", "com/adr/helloiot/fxml/clientlogin");
    }

    @FXML
    public void initialize() {
        
        adddeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_PLUS, 18.0).build());
        removedeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_MINUS, 18.0).build());
        updeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_UP, 18.0).build());
        downdeviceunit.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_DOWN, 18.0).build());
        
        edittopic.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });
        edittopicpub.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });
        
        editqos.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object < 0) { 
                    return resources.getString("label.default");
                } else {
                    return object.toString();
                }
            }

            @Override
            public Integer fromString(String string) {
                return Integer.getInteger(string);
            }
        });
        editqos.setItems(FXCollections.observableArrayList(-1, 0, 1, 2));
        editqos.getSelectionModel().select(0);
        
        editformat.setItems(FXCollections.observableArrayList(
            StringFormatIdentity.INSTANCE,
            StringFormatDecimal.INTEGER,        
            StringFormatDecimal.DOUBLE, 
            StringFormatDecimal.DECIMAL, 
            StringFormatDecimal.DEGREES, 
            StringFormatBase64.INSTANCE,
            StringFormatHex.INSTANCE));
        editformat.getSelectionModel().select(0);
        
        edittype.setItems(FXCollections.observableArrayList("Subscription", "Publication", "Publication/Subscription"));
        edittype.getSelectionModel().clearSelection();
        edittype.valueProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });
        
        editretained.setConverter(new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean object) {
                if (object == null) { 
                    return resources.getString("label.default");
                } else {
                    return object.toString();
                }
            }

            @Override
            public Boolean fromString(String value) {
                if (Boolean.TRUE.toString().equals(value)) {
                    return Boolean.TRUE;
                } else if (Boolean.FALSE.toString().equals(value)) {
                    return Boolean.FALSE;
                } else {
                    return null;
                }
            }
        });        
        editretained.setItems(FXCollections.observableArrayList(null, Boolean.TRUE, Boolean.FALSE));
        editretained.getSelectionModel().select(0);
        
        devicesunitslist.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TopicInfo> ov, TopicInfo old_val, TopicInfo new_val) -> {
            updateDevicesUnitsList();
        });
        updateDevicesUnitsList();
        
        nextbutton.setGraphic(IconBuilder.create(FontAwesome.FA_PLAY, 18.0).build());
        Platform.runLater(url::requestFocus);
    }
    
    private void updateCurrentTopic() {
        if (!updating) {
            int  index = devicesunitslist.getSelectionModel().getSelectedIndex();
            TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();
            
            topic.setTopic(edittopic.getText());
            topic.setTopicpub(edittopicpub.getText() == null || edittopicpub.getText().isEmpty() ? null : edittopicpub.getText());
            topic.setType(edittype.getValue());
            
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
            edittopic.setText(null);
            edittopicpub.setText(null);
            edittype.getSelectionModel().clearSelection();
            updating = false;            
            
        } else {
            removedeviceunit.setDisable(false);
            deviceunitform.setDisable(false);
            updeviceunit.setDisable(index <= 0);
            downdeviceunit.setDisable(index >= devicesunitslist.getItems().size() - 1);
            
            updating = true;
            edittopic.setText(topic.getTopic());
            edittopicpub.setText(topic.getTopicpub());
            edittype.getSelectionModel().select(topic.getType());            
            updating = false;
        }
    }

    @FXML
    void onAddDeviceUnit(ActionEvent event) {
        TopicInfo t = new TopicInfo();
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

    public String getURL() {
        return url.getText();
    }

    public void setURL(String value) {
        url.setText(value);
    }

    public String getUserName() {
        return username.getText();
    }

    public void setUserName(String value) {
        username.setText(value);
    }

    public String getPassword() {
        return password.getText();
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
        return topicprefix.getText();
    }
    
    public void setTopicPrefix(String value) {
        topicprefix.setText(value);
    }
    
    public String getTopicApp() {
        return topicapp.getText();
    }
    
    public void setTopicApp(String value) {
        topicapp.setText(value);
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
    
    public void setTopicInfoList(ObservableList<TopicInfo> list) {
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
    
    public boolean isLightsPane() {
        return lightspane.isSelected();
    }
    
    public void setLightsPane(boolean value) {
        lightspane.setSelected(value);
    }    
    
    public boolean isGaugesPane() {
        return gaugespane.isSelected();
    }
    
    public void setGaugesPane(boolean value) {
        gaugespane.setSelected(value);
    }    
    
    public boolean isMainPage() {
        return mainpage.isSelected();
    }
    
    public void setMainPage(boolean value) {
        mainpage.setSelected(value);
    }
}
