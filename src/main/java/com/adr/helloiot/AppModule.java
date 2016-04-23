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

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiot.device.Device;
import com.adr.helloiot.media.ClipFactory;
import com.adr.helloiot.media.SilentClipFactory;
import com.adr.helloiot.media.StandardClipFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;

/**
 *
 * @author adrian
 */
public class AppModule extends AbstractModule {
    
    private final URL configfile;
    
    public AppModule(URL configfile) {
        this.configfile = configfile;
    }
    
    @Override
    protected void configure() { 
        // Configuration
        bind(URL.class).annotatedWith(Names.named("arg.configuration")).toInstance(configfile);
        
        Properties properties = new Properties();
        // default values    
        properties.setProperty("app.title", "Hello IoT");
        properties.setProperty("app.client", "helloiot");
        properties.setProperty("app.exitbutton", "false");
        properties.setProperty("app.fullscreen", "true");

        properties.setProperty("mqtt.url", "tcp://localhost:1883");
        properties.setProperty("mqtt.username", "");
        properties.setProperty("mqtt.password", "");
        properties.setProperty("mqtt.connectiontimeout", "30");
        properties.setProperty("mqtt.keepaliveinterval", "60");
        properties.setProperty("mqtt.qos", "1");
        
        properties.setProperty("clock.pattern", ""); // do not show clock
        properties.setProperty("devicesunits", ""); // do not load any fxml

        try (InputStream in = configfile.openStream()) {            
            properties.load(in);
        } catch (IOException ex) {
            Logger.getLogger(AppModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        Names.bindProperties(binder(), properties);
        
        // External services
        List<UnitPage> appunitpages = new ArrayList<>();
        List<Device> appdevices = new ArrayList<>();
        List<Unit> appunits = new ArrayList<>();
        
        ServiceLoader<ApplicationUnitPages> unitpagesloader = ServiceLoader.load(ApplicationUnitPages.class);
        unitpagesloader.forEach(c -> {
            c.init(properties);
            appunitpages.addAll(c.getUnitPages());
        });
        // Add "main" unit page if needed
        if (!appunitpages.stream().anyMatch(p -> "main".equals(p.getName()))) {
            ResourceBundle resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
            UnitPage main = new UnitPage("main", IconBuilder.create(FontAwesome.FA_HOME, 24.0).build(), resources.getString("page.main"));
            main.setOrder(0);     
            appunitpages.add(main);
        }
         
        ServiceLoader<ApplicationDevicesUnits> devicesunitsloader = ServiceLoader.load(ApplicationDevicesUnits.class);
        devicesunitsloader.forEach(c -> {
            c.init(properties);
            appdevices.addAll(c.getDevices());
            appunits.addAll(c.getUnits());                 
        });
        
        bind(Device[].class).annotatedWith(Names.named("app.devices")).toInstance(appdevices.toArray(new Device[appdevices.size()]));
        bind(Unit[].class).annotatedWith(Names.named("app.units")).toInstance(appunits.toArray(new Unit[appunits.size()]));
        bind(UnitPage[].class).annotatedWith(Names.named("app.unitpages")).toInstance(appunitpages.toArray(new UnitPage[appunitpages.size()]));
        
        // An application singleton
        bind(MQTTManager.class).toProvider(MQTTManagerProvider.class).in(Scopes.SINGLETON);       
        bind(HelloIoTApp.class).toProvider(HelloIoTAppProvider.class).in(Scopes.SINGLETON);
        Class<? extends ClipFactory> clipfactoryclass = Platform.isSupported(ConditionalFeature.MEDIA) ? StandardClipFactory.class : SilentClipFactory.class;
        bind(ClipFactory.class).to(clipfactoryclass).in(Scopes.SINGLETON);
    }
}
