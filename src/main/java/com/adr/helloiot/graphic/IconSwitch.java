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

import com.adr.helloiot.device.StatusSwitch;
import java.util.Arrays;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public interface IconSwitch extends IconStatus {

    @Override
    public default Node buildIcon(byte[] status) {
        if (Arrays.equals(StatusSwitch.ON, status)) {
            return buildIconOn();
        } else {
            return buildIconOff();
        }
    }
    
    public Node buildIconOn(); 
    public Node buildIconOff();    
}
