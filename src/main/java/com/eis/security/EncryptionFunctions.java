package com.eis.security;

import com.eis.models.AlgorithmType;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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
     *
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

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link String}
     */
    public static String toEncodedBase64String(byte[] imageByteArray) {
        return Base64.getEncoder().encodeToString(imageByteArray);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link String}
     * @return byte array
     */
    public static byte[] toDecodedBase64String(String imageDataString) {
        return Base64.getDecoder().decode(imageDataString);
    }

    /**
     * Generates a String representation of an encryption key based on
     * the algorithm type passed.
     *
     * @param type the type of algorithm
     * @return the generated encryption key
     */
    public static String generateKey(AlgorithmType type) {
        switch (type) {
            default:
            case AES_256_CBC:
                return generateAES256Key();
        }
    }

    /**
     * Generates a String representation of an encryption initialization vector
     * based on the algorithm type passed.
     *
     * @param type the type of algorithm
     * @return the generated initialization vector.
     */
    public static String generateIV(AlgorithmType type) {
        switch (type) {
            default:
            case AES_256_CBC:
                return generate16ByteIV();
        }
    }

    public static String generateAES256Key() {
        KeyGenerator keyGen;
        SecretKey secretKey = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey == null ? "" : new String(secretKey.getEncoded());
    }

    public static String generate16ByteIV() {
        byte[] iv = new byte[16];
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(iv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return new String(iv);
    }
}
