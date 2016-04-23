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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class Clock {
    
    private final Label l;
    private final DateTimeFormatter formatter; 
    
    private LocalDateTime datetime = null;
    private Timeline timeline = null;
    
    
    public Clock(Label l, String pattern) {
        this.l = l;
        formatter = DateTimeFormatter.ofPattern(pattern);         
    }
    
    public void play() {
        if (timeline != null) {
            stop();
        }
        
        datetime = LocalDateTime.now();
        int second = datetime.getSecond();
        datetime.minusSeconds(second);
        l.setText(datetime.format(formatter));

        timeline = new Timeline(new KeyFrame(Duration.minutes(1.0), ae -> {
            datetime = datetime.plusMinutes(1);
            l.setText(datetime.format(formatter));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFrom(Duration.seconds(second));        
    }
    
    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
            datetime = null;
        }        
    }
}
