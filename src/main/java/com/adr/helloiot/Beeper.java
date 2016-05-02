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

import com.adr.helloiot.device.StatusSwitch;
import com.adr.helloiot.media.Clip;
import com.adr.helloiot.media.ClipFactory;
import com.google.common.eventbus.Subscribe;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.media.AudioClip;

/**
 *
 * @author adrian
 */
public class Beeper {

    private final Clip beep;
    private final Label alert;
    
    public Beeper(ClipFactory factory, Label alert) {
        
        this.alert = alert;
        // http://www.soundjay.com/tos.html
        beep = factory.createClip(getClass().getResource("/com/adr/helloiot/sounds/beep-01a.wav").toExternalForm(), AudioClip.INDEFINITE);
    }
    
    @Subscribe
    public void selectUnitPage(EventMessage message) {
        Platform.runLater(() -> updateStatus(message.getMessage()));               
    }
    
    private void updateStatus(byte[] status) {
        alert.setVisible(false);
        beep.stop();           

        if (Arrays.equals(StatusSwitch.ON, status)){
            alert.setVisible(true);
            beep.play();
        }          
    }      
}
