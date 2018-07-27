//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018 Adrián Romero Corchado.
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

import java.util.Arrays;

/**
 *
 * @author adrian
 */
public class MiniVarBytes implements MiniVar {
    
    public final static MiniVar NULL = new MiniVarBytes(null);
    
    public final byte[] value;
    
    public MiniVarBytes(byte[] value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return value == null ? "" : Arrays.toString(value);
    }

    @Override
    public double asDouble() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public int asInt() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("Not supported.");
    }   

    @Override
    public byte[] asBytes() {
        return value;
    }
    
    @Override
    public boolean isEmpty() {
        return value == null;
    }
}
