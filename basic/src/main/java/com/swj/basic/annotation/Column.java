package com.swj.basic.annotation;

import java.lang.annotation.*;

/**
 * 用于pojo上，标志字段的具有什么属性，根据这个生成对应的sql语句
 * @author  Chenjw
 * @since  2017/11/24
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    String name() default "";//数据库字段名字

    boolean isUpdate() default true ; //默认是更新字段

    boolean isInsert() default true ;//默认是插入字段

    boolean isSelect() default true ;//默认是查询字段

    boolean isDataKey() default true ;//默认是数据库字段

    boolean isPK() default false ;//默认不是主键

    boolean isAutoIncrement() default false;//默认不是自增列
}
