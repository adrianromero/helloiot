//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2018 Adrián Romero Corchado.
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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.unit.UnitPage;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author adrian
 */
public class ApplicationUnitPagesBasic implements ApplicationUnitPages {

    @Override
    public List<UnitPage> getUnitPages() {

        ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

        // Application pages
        UnitPage notfound = new UnitPage("notfound", IconBuilder.create(FontAwesome.FA_BAN, 24.0).styleClass("icon-fill").build(), resources.getString("page.notfound"));
        notfound.setEmptyLabel(resources.getString("label.notfound"));

        UnitPage start = new UnitPage("start", IconBuilder.create(FontAwesome.FA_STAR_O, 24.0).styleClass("icon-fill").build(), resources.getString("page.start"));
        start.setEmptyLabel(resources.getString("label.start"));
        start.setSystem(true);
        
        // Security pages       
        UnitPage security = new UnitPage("security", IconBuilder.create(FontAwesome.FA_KEY, 24.0).build(), resources.getString("page.security"));
        security.setEmptyLabel(resources.getString("label.locked"));
        security.setMaxSize(400.0, 150.0);        
        security.setSystem(true);

        UnitPage securityarming = new UnitPage("security_arming", IconBuilder.create(FontAwesome.FA_KEY, 24.0).build(), resources.getString("page.securityarming"));
        securityarming.setEmptyLabel(resources.getString("label.locked"));
        securityarming.setMaxSize(400.0, 150.0);
        securityarming.setSystem(true);

        UnitPage securitylocked = new UnitPage("security_locked", IconBuilder.create(FontAwesome.FA_LOCK, 24.0).build(), resources.getString("page.securitylocked"));
        securitylocked.setEmptyLabel(resources.getString("label.locked"));
        securitylocked.setSystem(true);

        UnitPage emergency = new UnitPage("emergency", IconBuilder.create(FontAwesome.FA_HEARTBEAT, 24.0).build(), resources.getString("page.emergency"));
        emergency.setEmptyLabel(resources.getString("label.locked"));
        emergency.setMaxSize(400.0, 150.0);        
        emergency.setSystem(true);        

        return Arrays.asList(
                notfound, start,
                security, securityarming, securitylocked, emergency,
                new UnitPage("config", IconBuilder.create(FontAwesome.FA_SLIDERS, 24.0).styleClass("icon-fill").build(), resources.getString("page.config")),
                new UnitPage("Lights", IconBuilder.create(FontAwesome.FA_LIGHTBULB_O, 24.0).styleClass("icon-fill").build(), resources.getString("page.lights")));
    }
}
