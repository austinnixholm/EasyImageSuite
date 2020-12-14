package com.eis.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Useful encryption/decryption functions.
 *
 * @author Austin Nixholm
 */
public final class EncryptionFunctions {

    public static String encrypt(String src, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(key), makeIv(iv));
            return Base64.getEncoder().encodeToString(cipher.doFinal(src.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String src, String key, String iv) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(key), makeIv(iv));
            decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(src)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return decrypted;
    }

    static AlgorithmParameterSpec makeIv(String iv) {
        return new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a {@code Key}
     * @param desiredKey
     * @return
     */
    static Key makeKey(String desiredKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] key = md.digest(desiredKey.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
