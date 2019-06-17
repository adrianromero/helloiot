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
package com.adr.helloiot;

import com.adr.helloiot.topicinfo.TopicInfo;
import com.adr.helloiot.topicinfo.TopicInfoFactory;
import com.adr.helloiot.topicinfo.TopicInfoFactoryCode;
import com.adr.helloiot.topicinfo.TopicInfoFactoryEdit;
import com.adr.helloiot.topicinfo.TopicInfoFactoryMessagesPublish;
import com.adr.helloiot.topicinfo.TopicInfoFactoryMessagesSubscribe;
import com.adr.helloiot.topicinfo.TopicInfoFactorySend;
import com.adr.helloiot.topicinfo.TopicInfoFactorySwitch;
import com.adr.helloiot.topicinfo.TopicInfoFactoryView;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TopicInfoBuilder {

    private Map<String, TopicInfoFactory> factories = new LinkedHashMap<>();
    
    public TopicInfoBuilder() { 
        put(new TopicInfoFactoryView());
        put(new TopicInfoFactoryEdit());
        put(new TopicInfoFactorySend());
        put(new TopicInfoFactorySwitch());
        put(new TopicInfoFactoryMessagesPublish());
        put(new TopicInfoFactoryMessagesSubscribe());
        put(new TopicInfoFactoryCode());
    }
    
    private void put(TopicInfoFactory factory) {
        factories.put(factory.getType(), factory);
    }
    
    public Collection<TopicInfoFactory> getTopicInfoFactories() {
        return factories.values();
    }
    
    public TopicInfo create(String type) {
        return factories.get(type).create();
    }
    
    public TopicInfo fromProperties(SubProperties subproperties) {
        
        TopicInfo topicinfo = factories.get(subproperties.getProperty(".type", "Edit")).create();
        // load subproperties
        topicinfo.load(subproperties);
        return topicinfo; 
    }
}
