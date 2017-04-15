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
package com.adr.helloiot.unit;

import com.adr.helloiot.HelloIoTAppPublic;
import com.adr.helloiot.util.CompletableAsync;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.DefaultProperty;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author adrian
 */
@DefaultProperty("text")
public class ScriptCode {

    private static final ScriptEngineManager SCRIPTMANAGER = new ScriptEngineManager();

    private String engine = "nashorn";
    private String text = null;

    private HelloIoTAppPublic app;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void construct(HelloIoTAppPublic app) {
        this.app = app;
    }

    public CompletableAsync<Object> run() throws ScriptException {
        return run(null);
    }

    public CompletableAsync<Object> run(Map<String, Object> params) {

        return CompletableAsync.supplyAsync(() -> {
            try {
                ScriptEngine scriptengine = SCRIPTMANAGER.getEngineByName(engine);
                if (params != null) {
                    scriptengine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(params);
                }
                scriptengine.put("_app", (Supplier) () -> app);
                scriptengine.put("_allDevices", (Supplier) () -> app.getAllDevices());
                scriptengine.put("_device", (Function<String, ?>) (id) -> app.getDevice(id));
                return scriptengine.eval(text);
            } catch (ScriptException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    public static ScriptCode valueOf(String value) {
        ScriptCode sc = new ScriptCode();
        sc.setText(value);
        return sc;
    }
}
