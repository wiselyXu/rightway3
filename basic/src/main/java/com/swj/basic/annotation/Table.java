package com.swj.basic.annotation;

import java.lang.annotation.*;

/**
 * 标识表名称
 * @author  Chenjw
 * @since  2017/11/24
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String value(); //表名称
}
