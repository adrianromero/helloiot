//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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
package com.adr.helloiotlib.format;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;

/**
 *
 * @author adrian
 */
public class StringFormatDecimal extends StringFormatPath {

    private final static Logger logger = Logger.getLogger(StringFormatDecimal.class.getName());

    private static NumberFormat GENERALFORMAT = NumberFormat.getNumberInstance(Locale.US);
    public static StringFormat INTEGER = new StringFormatDecimal();
    public static StringFormat DOUBLE = new StringFormatDecimal(null, "0.00");
    public static StringFormat DECIMAL = new StringFormatDecimal(null, "0.000");
    public static StringFormat DEGREES = new StringFormatDecimal(null, "0.0°C");
    public static StringFormat PERCENTAGE = new StringFormatDecimal(null, "0'%'");

    private NumberFormat format;
    private String pattern;

    public StringFormatDecimal(String jsonpath, String pattern) {
        super(jsonpath);
        setPattern(pattern);
    }

    public StringFormatDecimal(String jsonpath) {
        this(jsonpath, "0");
    }

    public StringFormatDecimal() {
        this(null, "0");
    }

    @Override
    public String toString() {
        if ("0".equals(pattern)) {
            return "INT";
        } else if ("0.00".equals(pattern)) {
            return "DOUBLE";
        } else if ("0.000".equals(pattern)) {
            return "DECIMAL";
         } else if ("0'%'".equals(pattern)) {
            return "PERCENTAGE";
        } else if ("0.0°".equals(pattern)) {
            return "DEGREES";
        } else {
            return "DEC (" + pattern + ")";
        }
    }

    public final void setPattern(String pattern) {
        this.format = new DecimalFormat(pattern);
        this.pattern = pattern;
    }

    public final String getPattern() {
        return pattern;
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_RIGHT;
    }

    @Override
    protected MiniVar valueImpl(String value) {
        if (value == null || value.isEmpty()) {
            return MiniVarDouble.NULL;
        } else {
            try {
                return new MiniVarDouble(Double.parseDouble(value));
            } catch (NumberFormatException ex) {
                return MiniVarDouble.NULL;
            }
        }
    }

    @Override
    public String format(MiniVar value) {
        if (value.isEmpty()) {
            return "";
        } else {
            return format.format(value.asDouble());
        }
    }
    
    @Override
    public MiniVar parse(String formattedvalue) {
        if (formattedvalue == null || formattedvalue.isEmpty()) {
            return MiniVarDouble.NULL;
        } else {
            double d;
            try {
                d = format.parse(formattedvalue).doubleValue();
            } catch (ParseException ex) {
                try {                
                    d = GENERALFORMAT.parse(formattedvalue).doubleValue();
                } catch (ParseException ex2) {
                    logger.log(Level.WARNING, null, ex2);
                    throw new IllegalArgumentException(ex2);
                }
            }
            return new MiniVarDouble(d);
        }
    }
    
    @Override
    public byte[] devalue(MiniVar formattedvalue) {
        return formattedvalue.asString().getBytes(StandardCharsets.UTF_8);
    }
}
