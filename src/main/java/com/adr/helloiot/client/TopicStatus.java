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
import com.adr.helloiot.unit.Unit;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author adrian
 */
public class TopicStatus {
    
    private final static String STYLEFORMAT = "{} {-fx-background-color: gray; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 60px; -fx-text-alignment: center;}";
    private final static String STYLEQOS = "{} {-fx-background-color: darkblue; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 60px; -fx-text-alignment: center;}";
    private final static String STYLESPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 60px;}";
    
    private List<Device> devices;
    private List<Unit> units;
    
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
    
    private static String getFormatBadge(String s) {
        if (s == null || s.isEmpty()) {
            return STYLESPACE;
        } else {
            return STYLEFORMAT + s;
        }
    }
    private static String getQOSBadge(int i) {
        if (i < 0) {
            return STYLESPACE;
        } else {
            return STYLEQOS + Integer.toString(i);
        }
    }
    
    public static TopicStatus buildTopicPublish(String topic, int qos, StringFormat format,  boolean multiline) {

        TransmitterSimple d = new TransmitterSimple();
        d.setTopic(topic);
        d.setQos(qos);
        d.setFormat(format);

        EditEvent u = multiline ? new EditAreaEvent() : new EditEvent();
        u.setPrefWidth(320.0);
        u.setLabel(capitalize(leaf(topic)));
        u.setFooter(topic + getFormatBadge(format.getName()) + getQOSBadge(qos));
        u.setDevice(d);
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(u);
        return ts;                       
    }    
    
    public static TopicStatus buildTopicPublishRetained(String topic, int qos, StringFormat format,  boolean multiline) {

        DeviceSimple d = new DeviceSimple();
        d.setTopic(topic);
        d.setQos(qos);
        d.setFormat(format);

        EditStatus u = multiline ? new EditAreaStatus() : new EditStatus();
        u.setPrefWidth(320.0);
        u.setLabel(capitalize(leaf(topic)));
        u.setFooter(topic + getFormatBadge(format.getName()) + getQOSBadge(qos));
        u.setDevice(d);
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(u);
        return ts;                       
    }
    
    public static TopicStatus buildTopicSubscription(String topic, int qos, StringFormat format,  boolean multiline) {

        DeviceBasic d = new DeviceBasic();
        d.setTopic(topic);
        d.setQos(qos);
        d.setFormat(format);

        EditView u = multiline ? new EditAreaView() : new EditView();
        u.setPrefWidth(320.0);
        u.setLabel(capitalize(leaf(topic)));
        u.setFooter(topic + getFormatBadge(format.getName()) + getQOSBadge(qos));
        u.setDevice(d);
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(u);
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
