//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
import com.adr.helloiot.device.TreePublish;
import com.adr.helloiot.unit.PublicationsPage;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiotlib.device.Device;
import com.adr.helloiotlib.unit.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;

public class TopicInfoMessagesPublish implements TopicInfo {

    private final SimpleStringProperty topic = new SimpleStringProperty();
    private final SimpleStringProperty label = new SimpleStringProperty();
    
    private final TopicInfoFactory factory;
    private final TopicInfoMessagesPublishNode node;
    private final ResourceBundle resources;

    public TopicInfoMessagesPublish(TopicInfoFactory factory, TopicInfoMessagesPublishNode node) {
        this.factory = factory;
        this.node = node;
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/topicinfomessagespublishnode"); 
        label.setValue(resources.getString("label.sendmessages"));
    }

    @Override
    public TopicInfoFactory getFactory() {
        return factory;
    }

    @Override
    public ReadOnlyProperty<String> getLabel() {
        return label;
    }

    @Override
    public void load(SubProperties properties) {
        String proptopic = properties.getProperty(".topic");
        topic.setValue(proptopic);
        label.setValue(proptopic == null || proptopic.isEmpty() ? resources.getString("label.sendmessages") : proptopic);
    }

    @Override
    public void store(SubProperties properties) {
        properties.setProperty(".topic", getTopic());
    }

    @Override
    public DevicesUnits getDevicesUnits() throws HelloIoTException {
        
        List<Device> devices = new ArrayList<>();
        List<Unit> units = new ArrayList<>();
        
        TreePublish messagespublish = new TreePublish();
        messagespublish.setTopic(getTopic());
        devices.add(messagespublish);
        
        PublicationsPage publicationspage = new PublicationsPage();
        publicationspage.setDevice(messagespublish);
        publicationspage.setLabel(null);
        UnitPage.setPage(publicationspage, "FA_SOLID_ENVELOPE//" + label.getValue());
        UnitPage.setLayout(publicationspage, "StartFull");
        units.add(publicationspage);
        
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
        String proptopic = node.topic.getText();
        topic.setValue(proptopic);
        label.setValue(proptopic == null || proptopic.isEmpty() ? resources.getString("label.sendmessages") : proptopic);
    }

    public String getTopic() {
        return topic.getValue();
    }
}
