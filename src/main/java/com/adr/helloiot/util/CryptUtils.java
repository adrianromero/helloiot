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

package com.adr.helloiot.util;

import com.google.common.base.Strings;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 *
 * @author adrian
 */
public class CryptUtils {
    
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[32];
        random.nextBytes(salt);     
        return salt;
    }
    
    public static String hashsaltPassword(String input, byte[] salt) {
        try {
            // generate hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);
            byte[] hashedBytes = digest.digest(input.getBytes("UTF-8"));

            return "password:" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static boolean validatePassword(String password, String hashsalt) {
        
        if (Strings.isNullOrEmpty(password) && Strings.isNullOrEmpty(hashsalt)) {
            return true; // empty password. 
        } else if (Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(hashsalt)) {
            return false; // password not empty but hashsalt empty or vice
        } else {
            String [] splitted = hashsalt.split(":");
            
            if (splitted.length != 3 || !"password".equals(splitted[0])) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(splitted[1]);
                
            return hashsaltPassword(password, salt).equals(hashsalt);
        }
    }
}