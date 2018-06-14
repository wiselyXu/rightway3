package com.swj.basic.helper;


import java.util.List;

/**
 *  集合帮助类
 * @author  刘海峰【liuhf@3vjia.com】
 * @since  2018/4/28-14:28
 **/
public final class ListHelper {

    /**
     * list判空
     *
     * @param list 待判断的list
     * @author 刘海峰
     * @since 2018/4/28-14:28
     **/
    public static boolean isNullOrEmpty(List list)
    {
        return list==null || list.size()==0;
    }

    /**
     * 取list第一个元素
     *
     * @param list 待操作的list
     * @author 刘海峰
     * @since 2018/4/28-14:28
     **/
    public static <T> T first(List<T> list)
    {
        if(!isNullOrEmpty(list))
        {
            return list.get(0);
        }
        return null;
    }
}
