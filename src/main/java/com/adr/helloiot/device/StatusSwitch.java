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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author adrian
 */
public final class StatusSwitch {

    public final static byte[] ON;
    public final static byte[] OFF;

    static {
        ON = "ON".getBytes(StandardCharsets.UTF_8);
        OFF = "OFF".getBytes(StandardCharsets.UTF_8);
    }

    private StatusSwitch() {
    }

    public static boolean getFromBytes(byte[] v) {
        return Arrays.equals(ON, v);
    }

    public static byte[] getFromValue(boolean v) {
        return v ? ON : OFF;
    }
}
