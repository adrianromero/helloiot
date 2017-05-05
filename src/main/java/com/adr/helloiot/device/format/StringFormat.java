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
package com.adr.helloiot.device.format;

import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public interface StringFormat {

    public String format(byte[] value);
    public MiniVar value(byte[] value);

    public byte[] parse(String formattedvalue);
    public byte[] devalue(MiniVar formattedvalue);

    public Pos alignment();

    public static StringFormat valueOf(String value) {
        if ("STRING".equals(value) || "IDENTITY".equals(value)) {
            return StringFormatIdentity.INSTANCE;
        } else if ("INT".equals(value) || "INTEGER".equals(value)) {
            return StringFormatDecimal.INTEGER;
        } else if ("BASE64".equals(value)) {
            return StringFormatBase64.INSTANCE;
        } else if ("HEX".equals(value)) {
            return StringFormatHex.INSTANCE;
        } else if ("DOUBLE".equals(value)) {
            return StringFormatDecimal.DOUBLE;
        } else if ("DECIMAL".equals(value)) {
            return StringFormatDecimal.DECIMAL;
        } else if ("DEGREES".equals(value)) {
            return StringFormatDecimal.DEGREES;
        } else if (value.startsWith("DECIMAL/")) {
            return new StringFormatDecimal(null, value.substring(8));
        } else {
            throw new IllegalArgumentException("Cannot create StringFormat: " + value);
        }
    }
}
