//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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
package com.adr.helloiot.device;

import com.adr.fonticon.IconFontGlyph;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.graphic.Power;

/**
 *
 * @author adrian
 */
public class Plug extends DeviceSwitch {

    public static final IconStatus ICONPLUG = new Power(IconFontGlyph.FA_SOLID_PLUG);

    @Override
    public String getDeviceName() {
        return resources.getString("devicename.plug");
    }

    @Override
    public IconStatus getIconStatus() {
        return ICONPLUG;
    }
}
