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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 *
 * @author adrian
 */
public class StringFormatDecimal implements StringFormat {
    
    private static NumberFormat GENERALFORMAT = NumberFormat.getNumberInstance();
    
    private NumberFormat format;
    private String pattern;
    
    public StringFormatDecimal(String pattern) {
        setPattern(pattern);
    }
   
    public StringFormatDecimal() {
        setPattern("0");
    }

    public final void setPattern(String pattern) {
        this.format = new DecimalFormat(pattern);
        this.pattern = pattern;
    }

    public final String getPattern() {
        return pattern;
    }
    
    @Override
    public String format(String value) {
        
        if (value == null) {
            return "";
        }
        
        try {
            return format.format(Double.parseDouble(value));
        } catch (NumberFormatException ex) {
            return "ERROR";
        }
    }
    
    @Override
    public String parse(String formattedvalue) {
        try {
            return format.parse(formattedvalue).toString();
        } catch (ParseException ex) {
            return parseGeneral(formattedvalue);
        }
    }  
    
    protected static String parseGeneral(String formattedvalue) {
        try {
            return GENERALFORMAT.parse(formattedvalue).toString();
        } catch (ParseException ex) {            
            return Integer.toString(0);
        }        
    }
}
