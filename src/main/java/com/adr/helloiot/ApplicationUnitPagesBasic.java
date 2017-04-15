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

        UnitPage cfg = new UnitPage("config", IconBuilder.create(FontAwesome.FA_SLIDERS, 24.0).build(), resources.getString("page.config"));

        UnitPage notfound = new UnitPage("notfound", IconBuilder.create(FontAwesome.FA_BAN, 24.0).build(), resources.getString("page.notfound"));
        notfound.setEmptyLabel(resources.getString("label.notfound"));

        UnitPage start = new UnitPage("start", IconBuilder.create(FontAwesome.FA_STAR_O, 24.0).build(), resources.getString("page.start"));
        start.setEmptyLabel(resources.getString("label.start"));
        start.setSystem(true);

        return Arrays.asList(cfg, notfound, start);
    }
}
