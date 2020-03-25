package com.app.sms.retrieval.utils;

import android.util.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SystemCipherSecurity {
    private final static String ALGORITHM = "AES";
    private final static String HEX = "0123456789ABCDEF";
    private final static String KEY = "THAILAND2014+DOLPHINSMILE*BLUESKY;+";

    public static String cipher(String data) throws Exception {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(KEY.toCharArray(), KEY.getBytes(), 128, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String base64 = new String(Base64.encodeToString(toHex(cipher.doFinal(data.getBytes())).getBytes(), 0));

        return base64;

    }
    public static String decipher(String data) throws Exception {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(KEY.toCharArray(), KEY.getBytes(), 128, 128);
        String base64String = new String(Base64.decode(data,0));
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return new String(cipher.doFinal(toByte(base64String)));
    }
    private static byte[] toByte(String hexString) {

        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)

            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();

        return result;
    }
    public static String toHex(byte[] stringBytes) {

        StringBuffer result = new StringBuffer(2*stringBytes.length);
        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i]>>4)&0x0f)).append(HEX.charAt(stringBytes[i]&0x0f));
        }

        return result.toString();
    }
    public static void main(String[] args) throws Exception {

        String text = "999999";
        String crypted 	= SystemCipherSecurity.cipher(text);
        String decrypted = SystemCipherSecurity.decipher(crypted);
        System.out.println("CRYPTO-TEST plain: " 		+ text);
        System.out.println("CRYPTO-TEST crypted: " 	+ crypted);
        System.out.println("CRYPTO-TEST decrypted: " 	+ decrypted);

    }
}
