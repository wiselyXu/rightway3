package com.swj.basic.helper;

/**
 * 数组工具类
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/7 9:56
 **/
public class ArrayHelper {

    /**
     * 判断数组为空，为空返回true
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组不为空，不为空返回true
     */
    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组是否包含某个值
     * @param array  数组
     * @param target 包含的值
     * @return true：包含；false：不包含
     */
    public static boolean contains(int[] array, int target) {
        for (int value : array) {
            if (value == target) return true;
        }
        return false;
    }

}
