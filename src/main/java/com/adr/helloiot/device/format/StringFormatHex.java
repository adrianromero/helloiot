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

package com.adr.helloiot.device.format;

import com.google.common.base.Strings;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author adrian
 */
public class StringFormatHex implements StringFormat {
    
    public static final StringFormat INSTANCE = new StringFormatIdentity();

    @Override
    public String format(byte[] value) {
        if (value == null || value.length == 0) {
            return "";
        }       
        return formatHexString(value);
    }

    @Override
    public byte[] parse(String formattedvalue) {
        if (Strings.isNullOrEmpty(formattedvalue)) {
            return new byte[0];
        }  
        return parseHexString(formattedvalue);
    }
    
    public static byte[] parseHexString(String formattedvalue) {
        int lenght = formattedvalue.length();        
        if (lenght % 2 != 0) {
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
    
    private static int parseHexChar(char c)  {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }     
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
}
