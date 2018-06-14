package com.swj.basic.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON帮助类
 *
 * @author  刘海峰【liuhf@3vjia.com】
 * @since  2018/3/9-11:28
 **/
public class JsonHelper {
    /**
     * 用fastjson 将json字符串解析为一个 JavaBean
     *
     * @param jsonString jsonString
     * @param cls type
     * @return T
     */
    public static <T> T parseBean(String jsonString, Class<T> cls)
    {
        if(StringHelper.isNullOrEmpty(jsonString))
        {
            return null;
        }
        return JSON.parseObject(jsonString, cls);
    }

    /**
     * 用fastjson 将json字符串解析为一个 JavaBean
     *
     * @param jsonString jsonString
     * @param type       type
     * @return Object
     */
    public static Object parseBean(String jsonString,Type type)
    {
        return JSON.parseObject(jsonString,type);
    }

    /**
     * 用fastjson 将json字符串 解析成为一个 List<JavaBean> 及 List<String>
     *
     * @param jsonString jsonString
     * @param cls        type
     * @return List<T>
     */
    public static <T> List<T> parseBeanArray(String jsonString, Class<T> cls)
    {
         return JSON.parseArray(jsonString, cls);
    }

    /**
     * 用fastjson 将jsonString 解析成 List<Map<String,Object>>
     *
     * @param jsonString jsonString
     * @return list<map>
     */
    public static List<Map<String, Object>> parseListMap(String jsonString)
    {
        return JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * 用fastjson 将Object 转化为jsonString
     *
     * @param object object
     * @return string
     */
    public static String toJsonString(Object object)
    {
        return JSON.toJSONString(object);
    }
}
