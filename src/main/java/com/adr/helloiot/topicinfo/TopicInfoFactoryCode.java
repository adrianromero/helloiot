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

import com.adr.fonticon.IconFontGlyph;
import java.util.ResourceBundle;

public class TopicInfoFactoryCode implements TopicInfoFactory {
    
    private TopicInfoCodeNode codenode = null;
    private final String name;
    
    public TopicInfoFactoryCode() {
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/clientlogin");
        name = resources.getString("label.topicinfo.Code");
    }
    
    @Override
    public String getType() {
        return "Code";
    }

    @Override
    public String getTypeName() {
        return name;
    }
    
    @Override
    public TopicInfoNode getTopicInfoNode() {
        return codenode;
    }
    
    @Override
    public TopicInfo create() {
        if (codenode == null) {
            codenode = new TopicInfoCodeNode();
        }
        return new TopicInfoCode(this, codenode);
    }

    @Override
    public IconFontGlyph getGlyph() {
        return IconFontGlyph.FA_SOLID_CODE;
    }
}
