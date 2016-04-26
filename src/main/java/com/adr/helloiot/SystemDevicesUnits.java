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

import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.device.TransmitterSimple;
import com.adr.helloiot.device.TreeEvent;
import com.adr.helloiot.device.TreeStatus;
import java.util.Properties;

/**
 *
 * @author adrian
 */
public class SystemDevicesUnits extends AbstractApplicationDevicesUnits {
    
    public final static String SYS_HELLOIOT = "_sys_helloIoT/";
    
    public final static String SYS_VALUE_TOPIC = SYS_HELLOIOT + "sysvalue" ;
    public final static String SYS_VALUE_ID = "SYSVALUESID" ;   
    public final static String SYS_EVENT_TOPIC = SYS_HELLOIOT + "sysevent" ;   
    public final static String SYS_EVENT_ID = "SYSEVENTSID" ;  
      
    public final static String SYS_UNITPAGE_ID = "SYSUNITPAGEID";
    public final static String SYS_BEEPER_ID = "SYSBEEPERID";
    public final static String SYS_BUZZER_ID = "SYSBUZZERID";
    
    @Override
    public void init(Properties config) {
        
        TreeEvent sysevents = new TreeEvent();
        sysevents.setTopic(SYS_EVENT_TOPIC);
        sysevents.setId(SYS_EVENT_ID);
        
        TreeStatus sysstatus = new TreeStatus();
        sysstatus.setTopic(SYS_VALUE_TOPIC);
        sysstatus.setId(SYS_VALUE_ID);
       
        String topicapp = config.getProperty("mqtt.topicapp");
     
        DeviceSimple unitpage = new DeviceSimple();
        unitpage.setTopic(topicapp + "/unitpage");
        unitpage.setId(SYS_UNITPAGE_ID);
         
        DeviceSwitch beeper = new DeviceSwitch();
        beeper.setTopic(topicapp + "/beeper");
        beeper.setId(SYS_BEEPER_ID);
        
        TransmitterSimple buzzer = new TransmitterSimple();
        buzzer.setTopic(topicapp + "/buzzer");
        buzzer.setId(SYS_BUZZER_ID);
    
        createDevices(sysevents, sysstatus, unitpage, beeper, buzzer);
    }
}