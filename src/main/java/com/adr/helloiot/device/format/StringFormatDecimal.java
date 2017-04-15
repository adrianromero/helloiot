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
    public static StringFormat DEGREES = new StringFormatDecimal(null, "0.0°");

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
    public String formatImpl(String value) {
        return format.format(Double.parseDouble(value));
    }

    @Override
    public String parseImpl(String formattedvalue) {
        try {
            return format.parse(formattedvalue).toString();
        } catch (ParseException ex) {
            return parseGeneral(formattedvalue);
        }
    }

    @Override
    public Pos alignment() {
        return Pos.CENTER_RIGHT;
    }

    protected static String parseGeneral(String formattedvalue) {
        try {
            return GENERALFORMAT.parse(formattedvalue).toString();
        } catch (ParseException ex) {
            logger.log(Level.WARNING, null, ex);
            throw new IllegalArgumentException(ex);
        }
    }
}
