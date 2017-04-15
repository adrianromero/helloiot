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
import com.adr.helloiot.util.ExternalFonts;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
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
    private HBox headerbox;
    @FXML
    private Label headertitle;
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

    @FXML
    Button adddeviceunit;
    @FXML
    Button removedeviceunit;
    @FXML
    Button updeviceunit;
    @FXML
    Button downdeviceunit;
    @FXML
    ListView<TopicInfo> devicesunitslist;
    @FXML
    ScrollPane deviceunitform;

    @FXML
    TextField edittopic;
    @FXML
    TextField edittopicpub;
    @FXML
    ChoiceBox<String> edittype;
    @FXML
    ChoiceBox<String> editformat;
    @FXML
    ColorPicker editcolor;
    @FXML
    Button clearcolor;
    @FXML
    ColorPicker editbackground;
    @FXML
    Button clearbackground;
    @FXML
    ChoiceBox<Integer> editqos;
    @FXML
    ChoiceBox<Integer> editretained;
    @FXML
    TextField editjsonpath;
    @FXML
    CheckBox editmultiline;

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

        edittopicpub.promptTextProperty().bind(edittopic.textProperty());
        edittopic.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });
        edittopicpub.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });

        editformat.setItems(FXCollections.observableArrayList(
                "STRING",
                "INT",
                "DOUBLE",
                "DECIMAL",
                "DEGREES",
                "BASE64",
                "HEX"));
        editformat.getSelectionModel().clearSelection();
        editformat.valueProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });

        editjsonpath.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });

        edittype.setItems(FXCollections.observableArrayList("Subscription", "Publication", "Publication/Subscription"));
        edittype.getSelectionModel().clearSelection();
        edittype.valueProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateCurrentTopic();
        });

        editmultiline.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            updateCurrentTopic();
        });

        clearcolor.setGraphic(IconBuilder.create(FontAwesome.FA_TRASH, 14.0).build());
        editcolor.setValue(null);
        editcolor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            updateCurrentTopic();
        });

        clearbackground.setGraphic(IconBuilder.create(FontAwesome.FA_TRASH, 14.0).build());
        editbackground.setValue(null);
        editbackground.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
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
        editqos.getSelectionModel().clearSelection();
        editqos.valueProperty().addListener((ObservableValue<? extends Integer> ov, Integer old_val, Integer new_val) -> {
            updateCurrentTopic();
        });

        editretained.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object < 0) {
                    return resources.getString("label.default");
                } else if (object == 0) {
                    return resources.getString("label.no");
                } else {
                    return resources.getString("label.yes");
                }
            }

            @Override
            public Integer fromString(String value) {
                return Integer.getInteger(value);
            }
        });
        editretained.setItems(FXCollections.observableArrayList(-1, 0, 1));
        editretained.getSelectionModel().clearSelection();
        editretained.valueProperty().addListener((ObservableValue<? extends Integer> ov, Integer old_val, Integer new_val) -> {
            updateCurrentTopic();
        });

        devicesunitslist.setCellFactory((ListView<TopicInfo> list) -> new ListCell<TopicInfo>() {
            @Override
            public void updateItem(TopicInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Text t = new Text();
                    t.setFill(Color.WHITE);
                    t.setFont(Font.font(ExternalFonts.ROBOTOBOLD, FontWeight.BOLD, 10.0));
                    TextFlow tf = new TextFlow(t);
                    tf.setPrefWidth(55);
                    tf.setTextAlignment(TextAlignment.CENTER);
                    tf.setPadding(new Insets(2, 5, 2, 5));

                    if ("Subscription".equals(item.getType())) {
                        t.setText("SUB");
                        tf.setStyle("-fx-background-color: #001A80; -fx-background-radius: 12px;");
                    } else if ("Publication".equals(item.getType())) {
                        t.setText("PUB");
                        tf.setStyle("-fx-background-color: #4D001A; -fx-background-radius: 12px;");
                    } else { // "Publication/Subscription"
                        t.setText("P/SUB");
                        tf.setStyle("-fx-background-color: #003300; -fx-background-radius: 12px;");
                    }

                    setGraphic(tf);
                    String label = item.getLabel();
                    setText(label == null || label.isEmpty() ? resources.getString("label.empty") : label);
                }
            }
        });
        devicesunitslist.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TopicInfo> ov, TopicInfo old_val, TopicInfo new_val) -> {
            updateDevicesUnitsList();
        });
        updateDevicesUnitsList();

        Platform.runLater(host::requestFocus);
    }

    public Node getNode() {
        return rootpane;
    }

    private void updateCurrentTopic() {
        if (!updating) {
            int index = devicesunitslist.getSelectionModel().getSelectedIndex();
            TopicInfo topic = devicesunitslist.getSelectionModel().getSelectedItem();

            topic.setTopic(edittopic.getText());
            topic.setType(edittype.getValue());
            if ("Subscription".equals(edittype.getValue())) {
                topic.setTopicpub(null);
                edittopicpub.setDisable(true);
            } else {
                edittopicpub.setDisable(false);
                topic.setTopicpub(edittopicpub.getText() == null || edittopicpub.getText().isEmpty() ? null : edittopicpub.getText());
            }
            topic.setFormat(editformat.getValue());
            if ("BASE64".equals(editformat.getValue()) || "HEX".equals(editformat.getValue()) || "SWITCH".equals(editformat.getValue())) {
                topic.setJsonpath(null);
                editjsonpath.setDisable(true);
            } else {
                topic.setJsonpath(editjsonpath.getText());
                editjsonpath.setDisable(false);
            }
            topic.setMultiline(editmultiline.isSelected());
            topic.setColor(editcolor.getValue());
            topic.setBackground(editbackground.getValue());
            topic.setQos(editqos.getValue());
            topic.setRetained(editretained.getValue());

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
            editformat.getSelectionModel().clearSelection();
            editjsonpath.setText(null);
            editmultiline.setSelected(false);
            editcolor.setValue(null);
            editbackground.setValue(null);
            editcolor.setValue(null);
            editbackground.setValue(null);
            editqos.getSelectionModel().clearSelection();
            editretained.getSelectionModel().clearSelection();
            updating = false;
        } else {
            removedeviceunit.setDisable(false);
            deviceunitform.setDisable(false);
            updeviceunit.setDisable(index <= 0);
            downdeviceunit.setDisable(index >= devicesunitslist.getItems().size() - 1);

            updating = true;
            edittopic.setText(topic.getTopic());
            edittopicpub.setText(topic.getTopicpub());
            edittopicpub.setDisable("Subscription".equals(topic.getType()));
            edittype.getSelectionModel().select(topic.getType());
            editformat.getSelectionModel().select(topic.getFormat());
            editjsonpath.setText(topic.getJsonpath());
            editjsonpath.setDisable("BASE64".equals(editformat.getValue()) || "HEX".equals(editformat.getValue()) || "SWITCH".equals(editformat.getValue()));
            editmultiline.setSelected(topic.isMultiline());
            editcolor.setValue(topic.getColor());
            editbackground.setValue(topic.getBackground());
            editqos.setValue(topic.getQos());
            editretained.setValue(topic.getRetained());
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

    @FXML
    void onClearColor(ActionEvent event) {
        editcolor.setValue(null);
    }

    @FXML
    void onClearBackground(ActionEvent event) {
        editbackground.setValue(null);
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

    public boolean isMainPage() {
        return mainpage.isSelected();
    }

    public void setMainPage(boolean value) {
        mainpage.setSelected(value);
    }
}
