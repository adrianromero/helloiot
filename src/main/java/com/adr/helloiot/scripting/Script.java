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
package com.adr.helloiot.scripting;

import java.util.Map;

/**
 *
 * @author adrian
 */
public abstract class Script {
    public abstract void putScopeObject(String name, Object obj) throws ScriptExecException;
    public abstract void putScopeFunction(String name, Object obj, String method, int params) throws ScriptExecException;
    public abstract void putScopeMap(Map<String, Object> params) throws ScriptExecException;
    public abstract Object exec(String script) throws ScriptExecException;
    
    protected final String createArgs(int params) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append("p");
            result.append(Integer.toString(i));
        }
        return result.toString();
    }
}
