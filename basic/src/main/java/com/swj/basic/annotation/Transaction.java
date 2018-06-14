package com.swj.basic.annotation;

import java.lang.annotation.*;

/**
 * 标记一个方法是否为纯数据库查询
 * @author  余焕【yuh@3vjia.com】
 * @since   2018/5/4 14:20
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transaction {
}
