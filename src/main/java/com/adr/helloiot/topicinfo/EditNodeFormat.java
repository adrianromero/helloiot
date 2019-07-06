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

import com.adr.fonticon.IconFontGlyph;
import com.adr.helloiotlib.format.StringFormat;
import com.adr.helloiotlib.format.StringFormatBase64;
import com.adr.helloiotlib.format.StringFormatDecimal;
import com.adr.helloiotlib.format.StringFormatHex;
import com.adr.helloiotlib.format.StringFormatIdentity;
import com.adr.helloiotlib.format.StringFormatLong;

public enum EditNodeFormat {
    STRING(null, jsonpath -> new StringFormatIdentity(jsonpath)),
    LONG(IconFontGlyph.FA_SOLID_HASHTAG, jsonpath -> new StringFormatLong(jsonpath)),
    DOUBLE(IconFontGlyph.FA_SOLID_SLIDERS_H, jsonpath -> new StringFormatDecimal(jsonpath, "0.00")),
    DECIMAL(IconFontGlyph.FA_SOLID_RULER, jsonpath -> new StringFormatDecimal(jsonpath, "0.000")),
    TEMPERATUREC(IconFontGlyph.FA_SOLID_THERMOMETER_QUARTER, jsonpath -> new StringFormatDecimal(jsonpath, "0.0°C")),
    TEMPERATUREF(IconFontGlyph.FA_SOLID_THERMOMETER_QUARTER, jsonpath -> new StringFormatDecimal(jsonpath, "0.0°F")),
    HUMIDITY(IconFontGlyph.FA_SOLID_TINT, jsonpath -> new StringFormatDecimal(jsonpath, "0'%'")),
    WINDKMH(IconFontGlyph.FA_SOLID_WIND, jsonpath -> new StringFormatDecimal(jsonpath, "0' Km/h'")),
    WINDMPH(IconFontGlyph.FA_SOLID_WIND, jsonpath -> new StringFormatDecimal(jsonpath, "0' mph'")),
    BASE64(IconFontGlyph.FA_SOLID_CODE, jsonpath -> new StringFormatBase64()),
    HEX(IconFontGlyph.FA_SOLID_MICROCHIP, jsonpath -> new StringFormatHex());
    
    private final IconFontGlyph glyph;
    private final StringFormatBuilder stringformatbuilder;
    
    EditNodeFormat(IconFontGlyph glyph, StringFormatBuilder stringformatbuilder) {
        this.glyph = glyph;
        this.stringformatbuilder = stringformatbuilder;
    }
    
    public IconFontGlyph createGlyph() {
        return glyph;
    }
    
    public StringFormat createFormat(String jsonpath) {
        return stringformatbuilder.create(jsonpath);
    }
    
    @FunctionalInterface
    public interface StringFormatBuilder {
        StringFormat create(String jsonpath);
    }
}
