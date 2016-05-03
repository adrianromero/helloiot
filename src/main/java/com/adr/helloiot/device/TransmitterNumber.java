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

package com.adr.helloiot.device;

import com.adr.helloiot.device.format.StringFormat;
import com.adr.helloiot.device.format.StringFormatDecimal;

/**
 *
 * @author adrian
 */
public class TransmitterNumber extends TransmitterSimple {

    public static final StringFormat NUMBERFORMAT = new StringFormatDecimal("0.000");    
    
    private double levelmax = 100.0;
    private double levelmin = 0.0;
    
    public TransmitterNumber() {
        setFormat(new StringFormatDecimal("0.000"));
    }

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.transmitternumber");
    }

    public String getUnit() {
        return "";
    }

    public final void setLevelMax(double levelmax) {
        this.levelmax = levelmax;
    }   
    
    public final double getLevelMax() {
        return levelmax;
    }
    
    public final void setLevelMin(double levelmin) {
        this.levelmin = levelmin;
    }
    
    public final double getLevelMin() {
        return levelmin;
    }    
}
