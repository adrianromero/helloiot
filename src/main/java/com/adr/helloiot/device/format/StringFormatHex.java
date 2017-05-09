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

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatHex extends StringFormat {

    private final static Logger logger = Logger.getLogger(StringFormatHex.class.getName());
    public static final StringFormat INSTANCE = new StringFormatHex();

    @Override
    public String toString() {
        return "HEX";
    }

    @Override
    public String format(byte[] value) {
        MiniVar v = value(value);
        if (v.isEmpty()) {
            return "";
        } else {
            return v.asString();
        }
    }

    @Override
    public MiniVar value(byte[] value) {
        if (value == null) {
            return MiniVarString.NULL;
        } else {
            return new MiniVarString(fixedSplit(formatHexString(value)));
        }
    }
    
    @Override
    public byte[] parse(String formattedvalue) {
        return devalue(new MiniVarString(formattedvalue));
    }
    
    @Override
    public byte[] devalue(MiniVar formattedvalue) {
        return parseHexString(formattedvalue.asString().replaceAll("\\s", ""));
    }
    
    @Override
    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }

    public static byte[] parseHexString(String formattedvalue) {
        int lenght = formattedvalue.length();
        if (lenght % 2 != 0) {
            logger.log(Level.WARNING, "Hexadecimal string length needs to be even: {0}", formattedvalue);
            throw new IllegalArgumentException("Hexadecimal string length needs to be even: " + formattedvalue);
        }

        byte[] value = new byte[lenght / 2];
        int h;
        int l;
        int i = 0;
        int i2 = 0;
        while (i < value.length) {
            h = parseHexChar(formattedvalue.charAt(i2++));
            l = parseHexChar(formattedvalue.charAt(i2++));
            value[i++] = (byte) ((h << 4) | l);
        }
        return value;
    }

    private static int parseHexChar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        logger.log(Level.WARNING, "Illegal hexadecimal character: {0}", c);
        throw new IllegalArgumentException("Illegal hexadecimal character: " + c);
    }

    private static final char[] HEXCHARS = "0123456789ABCDEF".toCharArray();

    public static String formatHexString(byte[] value) {
        char[] hex = new char[value.length * 2];
        int i2 = 0;
        for (byte b : value) {
            hex[i2++] = HEXCHARS[(b >> 4) & 0x0f];
            hex[i2++] = HEXCHARS[b & 0x0f];
        }
        return new String(hex);
    }

    public static String fixedSplit(String s) {
        return String.join("\n", s.split("(?<=\\G.{76})"));
    }
}
