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

import javafx.scene.paint.Color;

/**
 *
 * @author adrian
 */
public class TopicInfo {
    
    private String topic = null;
    private String topicpub = null;
    private String type = "Publication/Subscription";
    private String format = "STRING";
    private String jsonpath = null;
    private boolean multiline = false;
    private Color color = null;
    private Color background = null;
    private int qos = -1;
    private int retained = -1;
    
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicpub() {
        return topicpub;
    }

    public void setTopicpub(String topicpub) {
        this.topicpub = topicpub;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getJsonpath() {
        return jsonpath;
    }

    public void setJsonpath(String jsonpath) {
        this.jsonpath = jsonpath;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public int getRetained() {
        return retained;
    }

    public void setRetained(int retained) {
        this.retained = retained;
    }
    
    @Override
    public String toString() {
        return topic == null || topic.isEmpty() ? "-" : topic;
    }
    
    public String getLabel() {
        return (topic == null) ? null : capitalize(leaf(topic));
    }
    
    private static String capitalize(String s) {
        final char[] buffer = s.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (ch == '_' || ch == ' ') {
                buffer[i] = ' ';
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }
    
    private static String leaf(String s) {
        int i = s.lastIndexOf('/');
        if (i < 0) {
            return s;
        } else if (i == s.length() - 1) {
            return leaf(s.substring(0, s.length()- 1));
        } else {
            return s.substring(i + 1);
        }
    }
        
}
