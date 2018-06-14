package com.swj.basic.annotation;

import java.lang.annotation.*;

/**
 * com.swj.freeway.config.annotation
 *
 * @Description 服务路径注解 用于API接口
 * @Author 余焕【yuh@3vjia.com】
 * @Datetime 2018/5/9 17:30
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FreewayPath {
    String value() default "";
}
