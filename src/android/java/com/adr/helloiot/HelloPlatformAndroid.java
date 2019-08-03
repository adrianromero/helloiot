//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
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
package com.adr.helloiot;

import android.view.Window;
import android.view.WindowManager;
import javafxports.android.FXActivity;
import com.adr.helloiot.scripting.Rhino;
import com.adr.helloiot.scripting.Script;
import java.io.IOException;
import java.io.File;
/**
 *
 * @author adrian
 */
public class HelloPlatformAndroid extends HelloPlatform {
    
    private final FXActivity context;

    public HelloPlatformAndroid() {
        context = FXActivity.getInstance();
    }
    
    @Override
    public String getHome() {
       return null; 
    }
         
    @Override
    public File getFile(String fileName) throws IOException {
        return new File(context.getFilesDir(), fileName);
    }
    
    @Override
    public boolean isFullScreen() {
        return true;
    } 
    
    @Override
    public Script getNewScript() {
        return new Rhino();
    }

    @Override
    public void keepON() {
        context.runOnUiThread(() -> {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        });
    }
}
