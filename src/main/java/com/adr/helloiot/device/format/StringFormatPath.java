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
import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author adrian
 */
public abstract class StringFormatPath implements StringFormat {

    private String path;
    
    public StringFormatPath() {
        this(null);
    }

    public StringFormatPath(String jsonpath) {
        path = jsonpath;
    }

    public final void setPath(String path) {
        this.path = path;
    }

    public final String getPath() {
        return path;
    }

    protected abstract String formatImpl(String value);
    protected abstract String parseImpl(String value);

    @Override
    public final String format(byte[] value) {
        if (path == null || path.isEmpty()) {
            // No JSON path -> Normal payload processing
            if (value == null || value.length == 0) {
                return "";
            }
            return formatImpl(new String(value, StandardCharsets.UTF_8));
        } else {
            // if value null or empty this will throw an exception
            // Note that this is a different behavior when path is null because here we expect a valid JSON.
            return formatImpl(JsonPath.<String>read(new String(value, StandardCharsets.UTF_8), path));
        }
    }

    @Override
    public final byte[] parse(String formattedvalue) {
        if (path != null && !path.isEmpty()) {
            throw new UnsupportedOperationException("Cannot create a full message if there is a valid JSON path.");
        }

        if (Strings.isNullOrEmpty(formattedvalue)) {
            return new byte[0];
        }

        return parseImpl(formattedvalue).getBytes(StandardCharsets.UTF_8);
    }
}
