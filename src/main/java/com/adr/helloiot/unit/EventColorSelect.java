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

package com.adr.helloiot.unit;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author adrian
 */
public class EventColorSelect extends Event {
    
    private final double hue;
    private final double saturation;
    private final double lightness;    

    public EventColorSelect(Object source, EventTarget target, EventType<? extends Event> eventType, double hue, double saturation, double lightness) {
        super(source, target, eventType);
        this.hue = hue;
        this.saturation = saturation;
        this.lightness = lightness;        
    }

    public Double getLightness() {
        return lightness;
    }

    public Double getHue() {
        return hue;
    }

    public Double getSaturation() {
        return saturation;
    }
}
