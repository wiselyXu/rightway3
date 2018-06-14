package com.swj.basic.helper;


import com.swj.basic.TableColumn;
import com.swj.basic.TypeColumn;
import com.swj.basic.TypeColumnMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象帮助类
 *
 * @author liuhf
 * @since 2017/11/28-9:14
 **/
public class ObjectHelper {

    private final static Logger logger= LoggerFactory.getLogger(ObjectHelper.class);


    /**
     * 把对象的属性转成Map
     * @param value 待转换的对象
     * @author liuhf
     */
    public static Map<String, Object> toMap(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof HashMap) {
            return (HashMap<String, Object>) value;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(value.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map<String, Object> result = new HashMap<String, Object>(propertyDescriptors.length);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String key = propertyDescriptor.getName();
                if (!key.equals("class")) {
                    Method getter = propertyDescriptor.getReadMethod();
                    Object object = getter.invoke(value);
                    result.put(key, object);
                }
            }
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    /**
     * 把Map转换成指定类型的对象
     * @param map   map
     * @param t     类型
     * @author liuhf
     * @return T实例
     */
    public static  <T> T toEntity(Map<String,Object> map,Class<T> t) {
        if(map==null || map.isEmpty())
        {
            return  null;
        }
        try {
            T value = t.newInstance();
            TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(t);
            List<TableColumn> columns = typeColumnMapping.getColumns();
            String fieldName;
            for (TableColumn column : columns) {
                Field field = column.getField();
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                fieldName = column.getName().toLowerCase();
                if (!map.containsKey(fieldName)) {
                    continue;
                }
                Object temp = convertToType(field.getType(), map.get(fieldName));
                if (temp == null) {
                    continue;
                }
                field.set(value, temp);
            }
            return value;
       }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验时间格式
     * @author zenglj
     * @since  2018/05/29
     */

    public static String getDateType(String date){
        String type="yyyy-MM-dd HH:mm:ss";
        int result = date.trim().indexOf(":");
        if(result<0){
            type="yyyy-MM-dd";
        }
        return type;
    }

    /**
     *  转换对象为指定类型
     * @param type      目标类型
     * @param source    待转换的对象
     * @author liuhf
     * @since 2018/3/31
     **/
    public static Object convertToType(Class<?> type, Object source) throws ParseException {
        if (!StringHelper.isObjectNullOrEmpty(source)) {
            String typeName = type.getName();
            String temp=source.toString().trim();
            if(!temp.equals("")){
                switch (typeName) {
                    case "java.lang.String":
                        return source.toString();
                    case "java.util.Date":
                        if(source.getClass().getName().equals("java.sql.Timestamp"))
                        {
                            return new Date(((Timestamp)source).getTime());
                        }
                        else {
                            String dateType = getDateType(temp);
                            return new SimpleDateFormat(dateType).parse(temp);
                        }
                    case "java.sql.Date":
                        if(source.getClass().getName().equals("java.sql.Timestamp"))
                        {
                            return new java.sql.Date(((Timestamp)source).getTime());
                        }
                        else {
                            return java.sql.Date.valueOf(temp);
                        }
                    case "java.sql.Timestamp":
                        if(source.getClass().getName().equals("java.sql.Timestamp"))
                        {
                            return source;
                        }
                        else {
                            return Timestamp.valueOf(temp);
                        }
                    case "byte":
                    case "java.lang.Byte":
                        return Byte.parseByte(temp);
                    case "short":
                    case "java.lang.Short":
                        return Short.parseShort(temp);
                    case "int":
                    case "java.lang.Integer":
                        return Integer.parseInt(temp);
                    case "long":
                    case "java.lang.Long":
                        return Long.parseLong(temp);
                    case "double":
                    case "java.lang.Double":
                        return Double.parseDouble(temp);
                    case "float":
                    case "java.lang.Float":
                        return Float.parseFloat(temp);
                    case "boolean":
                    case "java.lang.Boolean":
                        return Boolean.parseBoolean(temp);
                }
            }

        }
        return source;
    }


    /**
     *  获取参数
     * @author liuhf
     * @since 2018/3/31
     **/
    public static Map<String, Object> getParameter(Object... arg) {
        if (arg == null || arg.length == 0 || arg.length % 2 != 0) {
            logger.error("argument not valid");
            return null;
        }
        Map<String, Object> param = new HashMap<String, Object>();
        for (int i = 0; i < arg.length; i += 2) {
            param.put(arg[i].toString(), arg[i + 1]);
        }
        return param;
    }

    /**
     *  增加参数
     * @author liuhf
     * @since 2018/3/31
     **/
    public static Map<String, Object> addParameter(Map<String,Object> param,Object... arg) {
        if (param==null || param.size()==0 || arg == null || arg.length == 0 || arg.length % 2 != 0) {
            logger.error("argument not valid");
            return param;
        }
        for (int i = 0; i < arg.length; i += 2) {
            param.put(arg[i].toString(), arg[i + 1]);
        }
        return param;
    }

}
