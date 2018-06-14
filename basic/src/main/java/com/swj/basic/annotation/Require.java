package com.swj.basic.annotation;


import java.lang.annotation.*;


/**
 * 字段非空验证注解
 * @author  liuhf
 * @since  2018/3/24 10:03
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ErrorMessage
public @interface Require {

    String errorMessage() default "";

}
