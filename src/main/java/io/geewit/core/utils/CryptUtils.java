package io.geewit.core.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * 加密工具类
 * @author geewit
 * @since  2016/4/17
 */
@SuppressWarnings({"unused"})
public class CryptUtils {
    private final static Logger logger = LoggerFactory.getLogger(CryptUtils.class);

    private final static String ALGORITHM = "AES";
    private final static String KEY = "key";

    /**
     * 加密
     * @param password
     * @param strKey
     * @return
     */
    public static String encrypt(String password, String strKey) {
        try {
            byte[] keyBytes = Arrays.copyOf(strKey.getBytes(StandardCharsets.US_ASCII), 16);

            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] cleartext = password.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertextBytes = cipher.doFinal(cleartext);

            return new String(Hex.encodeHex(ciphertextBytes)).toUpperCase();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     * @param encryptResult
     * @param strKey
     * @return
     */
    public static String decrypt(String encryptResult, String strKey) {
        try {
            byte[] keyBytes = Arrays.copyOf(strKey.getBytes(StandardCharsets.US_ASCII), 16);

            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] cleartext = Hex.decodeHex(encryptResult.toCharArray());
            byte[] ciphertextBytes = cipher.doFinal(cleartext);
            return new String(ciphertextBytes, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | DecoderException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     * @param encryptResult
     * @return
     */
    public static String decrypt(String encryptResult) {
        return decrypt(encryptResult, KEY);
    }

//    public static void main(String[] args) {
//        String content = "qwe123!@#";
//        String password = "qwe123!@#";
//        // 加密
//        System.out.println("加密前：" + content);
//        String encryptResult = encrypt(content, password);
//        System.out.println("加密后：" + encryptResult);
//        // 解密
//        String decryptResult = decrypt(encryptResult, password);
//        System.out.println("解密后：" + decryptResult);
//
//
//        //SELECT HEX(encrypt("password", "0123456789012345"));
//        //SELECT decrypt(UNHEX("E75C7C56AFB3EA4360A9856456F1C8A2"), "0123456789012345");
//    }
}
