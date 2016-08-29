/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.helloiot.client;

import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceBasic;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.unit.EditAreaEvent;
import com.adr.helloiot.unit.EditAreaStatus;
import com.adr.helloiot.unit.EditAreaView;
import com.adr.helloiot.unit.EditEvent;
import com.adr.helloiot.unit.EditStatus;
import com.adr.helloiot.unit.EditView;
import com.adr.helloiot.unit.StartLine;
import com.adr.helloiot.unit.Unit;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author adrian
 */
public class TopicStatus {
    
    private List<Device> devices;
    private List<Unit> units;
    
    public static TopicStatus buildTopicPublish(String topic, int qos, StringFormat format,  boolean multiline) {

        TransmitterSimple d = new TransmitterSimple();
        d.setTopic(topic);
        d.setQos(qos);
        d.setFormat(format);

        EditEvent u = multiline ? new EditAreaEvent() : new EditEvent();
        u.setLabel(topic);
        u.setDevice(d);
        u.setStyle("-fx-background-color: white;");
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(new StartLine(), u);
        return ts;                       
    }    
    
    public static TopicStatus buildTopicPublishRetained(String topic, int qos, StringFormat format,  boolean multiline) {

        DeviceSimple d = new DeviceSimple();
        d.setTopic(topic);
        d.setQos(qos);
        d.setFormat(format);

        EditStatus u = multiline ? new EditAreaStatus() : new EditStatus();
        u.setLabel(topic);
        u.setDevice(d);
        u.setStyle("-fx-background-color: white;");
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(new StartLine(), u);
        return ts;                       
    }
    
    public static TopicStatus buildTopicSubscription(String topic, int qos, StringFormat format,  boolean multiline) {

        DeviceBasic d = new DeviceBasic();
        d.setTopic(topic);
        d.setQos(qos);
        d.setFormat(format);

        EditView u = multiline ? new EditAreaView() : new EditView();
        u.setLabel(topic);
        u.setDevice(d);
        u.setStyle("-fx-background-color: white;");
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(new StartLine(), u);
        return ts;          
    }
    
    private TopicStatus() {}
    
    public List<Device> getDevices() {
        return devices;
    }
    
    public List<Unit> getUnits() {
        return units;
    }
}
