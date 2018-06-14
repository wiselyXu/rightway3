package com.swj.qc;

import com.swj.basic.helper.EncryptHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * com.swj.framework.helper
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class EncryptHelperTest {


    @Test
    public void base64Encrypt() {

        //1，正常字符串
        String word="abcdefg";
        String encodedWord= EncryptHelper.base64Encrypt(word);
        Assert.assertTrue(encodedWord.length()>0 && !encodedWord.equals(word));

        word="中午";
        encodedWord=EncryptHelper.base64Encrypt(word);
        Assert.assertTrue(encodedWord.length()>0 && !encodedWord.equals(word));

        //2，被编码字符为空，或字符串为null，返回null
        word="";
        encodedWord=EncryptHelper.base64Encrypt(word);
        Assert.assertNull(encodedWord);

        word=null;
        encodedWord=EncryptHelper.base64Encrypt(word);
        Assert.assertNull(encodedWord);
    }

    @Test
    public void base64Decrypt() {
        String encodedWord="WVdKalpHVm1adz09";
        String decodedWordResult="";
        String encodedResult=EncryptHelper.base64Encrypt(encodedWord);
        decodedWordResult=EncryptHelper.base64Decrypt(encodedResult);
        Assert.assertEquals(encodedWord,decodedWordResult);

        //2，被解码字符为空，或字符串为null，返回null
        encodedWord="";
        encodedResult=EncryptHelper.base64Decrypt(encodedWord);
        Assert.assertNull(encodedResult);

        encodedWord=null;
        encodedResult=EncryptHelper.base64Decrypt(encodedWord);
        Assert.assertNull(encodedResult);
        //异常情况
    }

    @Test
    public void sha1() {

        String encodeWord=EncryptHelper.sha1("abcdefg");
        System.out.println(encodeWord);
        Assert.assertNotNull(encodeWord);

    }

    @Test
    public void md5() {
        String encodeWord=EncryptHelper.md5("abcdefg");
        System.out.println(encodeWord);
        Assert.assertNotNull(encodeWord);
    }

    @Test
    public void encrypt() {
        String encodeWord=EncryptHelper.encrypt("md5","abcdefg");
        Assert.assertEquals("7ac66c0f148de9519b8bd264312c4d64",encodeWord);

        //加密内容为空 或是  null则返回null
        encodeWord=EncryptHelper.encrypt("md5","");
        Assert.assertNull(encodeWord);

        encodeWord=EncryptHelper.encrypt("md5",null);
        Assert.assertNull(encodeWord);

    }

    @Test
    public void aesEncrypt_aesDecrypt() {//加密—解密
        String source = "特斯塔";
        String result = EncryptHelper.aesEncrypt(source,"key1");//16位
        Assert.assertNotEquals(source,result);
        result = EncryptHelper.aesDecrypt(result,"key1");
        Assert.assertEquals(source,result);

        source = "";
        result = EncryptHelper.aesEncrypt(source, "密钥");
        Assert.assertNotEquals(source,result);
        result = EncryptHelper.aesDecrypt(result,"密钥");
        Assert.assertEquals(source,result);
    }

    @Test
    public void aesEncryptNull()  {
        String source = null;
        try {
            String result = EncryptHelper.aesEncrypt(source,"key16");
        }catch (Exception e) {
            Assert.assertEquals(NullPointerException.class,e.getMessage());
        }
    }

    @Test
    public void aesDecryptNull()  {
        String source = null;
        try {
            String result = EncryptHelper.aesDecrypt(source,"key16");
        }catch (Exception e) {
            Assert.assertEquals(NullPointerException.class,e.getMessage());
        }


    }

    @Test
    public void desEncrypt_desDecrypt() {//加密—解密
        String source = "特斯塔";
        String result = EncryptHelper.desEncrypt(source,"key8");
        Assert.assertNotEquals(source,result);
        result = EncryptHelper.desDecrypt(result,"key8");
        Assert.assertEquals(source,result);

        source = "";
        result = EncryptHelper.desEncrypt(source, "key8");
        Assert.assertNotEquals(source,result);
        result = EncryptHelper.desDecrypt(result,"key8");
        Assert.assertEquals(source,result);
    }

    @Test
    public void desDecryptNull() {//加密
        String source = null;
        try {
            String result = EncryptHelper.desEncrypt(source,"key8");
        }catch (Exception e) {
            Assert.assertEquals(NullPointerException.class,e.getMessage());
        }
    }

    @Test
    public void desEncryptNull() {//加密
        String source = null;
        try {
            String result = EncryptHelper.desDecrypt(source,"key8");
        }catch (Exception e) {
            Assert.assertEquals(NullPointerException.class,e.getMessage());
        }
    }

    @Test
    public void byteToHexString() {
        String result=EncryptHelper.byteToHexString(null);
        Assert.assertNull(result);

    }
}