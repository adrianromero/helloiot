//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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
package com.adr.helloiotext.home;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.ApplicationDevicesUnits;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiotlib.device.Device;
import com.adr.helloiot.device.DeviceSubscribe;
import com.adr.helloiot.device.MessageStatus;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.TreePublish;
import com.adr.helloiot.mqtt.MQTTProperty;
import com.adr.helloiot.unit.ButtonBase;
import com.adr.helloiot.unit.LabelSection;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiotlib.unit.Unit;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author adrian
 */
public class HomeConfig implements ApplicationDevicesUnits {
    
    private final List<Device> devices;
    private final List<Unit> units;
    
    public HomeConfig() {
        
        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiotext/fxml/home");
       
        TreePublish lightactions = new TreePublish();
        lightactions.setId("lights");
        lightactions.setTopic("lightevents/devicesmanager");
        MQTTProperty.setRetained(lightactions, false);
        
        DeviceSubscribe lightevents = new MessageStatus();
        lightevents.setId("lightevents");
        lightevents.setTopic("lightevents/devicesmanager/#");
        MQTTProperty.setRetained(lightevents, false);
        
        DeviceSimple secstatus = new DeviceSimple();
        secstatus.setTopic("securityevents/security/status");
        MQTTProperty.setRetained(secstatus, true);
        
        DeviceSimple secactions = new DeviceSimple();
        secactions.setId("security");
        secactions.setTopic("securityevents/security/action");
        MQTTProperty.setRetained(secactions, false);
      
        TransmitterSimple secalarm = new TransmitterSimple();
        secalarm.setTopic("home/master/alarm");
        
        // Managers
        DevicesManager devsmng = new DevicesManager(secactions, secstatus);
        devsmng.setDevice(lightevents);   
        
        SecurityManager secmng = new SecurityManager(secactions, secstatus, secalarm);
        secmng.setDevice(secactions);        

        // Arming security
        LabelSection arminglabel = new LabelSection();
        UnitPage.setPage(arminglabel, "security_arming");
        UnitPage.setLayout(arminglabel, "StartFull"); 
        arminglabel.setText(resources.getString("label.securityarming"));
   
        ButtonBase cancelbutton = new ButtonBase() {
            @Override protected void doRun(ActionEvent event) {
                secactions.sendStatus("DISARMED");
            }
        };  
        UnitPage.setPage(cancelbutton, "security_arming");
        UnitPage.setLayout(cancelbutton, "StartFull"); 
        cancelbutton.setLabel("");
        cancelbutton.setText(resources.getString("button.cancelarming"));
        cancelbutton.setStyle("-fx-background-color: transparent;");        
        cancelbutton.setGraphic(IconBuilder.create(FontAwesome.FA_TIMES, 48.0).color(Color.RED).shine(Color.WHITE).build());
        
        // Armed security
        LabelSection armedlabel = new LabelSection();
        UnitPage.setPage(armedlabel, "security");
        UnitPage.setLayout(armedlabel, "StartFull"); 
        armedlabel.setText(resources.getString("label.securityarmed"));
        
        ButtonBase disarmbutton = new ButtonBase() {
            @Override protected void doRun(ActionEvent event) {
                secactions.sendStatus("DISARMED");
            }
        };
        UnitPage.setPage(disarmbutton, "security");
        UnitPage.setLayout(disarmbutton, "StartFull");  
        disarmbutton.setLabel("");
        disarmbutton.setText(resources.getString("button.disarm"));
        disarmbutton.setStyle("-fx-background-color: transparent;");
        disarmbutton.setSecurityKey("mainsecuritykey");
        disarmbutton.setGraphic(IconBuilder.create(FontAwesome.FA_KEY, 48.0).color(Color.GREEN).shine(Color.WHITE).build());
        
        devices = Arrays.asList(lightactions, lightevents, secactions, secstatus, secalarm);
        units = Arrays.asList(devsmng, secmng,
                arminglabel, cancelbutton,
                armedlabel, disarmbutton);
    }

    @Override
    public List<Device> getDevices() {
        return devices;
    }

    @Override
    public List<Unit> getUnits() {
        return units;
    }
}
