//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2019 Adrián Romero Corchado.
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
package com.adr.helloiot.topicinfo;

import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatDecimal;
import com.adr.helloiotlib.format.StringFormatLong;

public enum GaugeNodeFormat {
    LONG("", 0, jsonpath -> new StringFormatLong(jsonpath)),
    DOUBLE("", 2, jsonpath -> new StringFormatDecimal(jsonpath, "0.00")),
    DECIMAL("", 3, jsonpath -> new StringFormatDecimal(jsonpath, "0.000")),
    TEMPERATUREC("°C", 1, jsonpath -> new StringFormatDecimal(jsonpath, "0.0°C")),
    TEMPERATUREF("°F", 1, jsonpath -> new StringFormatDecimal(jsonpath, "0.0°F")),
    HUMIDITY("%", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0'%'")),
    PRESSURE("hPa", 2, jsonpath -> new StringFormatDecimal(jsonpath, "0.00' hPa'")),
    CO2("ppm", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0' ppm'")),
    WINDKMH("Km/h", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0' Km/h'")),
    WINDMPH("mph", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0' mph'")),
    CURRENT("A", 2, jsonpath -> new StringFormatDecimal(jsonpath, "0.00' A'")),
    VOLTAGE("V", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0' V'")),
    POWER("W", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0' W'")),
    LUX("lx", 0, jsonpath -> new StringFormatDecimal(jsonpath, "0' lx'"));
    
    private final String unit;
    private final int decimals;
    private final GaugeStringFormatBuilder stringformatbuilder;
    
    GaugeNodeFormat(String unit, int decimals, GaugeStringFormatBuilder stringformatbuilder) {
        this.unit = unit;
        this.decimals = decimals;
        this.stringformatbuilder = stringformatbuilder;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public int getDecimals() {
        return decimals;
    }
    
    public StringFormat createFormat(String jsonpath) {
        return stringformatbuilder.create(jsonpath);
    }
    
    @FunctionalInterface
    public interface GaugeStringFormatBuilder {
        StringFormat create(String jsonpath);
    }
}
