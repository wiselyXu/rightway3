package com.swj.basic.annotation;


import java.lang.annotation.*;


/**
 * 整型范围验证注解
 * @author  liuhf
 * @since  2018/3/24 10:03
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ErrorMessage
public @interface Range {

    int min() default Integer.MIN_VALUE;

    int max() default Integer.MAX_VALUE;
}
