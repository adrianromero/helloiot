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

import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.HelloPlatform;
import com.adr.helloiot.scripting.Script;
import com.adr.helloiot.util.CompletableAsync;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.DefaultProperty;

/**
 *
 * @author adrian
 */
@DefaultProperty("text")
public class ScriptCode {

    private String text = null;
    private IoTApp app;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void construct(IoTApp app) {
        this.app = app;
    }

    public ListenableFuture<Object> run() {
        return run(null);
    }

    public ListenableFuture<Object> run(Map<String, Object> params) {

        return CompletableAsync.supplyAsync(() -> {
            Script script = HelloPlatform.getInstance().getNewScript();
            if (params != null) {
                script.putScopeMap(params);
            }
            script.putScopeFunction("_app", (Supplier) () -> app, "get", 0);
            script.putScopeFunction("_allDevices", (Supplier) () -> app.getAllDevices(), "get", 0);
            script.putScopeFunction("_device", (Function<String, ?>) (id) -> app.getDevice(id), "apply", 1);
            return script.exec(text);
        });
    }

    public static ScriptCode valueOf(String value) {
        ScriptCode sc = new ScriptCode();
        sc.setText(value);
        return sc;
    }
}
