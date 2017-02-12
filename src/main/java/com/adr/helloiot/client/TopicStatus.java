/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.helloiot.client;

import com.adr.helloiot.TopicInfo;
import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceBasic;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatBase64;
import com.adr.helloiot.device.format.StringFormatDecimal;
import com.adr.helloiot.device.format.StringFormatHex;
import com.adr.helloiot.device.format.StringFormatIdentity;
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
    
    private final static String STYLEFORMAT = "{} {-fx-background-color: gray; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 70px; -fx-text-alignment: center;}";
    private final static String STYLEFORMATSPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 70px;}";
    private final static String STYLEQOS = "{} {-fx-background-color: darkblue; -fx-background-radius: 10px; -fx-fill:white; -fx-padding: 0 5 0 5; -fx-pref-width: 30px; -fx-text-alignment: center;}";
    private final static String STYLEQOSSPACE = "{} {-fx-padding: 0 5 0 5; -fx-pref-width: 30px;}";
    
    private List<Device> devices;
    private List<Unit> units;

    private static String getFormatBadge(StringFormat f) {
        if (f instanceof StringFormatIdentity) {
            return STYLEFORMATSPACE;
        } else {
            return STYLEFORMAT + f.toString();
        }
    }
    private static String getQOSBadge(int i) {
        if (i < 0) {
            return STYLEQOSSPACE;
        } else {
            return STYLEQOS + Integer.toString(i);
        }
    }
    
    private static StringFormat createFormat(TopicInfo topicinfo) {
        if ("STRING".equals(topicinfo.getFormat())) {
            return new StringFormatIdentity(topicinfo.getJsonpath() == null || topicinfo.getJsonpath().isEmpty() ? null: topicinfo.getJsonpath());
        } else if ("INT".equals(topicinfo.getFormat())) {
            return new StringFormatDecimal(topicinfo.getJsonpath() == null || topicinfo.getJsonpath().isEmpty() ? null: topicinfo.getJsonpath(), "0");
        } else if ("BASE64".equals(topicinfo.getFormat())) {
            return new StringFormatBase64();
        } else if ("HEX".equals(topicinfo.getFormat())) {
            return new StringFormatHex();      
        } else if ("DOUBLE".equals(topicinfo.getFormat())) {
            return new StringFormatDecimal(topicinfo.getJsonpath() == null || topicinfo.getJsonpath().isEmpty() ? null: topicinfo.getJsonpath(), "0.00");
        } else if ("DECIMAL".equals(topicinfo.getFormat())) {
            return new StringFormatDecimal(topicinfo.getJsonpath() == null || topicinfo.getJsonpath().isEmpty() ? null: topicinfo.getJsonpath(), "0.000");
        } else if ("DEGREES".equals(topicinfo.getFormat())) {
            return new StringFormatDecimal(topicinfo.getJsonpath() == null || topicinfo.getJsonpath().isEmpty() ? null: topicinfo.getJsonpath(), "0.0°");
        } else {
            return StringFormatIdentity.INSTANCE;
        }        
    }
    
    public static TopicStatus buildTopicPublish(TopicInfo topicinfo) {

        TransmitterSimple d = new TransmitterSimple();
        d.setTopic(topicinfo.getTopic());
        d.setTopicPublish(topicinfo.getTopicpub());
        d.setQos(topicinfo.getQos());
        if (topicinfo.getRetained() >= 0) {
            d.setRetained(topicinfo.getRetained() != 0);
        }
        d.setFormat(createFormat(topicinfo));

        EditEvent u = topicinfo.isMultiline() ? new EditAreaEvent() : new EditEvent();
        u.setPrefWidth(320.0);
        u.setLabel(topicinfo.getLabel());
        u.setFooter(topicinfo.getTopic() + getQOSBadge(topicinfo.getQos()) + getFormatBadge(d.getFormat()));
        u.setDevice(d);
        
        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(u);
        return ts;                       
    }    
    
    public static TopicStatus buildTopicPublishSubscription(TopicInfo topicinfo) {

        DeviceSimple d = new DeviceSimple();
        d.setTopic(topicinfo.getTopic());
        d.setTopicPublish(topicinfo.getTopicpub());
        d.setQos(topicinfo.getQos());
        if (topicinfo.getRetained() >= 0) {
            d.setRetained(topicinfo.getRetained() != 0);
        }
        d.setFormat(createFormat(topicinfo));

        EditStatus u = topicinfo.isMultiline() ? new EditAreaStatus() : new EditStatus();
        u.setPrefWidth(320.0);
        u.setLabel(topicinfo.getLabel());
        u.setFooter(topicinfo.getTopic() + getQOSBadge(topicinfo.getQos()) + getFormatBadge(d.getFormat()));
        u.setDevice(d);        

        TopicStatus ts = new TopicStatus();
        ts.devices = Arrays.asList(d);
        ts.units = Arrays.asList(u);
        return ts;                       
    }
    
    public static TopicStatus buildTopicSubscription(TopicInfo topicinfo) {

        DeviceBasic d = new DeviceBasic();
        d.setTopic(topicinfo.getTopic());
        d.setTopicPublish(topicinfo.getTopicpub());
        d.setQos(topicinfo.getQos());
        if (topicinfo.getRetained() >= 0) {
            d.setRetained(topicinfo.getRetained() != 0);
        }
        d.setFormat(createFormat(topicinfo));

        EditView u = topicinfo.isMultiline() ? new EditAreaView() : new EditView();
        u.setPrefWidth(320.0);
        u.setLabel(topicinfo.getLabel());
        u.setFooter(topicinfo.getTopic() + getQOSBadge(topicinfo.getQos()) + getFormatBadge(d.getFormat()));
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
