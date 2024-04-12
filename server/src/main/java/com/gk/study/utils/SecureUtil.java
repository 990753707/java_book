package com.gk.study.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 提供常用的加解密功能
 * 目前支持：md5
 */
public class SecureUtil {

    public static String md5(String message,String salt) throws NoSuchAlgorithmException {
        salt = salt == null?"NULL":salt;
        message = salt+"D"+message+"D"+salt;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArr = md5.digest(message.getBytes(StandardCharsets.UTF_8));
        return byte2hex(byteArr);
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs= new StringBuilder();
        String stmp="";
        for (byte value : b) {
            //为了保证二进制机器数不变，这里需要& 0XFF
            stmp = (Integer.toHexString(value & 0XFF));
            //如果只有一位，需要在前面补上0凑足两位
            if(stmp.length() == 1) {
                hs.append("0").append(stmp);
            }else {
                hs.append(stmp);
            }
        }
        return hs.toString();
    }
}
