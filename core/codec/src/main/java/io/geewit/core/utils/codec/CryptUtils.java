package io.geewit.core.utils.codec;

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
     * @param value  未加密的字符串
     * @param strKey 秘钥
     * @return 加密的字符串
     */
    public static String encrypt(String value, String strKey) {
        try {
            byte[] keyBytes = Arrays.copyOf(strKey.getBytes(StandardCharsets.US_ASCII), 16);

            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] cleartext = value.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertextBytes = cipher.doFinal(cleartext);

            return new String(Hex.encodeHex(ciphertextBytes)).toUpperCase();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     * @param encrypt 加密的字符串
     * @param strKey 秘钥
     * @return 解密的字符串
     */
    public static String decrypt(String encrypt, String strKey) {
        try {
            byte[] keyBytes = Arrays.copyOf(strKey.getBytes(StandardCharsets.US_ASCII), 16);

            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] cleartext = Hex.decodeHex(encrypt.toCharArray());
            byte[] ciphertextBytes = cipher.doFinal(cleartext);
            return new String(ciphertextBytes, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | DecoderException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     * @param encrypt 加密的字符串
     * @return 解密的字符串
     */
    public static String decrypt(String encrypt) {
        return decrypt(encrypt, KEY);
    }

}
