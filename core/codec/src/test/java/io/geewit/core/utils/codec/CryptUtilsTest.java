package io.geewit.core.utils.codec;

import org.junit.jupiter.api.Test;

public class CryptUtilsTest {

    private final static String username = "admin";
    private final static String password = "Ab123456";
    String encrypt = "A8B06AC0BFBCF3438529492A309A5FE0";

    @Test
    public void encrypt() {
        // 加密
        System.out.println("加密前：" + username);
        String encryptResult = CryptUtils.encrypt(username, password);
        System.out.println("加密后：" + encryptResult);
    }


    @Test
    public void decrypt() {

        String decryptResult = CryptUtils.decrypt(encrypt, password);
        System.out.println("解密后：" + decryptResult);
        // 加密
        System.out.println("加密前：" + username);
        String encryptResult = CryptUtils.encrypt(username, password);
        System.out.println("加密后：" + encryptResult);
    }

}
