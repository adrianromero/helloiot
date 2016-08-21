/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.helloiot.client;

import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatIdentity;
import com.adr.helloiot.unit.EditStatus;
import com.adr.helloiot.unit.StartLine;
import com.adr.helloiot.unit.Unit;
import java.util.Arrays;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author adrian
 */
public class TopicStatus {
    
    private List<Device> devices;
    private List<Unit> units;
    
    public static TopicStatus buildTopicStatus(String topic) {
        return buildTopicStatus(topic, StringFormatIdentity.INSTANCE);
    }
    
    public static TopicStatus buildTopicStatus(String topic, String format) {
        return buildTopicStatus(topic, StringFormat.valueOf(format));
    }
    
    public static TopicStatus buildTopicStatus(String topic, StringFormat format) {
//        <DeviceSimple fx:id="sampleformat2" topic="home/livingroom/sampleformat" format ="HEXADECIMAL" />
        DeviceSimple d = new DeviceSimple();
        d.setTopic(topic);
        d.setFormat(format);
        
        
        
//        <EditAreaStatus UnitPage.page="Status" GridPane.columnSpan="2" label="Sample topic" device="$sampleformat" style="-fx-background-color: cyan;"/>
        EditStatus u = new EditStatus(); // EditAreaStatus
        u.setLabel(topic);
        u.setDevice(d);
        u.setStyle("-fx-background-color: white;");
        HBox.setHgrow(u, Priority.SOMETIMES);
        
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
