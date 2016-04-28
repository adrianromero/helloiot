HelloIot [![Release](https://jitpack.io/v/adrianromero/helloiot.svg)](https://jitpack.io/#adrianromero/helloiot)
========

HelloIot is a different [MQTT](https://en.wikipedia.org/wiki/MQTT) JavaFX application. 
With HelloIot you can publish and subscribe to topics using a collection of widgets available to create your own dashboard. 

![Screenshot](http://i.imgur.com/Uv3mF63.gif)

Starting
========

Install a MQTT Broker
----------------------

HelloIot is developed and tested using [Mosquitto](http://mosquitto.org/) but it will also work 
with other MQTT Brokers like [HiveMQ](http://www.hivemq.com/), [EMQTT](http://emqtt.io/), 
[Moquette](https://github.com/andsel/moquette)...
There is a list of MQTT brokers in the [MQTT Community Wiki](https://github.com/mqtt/mqtt.github.io/wiki/servers), you can choose the broker that better fits your needs.

Install Java
------------

You need to install the [Java SE 8 JDK](http://www.oracle.com/technetwork/es/java/javase/downloads/index.html) or later version, not the JRE, 
or the [OpenJDK 8](http://openjdk.java.net/install/) or later.

Execute HelloIot
----------------

Download, build and run HelloIot. By default HelloIot is configured to be connected to a MQTT broker installed locally, listening on port 1883, with no security configured. 
The standard installation of the Mosquitto broker will work.
 
```
git clone https://github.com/adrianromero/helloiot
cd ./helloiot
./gradlew run
```

Configure HelloIot
==================

If you want to connect to a different MQTT broker or create your own MQTT dashboard ou need to edit the file *helloiot.properties*.

Other MQTT tools
================

In the [MQTT Community Wiki](https://github.com/mqtt/mqtt.github.io/wiki/tools), you can find a list of client applications and tools. 
My favorites are:

* [MQTT.fx](http://mqttfx.jfx4ee.org/) by Jens Deters and created with JavaFX.
* [mqtt-spy](http://kamilfb.github.io/mqtt-spy/) by Kamil Baczkowicz and also created with JavaFX.

Acknowledgments
===============

* Font Awesome 4.4.0 by @davegandy - http://fontawesome.io - @fontawesome License - http://fontawesome.io/license (Font: SIL OFL 1.1, CSS: MIT License)
* Sound clips from http://www.soundjay.com/
* Medusa JavaFX library for Gauges by Gerrit Grunwald. https://github.com/HanSolo/Medusa (Apache License Version 2.0)
* Eclipse Paho MQTT Java library. https://www.eclipse.org/paho/clients/java/ (Eclipse Public License 1.0)
* Google Guice library. https://github.com/google/guice (Apache License Version 2.0)
* JideSoft Common library. https://github.com/jidesoft/jidefx-oss  (GPL version 2 with classpath exception)
* Apache Commons IO. https://commons.apache.org/proper/commons-io/ (Apache License Version 2.0)
* MapDB Database engine. http://www.mapdb.org/ (Apache License Version 2.0)

License
=======

HelloIot is licensed under the GNU General Public License, Version 3, 29 June 2007
