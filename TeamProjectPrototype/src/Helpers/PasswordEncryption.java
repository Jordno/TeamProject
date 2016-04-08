/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Wolf
 */
public class PasswordEncryption {
    private Key aesKey;
    private Cipher cipher;
    private final String key = "GratisGarageSys1"; // 128-bit key used to encrypt
    public PasswordEncryption() {
        try {
            aesKey = new SecretKeySpec(key.getBytes(), "AES");
            cipher = Cipher.getInstance("AES");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /*
     you must do it you should use an encoding that has a 1-to-1 mapping between bytes and characters, 
    that is, where every byte sequence can be mapped to a unique sequence of characters, and back.
    One such encoding is ISO-8859-1*/
//    public String encrypt(String password){
//        String encryptedString = null;
//        try {
//            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
//            byte[] encrypted = cipher.doFinal(password.getBytes("ISO-8859-1"));
//            encryptedString = new String(encrypted, "ISO-8859-1");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return encryptedString;
//    }
//    
//    public String decrypt(String password){
//        String decrypted = null;
//        try {
//            byte[] encrypted = password.getBytes("ISO-8859-1");
//            cipher.init(Cipher.DECRYPT_MODE, aesKey);
//            decrypted = new String(cipher.doFinal(encrypted));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return decrypted;
//    }
    
    public byte[] encrypt(String password){
        byte[] encrypted = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            encrypted = cipher.doFinal(password.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encrypted;
    }
    
    public String decrypt(byte[] encrypted){
        String decrypted = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decrypted = new String(cipher.doFinal(encrypted));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decrypted;
    }
}
