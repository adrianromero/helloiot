//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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

import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatSwitch;
import com.adr.helloiot.media.Clip;
import com.adr.helloiot.media.ClipFactory;
import com.adr.helloiotlib.unit.Units;
import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.media.AudioClip;

/**
 *
 * @author adrian
 */
public class Beeper {

    private final Clip beep;
    private final Node alert;
    private final Animation alertanimation;
    
    private final StringFormat format = new StringFormatSwitch();
    
    private final Object messageHandler = Units.messageHandler(this::updateStatus);  

    public Beeper(ClipFactory factory, Node alert, Animation alertanimation) {

        this.alert = alert;
        this.alertanimation = alertanimation;
        // http://www.soundjay.com/tos.html
        beep = factory.createClip(getClass().getResource("/com/adr/helloiot/sounds/beep-01a.wav").toExternalForm(), AudioClip.INDEFINITE);
    }
    
    public Object getMessageHandler() {
        return messageHandler;
    }

    private void updateStatus(byte[] status) {
        boolean isVisible = format.value(status).asBoolean();
        
        if (isVisible && !alert.isVisible()) {
            alertanimation.playFromStart();
            alert.setVisible(true);            
            beep.play();            
        } else if (!isVisible && alert.isVisible()) {
            alert.setVisible(false);
            alertanimation.stop();
            beep.stop();            
        }
    }
}
