package com.swj.basic.annotation;


import java.lang.annotation.*;

/**
 * 长度验证注解
 * @author  liuhf
 * @since  2018/3/24 10:03
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ErrorMessage
public @interface Length {
    int value() default Integer.MAX_VALUE;
}
