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

import com.adr.helloiotlib.unit.Units;
import com.adr.helloiot.graphic.IconStatus;
import com.adr.helloiot.device.DeviceSimple;
import com.adr.helloiotlib.app.IoTApp;
import com.adr.helloiot.graphic.IconNull;
import javafx.event.ActionEvent;

/**
 *
 * @author adrian
 */
public class ButtonSimple extends ButtonBase {

    private final static IconStatus ICONNULL = new IconNull();
    private IconStatus iconbuilder = ICONNULL;

    private DeviceSimple device = null;
    private final Object messageHandler = Units.messageHandler(this::updateStatus);

    private void updateStatus(byte[] status) {
        button.setGraphic(iconbuilder.buildIcon(device.getFormat().getValueFormat(status)));
    }

    @Override
    public void construct(IoTApp app) {
        super.construct(app);
        device.subscribeStatus(messageHandler);
        updateStatus(null);
    }

    @Override
    public void destroy() {
        super.destroy();
        device.unsubscribeStatus(messageHandler);
    }

    public void setDevice(DeviceSimple device) {
        this.device = device;
        if (getLabel() == null) {
            setLabel(device.getProperties().getProperty("label"));
        }
        if (getIconStatus() == ICONNULL) {
            setIconStatus(device.getIconStatus());
        }
    }

    public DeviceSimple getDevice() {
        return device;
    }

    public void setIconStatus(IconStatus iconbuilder) {
        this.iconbuilder = iconbuilder;
    }

    public IconStatus getIconStatus() {
        return iconbuilder;
    }

    @Override
    protected void doRun(ActionEvent event) {
        device.sendStatus(device.rollNextStatus());
    }
}
