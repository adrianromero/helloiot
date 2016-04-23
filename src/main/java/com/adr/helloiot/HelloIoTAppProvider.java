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

package com.adr.helloiot;

import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.device.Device;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import javax.inject.Inject;

/**
 *
 * @author adrian
 */
public class HelloIoTAppProvider implements Provider<HelloIoTApp> {

    private final Device[] appdevices;
    private final Unit[] appunits;
    
    @Inject
    public HelloIoTAppProvider(
            @Named("app.devices") Device[] appdevices,
            @Named("app.units") Unit[] appunits) {
        this.appdevices = appdevices;
        this.appunits = appunits;
    }
    
    @Override
    public HelloIoTApp get() {
        HelloIoTApp app = new HelloIoTApp();
        app.loadNamespace(appdevices, appunits);
        return app;
    } 
}
