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
package com.adr.helloiot.device;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author adrian
 */
public final class StatusNumber {

    private StatusNumber() {
    }

    public static byte[] getFromValue(double value) {
        return Double.toString(value).getBytes(StandardCharsets.UTF_8);
    }

    public static double getFromBytes(byte[] status) {
        if (status == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(new String(status, StandardCharsets.UTF_8));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
