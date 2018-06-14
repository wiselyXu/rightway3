package com.swj.freeway.rpc.proxy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

/**
 * com.swj.freeway.spring
 *
 * @Description
 * @Author 余焕【yuh@3vjia.com】
 * @Datetime 2018/5/12 15:30
 **/
@Slf4j
@Getter
@Setter
public class ProxyBeanFactory<T> implements FactoryBean<T> {

    private Class<T> proxyClass;

    @Override
    public T getObject() throws Exception {
        return proxyClass.newInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return proxyClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
