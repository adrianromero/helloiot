package com.adr.helloiot.tradfri;

/**
 * Partly inspired by "com/ikea/tradfri/lighting/ipso/IPSOObjects.java"
 * Some of these values can be found in the official LwM2M registry:
 *     http://www.openmobilealliance.org/wp/OMNA/LwM2M/LwM2MRegistry.html
 *
 * @author r41d
 */
public class TradfriConstants {

    // Device types (contained in INSTANCE_ID = "9003")
    public static final int TYPE_REMOTE = 0;
    public static final int TYPE_BULB = 2;
    // The others need to be figured out by people who own these

    // Top level navigation
    public static final String DEVICES = "15001";
    public static final String GROUPS = "15004";

    // Values in JSON data
    public static final String NAME = "9001"; // used in both devices and groups
    public static final String INSTANCE_ID = "9003"; // In devices: device ID. In groups: list of device IDs
    public static final String HS_ACCESSORY_LINK = "9018";
    public static final String LIGHT = "3311"; // urn:oma:lwm2m:ext:3311 in LwM2M registry
    public static final String TYPE = "5750"; // "Application Type" in LwM2M registry
    public static final String ONOFF = "5850"; // "On/Off" in LwM2M registry
    public static final String DIMMER = "5851"; // "Dimmer" in LwM2M registry
    public static final String TRANSITION_TIME = "5712"; // not contained in LwM2M registry

    // Color / Temperature related, these are independent of brightness, i.e. do not change if brightness does
    public static final String COLOR = "5706";
    public static final String COLOR_X = "5709";
    public static final String COLOR_Y = "5710";
    public static final String COLOR_COLD = "f5faf6";
    public static final String COLOR_COLD_X = "24930";
    public static final String COLOR_COLD_Y = "24694";
    public static final String COLOR_NORMAL = "f1e0b5";
    public static final String COLOR_NORMAL_X = "30140";
    public static final String COLOR_NORMAL_Y = "26909";
    public static final String COLOR_WARM = "efd275";
    public static final String COLOR_WARM_X = "33135";
    public static final String COLOR_WARM_Y = "27211";

    // Dimmer related
    public static final int DIMMER_MIN = 0;
    public static final int DIMMER_MAX = 254;
}
