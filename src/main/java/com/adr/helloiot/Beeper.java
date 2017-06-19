//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatSwitch;
import com.adr.helloiot.media.Clip;
import com.adr.helloiot.media.ClipFactory;
import com.adr.helloiot.unit.Units;
import javafx.scene.control.Label;
import javafx.scene.media.AudioClip;

/**
 *
 * @author adrian
 */
public class Beeper {

    private final Clip beep;
    private final Label alert;
    
    private final StringFormat format = new StringFormatSwitch();
    
    private final Object messageHandler = Units.messageHandler(this::updateStatus);  

    public Beeper(ClipFactory factory, Label alert) {

        this.alert = alert;
        // http://www.soundjay.com/tos.html
        beep = factory.createClip(getClass().getResource("/com/adr/helloiot/sounds/beep-01a.wav").toExternalForm(), AudioClip.INDEFINITE);
    }
    
    public Object getMessageHandler() {
        return messageHandler;
    }

    private void updateStatus(byte[] status) {
        alert.setVisible(false);
        beep.stop();

        if (format.value(status).asBoolean()) {
            alert.setVisible(true);
            beep.play();
        }
    }
}
