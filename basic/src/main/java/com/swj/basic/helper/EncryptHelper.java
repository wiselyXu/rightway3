package com.swj.basic.helper;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

/**
 * 加密/解密帮助类
 *
 * @author  Chenjw
 * @since  2018/3/9
 **/
public class EncryptHelper {

    /**
     * 对字符串进行BASE64编码
     * @param sourceStr 源字符串
     * @return 编码后的字符串
     */
    public static String base64Encrypt(String sourceStr){
        if(StringHelper.isNullOrEmpty(sourceStr)){
            return null;
        }
        byte[] temp = sourceStr.getBytes();
        return Base64.getEncoder().encodeToString(temp);//tnew BASE64Encoder().encode(temp);
    }

    /**
     * 对字符串进行BASE64解码
     * @param sourceStr 已编码的字符串
     * @return 解码后的原文
     */
    public static String base64Decrypt(String sourceStr){
        if(StringHelper.isNullOrEmpty(sourceStr)){
            return null;
        }
        return new String(Base64.getDecoder().decode(sourceStr));
    }

    /**
     * 对字符串进行SHA1加密
     * @param sourceStr 源字符串
     * @return 加密后的密文
     */
    public static String sha1(String sourceStr){

        return encrypt("SHA1",sourceStr);
    }

    /**
     * 对字符串进行MD5加密
     * @param sourceStr 源字符串
     * @return 加密后的密文
     */
    public static String md5(String sourceStr){

        return encrypt("MD5",sourceStr);
    }

    /**
     * 对字符串进行指定加密方式的加密
     * @param encryptType 源字符串
     * @param sourceStr 加密方式，如SHA1,MD5等
     * @return 加密后的密文
     */
    public static String encrypt(String encryptType,String sourceStr){
            if(StringHelper.isNullOrEmpty(sourceStr)){
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(encryptType);
            digest.update(sourceStr.getBytes());
            byte[] temp = digest.digest();
            return byteToHexString(temp);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getAESKey(String key,int len)
    {
        if(StringHelper.isNullOrEmpty(key))
        {
            throw new IllegalArgumentException("key is required!");
        }
        byte[] keyByte;
        if(key.length()>len)
        {
            keyByte = key.substring(0,len-1).getBytes();
        }
        else
        {
            keyByte=new byte[len];
            byte[] temp = key.getBytes();
            System.arraycopy(temp, 0, keyByte, 0, temp.length);
        }
        return keyByte;
    }

    /**
     * 对字符串进行AES/ECB/PKCS7Padding加密
     * @param sourceStr 源字符串
     * @param key 密钥
     * @return 加密后的密文
     */
    public static String aesEncrypt(String sourceStr,String key){
        try {
            byte[] keyByte = getAESKey(key,16);
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher=Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
            SecretKeySpec keySpec=new SecretKeySpec(keyByte,"AES");
            cipher.init(Cipher.ENCRYPT_MODE,keySpec);
            byte[] temp = cipher.doFinal(sourceStr.getBytes());
            return new BASE64Encoder().encode(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对字符串进行AES/ECB/PKCS7Padding解密
     * @param sourceStr 已加密字符串
     * @param key 密钥
     * @return 原文
     */
    public static String aesDecrypt(String sourceStr,String key){
        try {
            byte[] keyByte = getAESKey(key,16);
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher=Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
            SecretKeySpec keySpec=new SecretKeySpec(keyByte,"AES");
            cipher.init(Cipher.DECRYPT_MODE,keySpec);
            byte[] temp = cipher.doFinal(new BASE64Decoder().decodeBuffer(sourceStr));
            return new String(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对字符串进行des加密
     * @param sourceStr 待加密字符串
     * @param key 密钥
     * @return 密文
     */
    public static  String  desEncrypt(String sourceStr, String key) {
        try{
            //SecureRandom random = new SecureRandom();
            byte[] keyByte = getAESKey(key,8);
            DESKeySpec desKey = new DESKeySpec(keyByte);//key.getBytes()
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretkey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(keyByte);//key.getBytes()
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, iv);
            byte[] temp = cipher.doFinal(sourceStr.getBytes());
            return new BASE64Encoder().encode(temp);
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对字符串进行des解密
     * @param sourceStr 已加密字符串
     * @param key 密钥
     * @return 原文
     */
    public static String desDecrypt(String sourceStr,String key){
        try {
            //SecureRandom random = new SecureRandom();
            byte[] keyByte = getAESKey(key,8);
            DESKeySpec desKey = new DESKeySpec(keyByte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretkey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(keyByte);
            cipher.init(Cipher.DECRYPT_MODE, secretkey, iv);
            byte[] temp = cipher.doFinal(new BASE64Decoder().decodeBuffer(sourceStr));
            return new String(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * byte转hex
     * @param src
     * @return
     */
    public static String byteToHexString(byte[] src){
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(src.length*2);
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
