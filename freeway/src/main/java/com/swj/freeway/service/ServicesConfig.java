package com.swj.freeway.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ZK配置类
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/9 19:52
 **/
@Getter
@Setter
public class ServicesConfig {

    // 默认扫描包名
    private String scanPackage = "com.swj.api";

    // zk服务器列表
    private String zkUrl;

    // k8s地址
    private String k8sUrl;

    // 需要注册/订阅的服务，为空默认扫描scanPackage包下的所有api
    private List<Class> apis;

}
