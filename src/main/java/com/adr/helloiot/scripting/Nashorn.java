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
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author adrian
 */
public class Nashorn extends Script {

    private static final ScriptEngineManager SCRIPTMANAGER = new ScriptEngineManager();
    
    private final ScriptEngine scriptengine = SCRIPTMANAGER.getEngineByName("javascript");
    
    @Override
    public void putScopeObject(String name, Object obj) throws ScriptExecException {
        scriptengine.put(name, obj);
    }   
    
    @Override
    public void putScopeFunction(String name, Object obj, String method, int params) throws ScriptExecException {
        try {         
            scriptengine.put("__" + name, obj);
            scriptengine.eval("function " + name + "(" + createArgs(params) + ") {return __" + name +"." + method + "(" + createArgs(params) + ");}");
        } catch (ScriptException ex) {
            throw new ScriptExecException(ex);
        }
    }
    
    @Override
    public void putScopeMap(Map<String, Object> params) throws ScriptExecException {
        scriptengine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(params);
    }
    
    @Override
    public Object exec(String script) throws ScriptExecException {
        try {
            return scriptengine.eval(script);
        } catch (ScriptException ex) {
            throw new ScriptExecException(ex);
        }
    } 
}
