//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018-2019 Adrián Romero Corchado.
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
package com.adr.helloiot.topicinfo;

import com.adr.helloiot.DevicesUnits;
import com.adr.helloiot.HelloIoTException;
import com.adr.helloiot.SubProperties;
import com.adr.helloiot.device.MessageStatus;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiot.unit.MessagesPage;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.unit.Unit;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;

public class TopicInfoMessagesSubscribe implements TopicInfo {

    private final SimpleStringProperty topic = new SimpleStringProperty();

    private final TopicInfoFactory factory;
    private final TopicInfoMessagesSubscribeNode node;

    public TopicInfoMessagesSubscribe(TopicInfoFactory factory, TopicInfoMessagesSubscribeNode node) {
        this.factory = factory;
        this.node = node;
    }

    @Override
    public TopicInfoFactory getFactory() {
        return factory;
    }

    @Override
    public ReadOnlyProperty<String> getLabel() {
        return topic;
    }

    @Override
    public void load(SubProperties properties) {
        topic.setValue(properties.getProperty(".topic"));
    }

    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".topic", getTopic());
    }

    @Override
    public DevicesUnits getDevicesUnits() throws HelloIoTException {
        
        List<Device> devices = new ArrayList<>();
        List<Unit> units = new ArrayList<>();
        
        MessageStatus messagestatus = new MessageStatus();
        messagestatus.setTopic(getTopic());
        MQTTProperty.setQos(messagestatus, 2);
        devices.add(messagestatus);
        
        MessagesPage messagespage = new MessagesPage();
        messagespage.setDevice(messagestatus);
        messagespage.setLabel(null);
        UnitPage.setPage(messagespage, "FA_SOLID_INBOX//" + getTopic());
        UnitPage.setLayout(messagespage, "StartFull");
        units.add(messagespage);
        
        return new DevicesUnits(devices, units);
    }

    @Override
    public TopicInfoNode getEditNode() {
        return node;
    }

    @Override
    public void writeToEditNode() {
        node.topic.setText(getTopic());
    }

    @Override
    public void readFromEditNode() {
        topic.setValue(node.topic.getText());
    }

    public String getTopic() {
        return topic.getValue();
    }
}
