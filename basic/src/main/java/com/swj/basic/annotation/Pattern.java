package com.swj.basic.annotation;

import java.lang.annotation.*;

/**
 * 正则表达式注解
 * @author  余焕【yuh@3vjia.com】
 * @since  2018/4/24 10:03
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Pattern {

    String errorMessage() default "";

    String regexp();

}
