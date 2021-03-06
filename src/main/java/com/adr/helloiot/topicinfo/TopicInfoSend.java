//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiot.unit.EditAreaEvent;
import com.adr.helloiot.unit.EditEvent;
import com.adr.helloiot.unit.UnitPage;
import java.util.Arrays;
import java.util.ResourceBundle;

public class TopicInfoSend extends TopicInfoPublicationSubscription {

    public TopicInfoSend(TopicInfoFactory factory, TopicInfoEditNode editnode) {
        super(factory, editnode);
    }
    
    @Override
    public DevicesUnits getDevicesUnits() throws HelloIoTException {
        
        if (topicpub == null || topicpub.isEmpty()) {
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
            throw new HelloIoTException(resources.getString("exception.topicinfoedit"));
        }
        
        TransmitterSimple d = new TransmitterSimple();
        d.setTopicPublish(topicpub);
        MQTTProperty.setQos(d, qos);
        MQTTProperty.setRetained(d, retained);
        d.setFormat(createFormat());

        EditEvent u = multiline ? new EditAreaEvent() : new EditEvent();
        u.setPrefWidth(320.0);
        u.setLabel(getLabel().getValue());
        u.setFooter(topicpub + getQOSBadge(qos));
        setStyle(u);
        u.setDevice(d);
        UnitPage.setPage(u, page);

        return new DevicesUnits(Arrays.asList(d), Arrays.asList(u));
    }
}
