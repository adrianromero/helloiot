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

import com.adr.helloiot.mqtt.ManagerMQTT;
import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.helloiot.unit.Unit;
import com.adr.helloiot.device.Device;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiot.device.DeviceSwitch;
import com.adr.helloiot.device.TreePublish;
import com.adr.helloiot.device.TreePublishSubscribe;
import com.adr.helloiot.device.format.MiniVar;
import com.adr.helloiot.media.SilentClipFactory;
import com.adr.helloiot.media.StandardClipFactory;
import com.adr.helloiot.tradfri.ManagerTradfri;
import com.adr.helloiot.unit.UnitPage;
import com.adr.helloiot.util.CompletableAsync;
import com.adr.helloiot.util.HTTPUtils;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class HelloIoTApp {

    private final static Logger LOGGER = Logger.getLogger(HelloIoTApp.class.getName());

    public final static String SYS_VALUE_ID = "SYSVALUESID";
    public final static String SYS_EVENT_ID = "SYSEVENTSID";

    public final static String SYS_UNITPAGE_ID = "SYSUNITPAGEID";
    public final static String SYS_BEEPER_ID = "SYSBEEPERID";
    public final static String SYS_BUZZER_ID = "SYSBUZZERID";

    private final List<UnitPage> appunitpages = new ArrayList<>();
    private final List<Unit> appunits = new ArrayList<>();
    private final List<Device> appdevices = new ArrayList<>();

    private final TopicsManager topicsmanager;
    private final MainNode mainnode;
    private final ResourceBundle resources;

    private HelloIoTAppPublic apppublic = null;
    private DeviceSimple appunitpage;
    private DeviceSwitch appbeeper;
    private DeviceSimple appbuzzer;

    private EventHandler<ActionEvent> exitevent = null;
    private final Runnable styleConnection;

    public HelloIoTApp(Map<String, MiniVar> config) {

        // Load resources
        resources = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");

        // System and Application devices units
        addSystemDevicesUnits(config.get("client.topicsys").asString());
        addAppDevicesUnits(config.get("client.topicapp").asString());

        ManagerComposed manager = new ManagerComposed();
        manager.addManagerProtocol(
                "_LOCAL_/",
                new ManagerLocal(
                        config.get("client.topicapp").asString()));
        
        if (HTTPUtils.getAddress(config.get("tradfri.host").asString()) != null) {
            manager.addManagerProtocol(
                    "TRÅDFRI/",
                    new ManagerTradfri(
                                    config.get("tradfri.host").asString(),
                                    config.get("tradfri.identity").asString(),
                                    config.get("tradfri.psk").asString()));
        }
        
        if (HTTPUtils.getAddress(config.get("mqtt.host").asString()) != null) {
            boolean websockets = config.get("mqtt.websockets").asBoolean();
            boolean ssl = config.get("mqtt.ssl").asBoolean();
            String protocol = websockets
                    ? (ssl ? "wss" : "ws")
                    : (ssl ? "ssl" : "tcp");
            Properties sslproperties;
            if (ssl) {
                sslproperties = new Properties();
                sslproperties.setProperty("com.ibm.ssl.protocol", config.get("mqtt.protocol").asString());
                sslproperties.setProperty("com.ibm.ssl.keyStore", config.get("mqtt.keystore").asString());
                sslproperties.setProperty("com.ibm.ssl.keyStorePassword", config.get("mqtt.keystorepassword").asString());
                sslproperties.setProperty("com.ibm.ssl.keyStoreType", "JKS");
                sslproperties.setProperty("com.ibm.ssl.trustStore", config.get("mqtt.truststore").asString());
                sslproperties.setProperty("com.ibm.ssl.trustStorePassword", config.get("mqtt.truststorepassword").asString());
                sslproperties.setProperty("com.ibm.ssl.trustStoreType", "JKS");                
            } else {
                sslproperties = null;
            }
            String mqtturl = protocol + "://" + config.get("mqtt.host").asString()  + ":" + Integer.toString(config.get("mqtt.port").asInt());     
            manager.addManagerProtocol(
                    "",
                    new ManagerMQTT(
                                    mqtturl,
                                    config.get("mqtt.username").asString(),
                                    config.get("mqtt.password").asString(),
                                    config.get("mqtt.clientid").asString(),
                                    config.get("mqtt.connectiontimeout").asInt(),
                                    config.get("mqtt.keepaliveinterval").asInt(),
                                    config.get("mqtt.defaultqos").asInt(),
                                    config.get("mqtt.version").asInt(),
                                    config.get("mqtt.maxinflight").asInt(),
                                    sslproperties));
        }

        styleConnection = config.get("app.retryconnection").asBoolean() ? this::tryConnection : this::oneConnection;

        mainnode = new MainNode(
                this,
                Platform.isSupported(ConditionalFeature.MEDIA) ? new StandardClipFactory() : new SilentClipFactory(),
                config.get("app.clock").asBoolean(),
                config.get("app.exitbutton").asBoolean());
        
        topicsmanager = new TopicsManager(manager);
        topicsmanager.setOnConnectionLost(t -> {
            LOGGER.log(Level.WARNING, "Connection lost to broker.", t);
            Platform.runLater(() -> {
                MessageUtils.showException(MessageUtils.getRoot(mainnode.getNode()), resources.getString("title.errorconnection"), t.getLocalizedMessage(), t, ev -> {
                    exitevent.handle(new ActionEvent());
                });
            });
        });        
    }

    public void addUnitPages(List<UnitPage> unitpages) {
        appunitpages.addAll(unitpages);
    }

    public void addDevicesUnits(List<Device> devices, List<Unit> units) {
        appdevices.addAll(devices);
        appunits.addAll(units);
    }

    public void addServiceDevicesUnits() {
        ServiceLoader<ApplicationDevicesUnits> devicesunitsloader = ServiceLoader.load(ApplicationDevicesUnits.class);
        for (ApplicationDevicesUnits c : devicesunitsloader) {
            addDevicesUnits(c.getDevices(), c.getUnits());
        }
    }

    private void addAppDevicesUnits(String topicapp) {

        DeviceSimple unitpage = new DeviceSimple();
        unitpage.setTopic(topicapp + "/unitpage");
        unitpage.setId(SYS_UNITPAGE_ID);

        DeviceSwitch beeper = new DeviceSwitch();
        beeper.setTopic(topicapp + "/beeper");
        beeper.setId(SYS_BEEPER_ID);

        DeviceSimple buzzer = new DeviceSimple();
        buzzer.setTopic(topicapp + "/buzzer");
        buzzer.setId(SYS_BUZZER_ID);

        addDevicesUnits(Arrays.asList(unitpage, beeper, buzzer), Collections.emptyList());
    }

    private void addSystemDevicesUnits(String topicsys) {
        TreePublish sysevents = new TreePublish();
        sysevents.setTopic(topicsys + "/events");
        sysevents.setId(SYS_EVENT_ID);

        TreePublishSubscribe sysstatus = new TreePublishSubscribe();
        sysstatus.setTopic(topicsys + "/status");
        sysstatus.setId(SYS_VALUE_ID);

        addDevicesUnits(Arrays.asList(sysevents, sysstatus), Collections.emptyList());
    }

    public void addFXMLFileDevicesUnits(String filedescriptor) throws HelloIoTException {

        try {
            String versionfxml = HelloPlatform.getInstance().isPhone() ? "_mobile.fxml" : ".fxml";
            URL fxmlurl;
            ResourceBundle fxmlresources;
            if (filedescriptor.startsWith("local:")) {
                // Is a local resource
                fxmlurl = HelloIoTApp.class.getResource("/" + filedescriptor.substring(6) + versionfxml);
                fxmlresources = ResourceBundle.getBundle(filedescriptor.substring(6));
            } else {
                // Is a file       
                fxmlurl = new File(filedescriptor + versionfxml).toURI().toURL();
                File file = new File(filedescriptor);
                URL[] urls = {file.getAbsoluteFile().getParentFile().toURI().toURL()};
                ClassLoader loader = new URLClassLoader(urls);
                fxmlresources = ResourceBundle.getBundle(file.getName(), Locale.getDefault(), loader);
            }

            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(fxmlurl);
            if (fxmlresources != null) {
                fxmlloader.setResources(fxmlresources);
            }
            DevicesUnits du = fxmlloader.load();
            addDevicesUnits(du.getDevices(), du.getUnits());
        } catch (IOException | MissingResourceException ex) {
            throw new HelloIoTException(String.format(resources.getString("exception.cannotloadfxml"), filedescriptor), ex);
        }
    }

    public void setOnDisconnectAction(EventHandler<ActionEvent> exitevent) {
        this.exitevent = exitevent;
    }

    public void startAndConstruct() {

        // External services
        ServiceLoader<ApplicationUnitPages> unitpagesloader = ServiceLoader.load(ApplicationUnitPages.class);
        for (ApplicationUnitPages c : unitpagesloader) {
            appunitpages.addAll(c.getUnitPages());
        }
        // Add "main" unit page if needed
        if (!existsUnitPageMain()) {
            UnitPage main = new UnitPage("main", IconBuilder.create(FontAwesome.FA_HOME, 24.0).styleClass("icon-fill").build(), resources.getString("page.main"));
            main.setOrder(0);
            appunitpages.add(main);
        }
        mainnode.construct(appunitpages);

        // Construct All
        appunits.forEach((s) -> {
            s.construct(this.getAppPublic());
        });
        appdevices.forEach((d) -> {
            d.construct(topicsmanager);
        });

        connection();
    }

    private boolean existsUnitPageMain() {
        return appunitpages.stream().anyMatch((p) -> ("main".equals(p.getName()))); 
    }

    private void connection() {
        // connect !!!
        styleConnection.run();
    }

    private void oneConnection() {
        mainnode.showConnecting();
        Futures.addCallback(topicsmanager.open(), new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object v) {
                mainnode.hideConnecting();
                startUnits();
            }

            @Override
            public void onFailure(Throwable ex) {
                mainnode.hideConnecting();
                MessageUtils.showException(MessageUtils.getRoot(mainnode.getNode()), resources.getString("title.errorconnection"), ex.getLocalizedMessage(), ex, ev -> {
                    exitevent.handle(new ActionEvent());
                });
            }
        }, CompletableAsync.fxThread());
    }

    private void tryConnection() {
        mainnode.showConnecting();
        Futures.addCallback(topicsmanager.open(), new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object v) {
                mainnode.hideConnecting();
                startUnits();
            }

            @Override
            public void onFailure(Throwable ex) {
                new Timeline(new KeyFrame(Duration.millis(2500), ev -> {
                    tryConnection();
                })).play();
            }
        }, CompletableAsync.fxThread());
    }

    public void stopAndDestroy() {
        stopUnits();
        topicsmanager.close();

        // Destroy all units
        appunits.forEach(Unit::destroy);
        appdevices.forEach(Device::destroy);

        mainnode.destroy();
    }

    private void startUnits() {
        appunits.forEach(Unit::start);
    }

    private void stopUnits() {
        appunits.forEach(Unit::stop);
    }

    public MainNode getMainNode() {
        return mainnode;
    }

    public DeviceSimple getUnitPage() {
        if (appunitpage == null) {
            appunitpage = ((DeviceSimple) getDevice(SYS_UNITPAGE_ID));
        }
        return appunitpage;
    }

    public DeviceSwitch getBeeper() {
        if (appbeeper == null) {
            appbeeper = ((DeviceSwitch) getDevice(SYS_BEEPER_ID));
        }
        return appbeeper;
    }

    public DeviceSimple getBuzzer() {
        if (appbuzzer == null) {
            appbuzzer = ((DeviceSimple) getDevice(SYS_BUZZER_ID));
        }
        return appbuzzer;
    }

    public List<Unit> getUnits() {
        return appunits;
    }

    public List<Device> getDevices() {
        return appdevices;
    }

    public Device getDevice(String id) {
        for (Device d : appdevices) {
            if (id.equals(d.getId())) {
                return d;
            }
        }
        return null;
    }

    public MiniVar readSYSStatus(String branch) {
        return ((TreePublishSubscribe) getDevice(SYS_VALUE_ID)).readMessage(branch);
    }

    public String loadSYSStatus(String branch) {
        return ((TreePublishSubscribe) getDevice(SYS_VALUE_ID)).loadMessage(branch);
    }

    public void sendSYSStatus(String branch, String message) {
        ((TreePublishSubscribe) getDevice(SYS_VALUE_ID)).sendMessage(branch, message);
    }

    public void sendSYSStatus(String branch, MiniVar message) {
        ((TreePublishSubscribe) getDevice(SYS_VALUE_ID)).sendMessage(branch, message);
    }

    public final void sendSYSEvent(String branch, String message) {
        ((TreePublish) getDevice(SYS_EVENT_ID)).sendMessage(branch, message);
    }

    public final void sendSYSEvent(String branch, MiniVar message) {
        ((TreePublish) getDevice(SYS_EVENT_ID)).sendMessage(branch, message);
    }

    public void sendSYSEvent(String branch, String message, long delay) {
        ((TreePublish) getDevice(SYS_EVENT_ID)).sendMessage(branch, message, delay);
    }

    public void sendSYSEvent(String branch, MiniVar message, long delay) {
        ((TreePublish) getDevice(SYS_EVENT_ID)).sendMessage(branch, message, delay);
    }

    public final void sendSYSEvent(String branch) {
        ((TreePublish) getDevice(SYS_EVENT_ID)).sendMessage(branch);
    }

    public void cancelSYSEventTimer() {
        ((TreePublish) getDevice(SYS_EVENT_ID)).cancelTimer();
    }

    public HelloIoTAppPublic getAppPublic() {
        if (apppublic == null) {
            apppublic = new HelloIoTAppPublic(this);
        }
        return apppublic;
    }
}
