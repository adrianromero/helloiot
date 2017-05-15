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
//.
package com.adr.helloiot.scripting;

import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/**
 *
 * @author adrian
 */
public class Rhino extends Script {

    private Scriptable scope;

    public Rhino() {
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1); // allways interpretive mode
            scope = cx.initStandardObjects();
        } finally {
            Context.exit();
        }
    }

    @Override
    public void putScopeObject(String name, Object obj) {
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1); // always interpretive mode          
            ScriptableObject.putProperty(scope, name, Context.javaToJS(obj, scope));
        } finally {
            Context.exit();            
        }            
    }

    @Override
    public void putScopeFunction(String name, Object obj, String method, int params) throws ScriptExecException {
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1); // always interpretive mode                     
            ScriptableObject.putProperty(scope, "__" + name, Context.javaToJS(obj, scope));         
            cx.evaluateString(scope, "function " + name + "(" + createArgs(params) + ") {return __" + name +"." + method + "(" + createArgs(params) + ");}", "<cmd>", 1, null);
        } finally {
            Context.exit();            
        }
    }

    @Override
    public void putScopeMap(Map<String, Object> params) throws ScriptExecException {
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1); // always interpretive mode     
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                ScriptableObject.putProperty(scope, entry.getKey(), Context.javaToJS(entry.getValue(), scope));
            }
        } finally {
            Context.exit();            
        }            
    }

    @Override
    public Object exec(String script) throws ScriptExecException {
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1); // always interpretive mode
            Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);
            if (result instanceof Undefined) {
                return null;
            } else {
                return result;
            }
        } finally {
            Context.exit();
        }
    }
}
