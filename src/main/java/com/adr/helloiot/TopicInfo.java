/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.helloiot;

/**
 *
 * @author adrian
 */
public class TopicInfo {
    
    private String topic = null;
    private String topicpub = null;
    private String type = "Publication/Subscription";

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
    
    
    public String toString() {
        return topic == null || topic.isEmpty() ? "-" : topic;
    }
    
}
