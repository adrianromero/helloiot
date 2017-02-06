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

package com.adr.helloiot.media;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author adrian
 */
public class CommandClip implements Clip {
    
    private final static Logger logger = Logger.getLogger(CommandClip.class.getName());    
    private final static File CACHEFOLDER = new File(System.getProperty("java.io.tmpdir"), "CLIPCACHE");
    
    private String[] clipcommand;
    
    CommandClip(String[] mpgcommand, String url) {

        try {
            clipcommand = new String[mpgcommand.length + 1];
            System.arraycopy(mpgcommand, 0, clipcommand, 0, mpgcommand.length);   
            clipcommand[clipcommand.length -1] = getCachedMp3File(url);
        } catch (IOException | NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, null, ex);
            clipcommand = null;
        }
    }

    @Override
    public void play() {
        try {
            Runtime.getRuntime().exec(clipcommand);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void stop() {
    }
    
    private static String getCachedMp3File(String url) throws IOException, NoSuchAlgorithmException {

        if (url == null) {
            throw new IOException("Null url");
        } else if (!"http:".equalsIgnoreCase(url.substring(0, 5)) 
                && !"https:".equalsIgnoreCase(url.substring(0, 6)) 
                && !"ftp:".equalsIgnoreCase(url.substring(0, 4)) 
                && !"jar:".equalsIgnoreCase(url.substring(0, 4))) {
            // a local file
            return url;
        } else {
            // a remote file
            FileUtils.forceMkdir(CACHEFOLDER);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(url.getBytes(StandardCharsets.UTF_8));
            String cachefilename = Base64.getUrlEncoder().encodeToString(md.digest());
            int iext = url.lastIndexOf('.');
            String extension = iext >= 0 ? url.substring(iext) : "";
                    
            File cachefile = new File(CACHEFOLDER, cachefilename + extension);

            if (cachefile.exists()) {
                return cachefile.getPath();
            } else {
                try (InputStream input = new URL(url).openStream();
                        OutputStream output = new FileOutputStream(cachefile)) {
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = input.read(buffer)) != -1) {
                        output.write(buffer, 0, n);
                    }
                }
                return cachefile.getPath();
            }
        }
    }
}
