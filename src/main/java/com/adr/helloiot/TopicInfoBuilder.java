//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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

/**
 *
 * @author adrian
 */
public class TopicInfoBuilder {
    
    TopicInfoEditNode editnode = null;
    TopicInfoCodeNode codenode = null;
    TopicInfoSwitchNode switchnode = null;
    
    public TopicInfo fromProperties(SubProperties subproperties) {
        
        TopicInfo topicinfo = create(subproperties.getProperty(".type", "Publication/Subscription"));
        // load subproperties
        topicinfo.load(subproperties);
        return topicinfo; 
    }
    
    public TopicInfo create() {
        // Default new 
        return create("Publication/Subscription");
    }
    
    public TopicInfo create(String type) {
        TopicInfo topicinfo;
        if ("Code".equals(type)) {
            topicinfo = new TopicInfoCode(codenode == null ? (codenode = new TopicInfoCodeNode()) : codenode);
        } else if ("Switch".equals(type)) {
            topicinfo = new TopicInfoSwitch(switchnode == null ? (switchnode = new TopicInfoSwitchNode()) : switchnode);
        } else {
            topicinfo = new TopicInfoEdit(type, editnode == null ? (editnode = new TopicInfoEditNode()) : editnode);
        }
        return topicinfo;
    }
}
