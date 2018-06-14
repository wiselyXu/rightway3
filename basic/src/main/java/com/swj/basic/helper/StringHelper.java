package com.swj.basic.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * StringHelper
 *
 * 字符串转换帮助类
 * @author 刘海峰【liuhf@3vjia.com】
 * @since  2018/3/9-11:28
 **/
public class StringHelper {


    /**
     * 判断字符串是否为空
     *
     * @param source 待判断的字符串
     * @author 刘海峰
     * @since 2018/3/26
     **/
    public static boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty() || source.equals("") || source.trim().equals("");
    }

    /**
     * 判断对象是否为空字符串
     *
     * @param source 待判断的Object
     * @author 刘海峰
     * @since 2018/3/26
     **/
    public static boolean isObjectNullOrEmpty(Object source) {
        return source == null || source.toString().equals("");
    }

    /**
     * 判断两个字符串是否相等（忽略大小写）
     *
     * @param source 待比较的source
     * @param target 待比较的target
     * @author 刘海峰
     * @since 2018/3/26
     **/
    public static boolean ignoreCaseEquals(String source, String target) {
        if (source != null && target != null) {
            return source.toLowerCase().equals(target.toLowerCase());
        }
        return isNullOrEmpty(source) && isNullOrEmpty(target);
    }

    /**
     * 判断指定的字符串是 null、空还是仅由空白字符组成
     * @author 余焕
     * @since 2018/3/26
     **/
    public static boolean isNullOrWhiteSpace(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 首字母转小写
     * @author chenjw
     * @since 2018/3/26
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) return s;
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * 替换忽略大小写
     */
    public static String replaceIgnoreCase(String source, String target, String replacement) {
        if (isNullOrWhiteSpace(source) || isNullOrWhiteSpace(target) || isNullOrWhiteSpace(replacement)) {
            return source;
        }

        return source.toUpperCase().replace(target.toUpperCase(), replacement);
    }

    /**
     *  解压zlib 压缩后的字符串
     * @author chenjw
     * @since 2018/3/26
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        decompresser.end();
        return output;
    }

    /**
     * 以zlib的格式压缩字符串，输出成byte数组
     * @author chenjw
     * @since 2018/3/26
     */
    public static byte[] compress(String str) {

        byte[] data = str.getBytes();

        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }
}
