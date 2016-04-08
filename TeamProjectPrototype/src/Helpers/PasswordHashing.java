/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wolf
 */
public class PasswordHashing {

    public PasswordHashing() {
    }
    
    public byte[] hashPass(String pass){
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(pass.getBytes());
            hash = digest.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordHashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hash;
    }
    
}
