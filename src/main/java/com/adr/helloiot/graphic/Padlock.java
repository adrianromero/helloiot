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

package com.adr.helloiot.graphic;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.fonticon.IconFont;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author adrian
 */
public class Padlock implements IconSwitch {
    
    private final IconFont iconon;
    private final IconFont iconoff;
    private final Color iconcolor;
    
    public Padlock(IconFont iconon, IconFont iconoff, Color iconcolor) {
        this.iconon = iconon;
        this.iconoff = iconoff;
        this.iconcolor = iconcolor;
    }    
    public Padlock(IconFont iconon, IconFont iconoff) {
        this(iconon, iconoff, Color.DARKSLATEGRAY);
    }    
    public Padlock() {
        this(FontAwesome.FA_LOCK, FontAwesome.FA_UNLOCK, Color.DARKSLATEGRAY);
    }
    
    @Override
    public Node buildIconOn() {
        return IconBuilder.create(iconon, 48.0).color(iconcolor).build();
    }
    
    @Override
    public Node buildIconOff() {
        return IconBuilder.create(iconoff, 48.0).color(iconcolor).build();
    }     
}
