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
package com.adr.helloiot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import kotlin.text.Charsets;

public class HTTPUtils {

    public static String execGET(String address) throws IOException {

        URL url = new URL(address);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000 /* milliseconds */);
        connection.setConnectTimeout(15000 /* milliseconds */);
        connection.setRequestMethod("GET");
        connection.setAllowUserInteraction(false);
        connection.setUseCaches(false);

        int responsecode = connection.getResponseCode();
        if (responsecode == HttpURLConnection.HTTP_OK) {
            StringBuilder text = new StringBuilder();

            try (BufferedReader readerin = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
                String line;
                while ((line = readerin.readLine()) != null) {
                    text.append(line);
                    text.append(System.getProperty("line.separator"));
                }
            }
            return text.toString();
        } else {
            throw new IOException(MessageFormat.format("HTTP response error: {0}. {1}", Integer.toString(responsecode), connection.getResponseMessage()));
        }
    }
}
