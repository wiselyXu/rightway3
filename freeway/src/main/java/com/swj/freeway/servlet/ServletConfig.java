package com.swj.freeway.servlet;

import lombok.Getter;
import lombok.Setter;

/**
 * Servlet配置类
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/28 14:37
 **/
@Getter
@Setter
public class ServletConfig {

    /** api包路径 */
    private String apiPackage;

    /** 应用名称 */
    private String appName;

}
