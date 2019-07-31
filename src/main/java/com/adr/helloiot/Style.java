//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2018-2019 Adrián Romero Corchado.
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

import javafx.scene.layout.StackPane;

public enum Style {
    
    PRETTY("/com/adr/helloiot/styles/main"),
    LIGHT("/com/adr/helloiot/styles/main-light"),
    DARK("/com/adr/helloiot/styles/main-dark"),
    CLASSIC("/com/adr/helloiot/styles/empty");
    
    private final String styleurl;
    
    private Style(String styleurl) {
        this.styleurl = styleurl;
    }
    
    public String getStyleURL() {
        return styleurl;
    }
    
    public static void changeStyle(StackPane root, Style style) {
        
        Object currentstyle = root.getProperties().get("currentstyleurl");
        if (!style.name().equals(currentstyle)) {
            root.getStylesheets().clear();
            if (HelloPlatform.getInstance().isFullScreen()) {
                root.getStylesheets().add(Style.class.getResource("/com/adr/helloiot/styles/fullscreen.css").toExternalForm());
            } else {
                root.getStylesheets().add(Style.class.getResource("/com/adr/helloiot/styles/root.hover.css").toExternalForm());
                root.getStylesheets().add(Style.class.getResource(style.getStyleURL() + ".hover.css").toExternalForm());
            }     
            // hover pseudoclass definiton must go before armed pseudoclass definition
            root.getStylesheets().add(Style.class.getResource("/com/adr/helloiot/styles/root.css").toExternalForm());
            root.getStylesheets().add(Style.class.getResource(style.getStyleURL() + ".css").toExternalForm());  
            root.getProperties().put("currentstyleurl", style.name());
        }
    }
}
