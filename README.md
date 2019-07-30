HelloIoT [![Release](https://jitpack.io/v/adrianromero/helloiot.svg)](https://jitpack.io/#adrianromero/helloiot)
========

HelloIoT is a [MQTT](https://en.wikipedia.org/wiki/MQTT) and [IKEA Tradfri](https://www.ikea.com/gb/en/products/lighting/smart-lighting/) dashboard application. 
You can use HelloIoT as a MQTT and IKEA Tradfri client application to publish and subscribe to topics or you can use HelloIoT as a client platform to create your own dashboard. 
HelloIoT is a Java multiplatform application and it can run on Windows, MacOS, Linux or Android.

![Screenshot](https://i.imgur.com/qgvpxgy.png) 

Getting started
===============

Install and execute HelloIoT
----------------------------

To run HelloIoT just download and uncompress the appropiate release file. The executable is located in image/bin/HelloIoT.

* [helloiot-1.1.020-win.zip](https://github.com/adrianromero/helloiot/releases/download/1.1.020/helloiot-1.1.020-win.zip) Windows
* [helloiot-1.1.020-mac.zip](https://github.com/adrianromero/helloiot/releases/download/1.1.020/helloiot-1.1.020-mac.zip) MacOS
* [helloiot-1.1.020-linux.zip](https://github.com/adrianromero/helloiot/releases/download/1.1.020/helloiot-1.1.020-linux.zip) Linux
* [helloiot-1.1.020.apk](https://github.com/adrianromero/helloiot/releases/download/1.1.020/helloiot-1.1.020.apk) Android

[![Installing HelloIoT](https://img.youtube.com/vi/RuxUUHpTbR0/0.jpg)](https://www.youtube.com/watch?v=RuxUUHpTbR0)

Install a MQTT Broker
----------------------

To run HelloIoT you need to install a MQTT broker or you can use a public MQTT broker. Public MQTT brokers can be good for testing or prototyping purposes.

HelloIoT is developed and tested using [Mosquitto](http://mosquitto.org/) but it will also work 
with other MQTT Brokers like [HiveMQ](http://www.hivemq.com/), [EMQTT](http://emqtt.io/), 
[Moquette](https://github.com/andsel/moquette)...
There is a list of MQTT brokers in the [MQTT Community Wiki](https://github.com/mqtt/mqtt.github.io/wiki/servers), you can choose the broker that better fits your needs.

Execute HelloIoT from sources
-----------------------------

If you prefer to run HelloIoT from sources clone the repository, build and execute. You will need Java 11 installed first.
 
```
git clone https://github.com/adrianromero/helloiot
cd ./helloiot
./gradlew run
```

Other MQTT tools
================

In the [MQTT Community Wiki](https://github.com/mqtt/mqtt.github.io/wiki/tools), you can find a list of client applications and tools. 
My favorites are:

* [MQTT.fx](http://mqttfx.org/) by Jens Deters and created with JavaFX.
* [mqtt-spy](http://kamilfb.github.io/mqtt-spy/) by Kamil Baczkowicz and also created with JavaFX.

Acknowledgments
===============

* Font Awesome 4.4.0 by @davegandy - http://fontawesome.io - @fontawesome License - http://fontawesome.io/license (Font: SIL OFL 1.1, CSS: MIT License)
* Roboto font https://www.google.com/fonts/specimen/Roboto (Apache License Version 2.0)
* Sound clips from http://www.soundjay.com/
* Medusa JavaFX library for Gauges by Gerrit Grunwald. https://github.com/HanSolo/Medusa (Apache License Version 2.0)
* Eclipse Paho MQTT Java library. https://www.eclipse.org/paho/clients/java/ (Eclipse Public License 1.0)
* Google Guice library. https://github.com/google/guice (Apache License Version 2.0)
* JideSoft Common library. https://github.com/jidesoft/jidefx-oss  (GPL version 2 with classpath exception)
* Apache Commons IO. https://commons.apache.org/proper/commons-io/ (Apache License Version 2.0)
* MQTT Bridge for IKEA Tradfri Light Gateway by Ben Hardill https://github.com/hardillb/TRADFRI2MQTT (Apache License Version 2.0)

License
=======

HelloIoT is licensed under the GNU General Public License, Version 3, 29 June 2007
