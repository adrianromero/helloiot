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
import java.util.Base64;

/**
 *
 * @author adrian
 */
public class StringFormatBase64 implements StringFormat {
    
    public static final StringFormat INSTANCE = new StringFormatBase64();
    
    @Override
    public String getName() {
        return "BASE64";
    }

    @Override
    public String format(byte[] value) {
        if (value == null || value.length == 0) {
            return "";
        }          
        return Base64.getMimeEncoder().encodeToString(value);
    }

    @Override
    public byte[] parse(String formattedvalue) {
        if (Strings.isNullOrEmpty(formattedvalue)) {
            return new byte[0];
        }  
        return Base64.getMimeDecoder().decode(formattedvalue);
    }
}
