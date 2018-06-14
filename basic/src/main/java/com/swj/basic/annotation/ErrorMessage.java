package com.swj.basic.annotation;

import java.lang.annotation.*;

/**
 * 验证失败信息注解
 * @author  liuhf
 * @since  2018/3/24 10:03
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ErrorMessage {

    String errorMessage() default "";
}
