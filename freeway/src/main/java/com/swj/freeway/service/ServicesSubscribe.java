package com.swj.freeway.service;

import com.swj.basic.helper.StringHelper;
import com.swj.freeway.common.ClassUtils;
import com.swj.freeway.rpc.proxy.Proxy;
import com.swj.freeway.rpc.proxy.ProxyBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订阅服务
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/9 19:51
 **/
@Slf4j
public class ServicesSubscribe implements BeanDefinitionRegistryPostProcessor {

    public final static Map<Class, Class> registerBeans = new HashMap<>();

    public static ServicesConfig servicesConfig = new ServicesConfig();

    public ServicesSubscribe() {
        subscribe();
    }

    public ServicesSubscribe(ServicesConfig servicesConfig) {
        ServicesSubscribe.servicesConfig = servicesConfig;
        subscribe();
    }

    public void subscribe() {
        try {
            // 获取订阅服务的接口
            List<Class> apis = ClassUtils.getApiClasses(servicesConfig);
            log.info("订阅服务:{}", apis);

            // 生成代理类
            for (Class api : apis) {
                // 生成服务代理类
                Class clazz = Proxy.getProxy(api);

                // 给Spring初始化并注入到容器
                registerBeans.put(api, clazz);
            }

        } catch (Exception e) {
            log.error("订阅服务失败", e);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (Map.Entry<Class, Class> entry : ServicesSubscribe.registerBeans.entrySet()) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(entry.getKey());
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getPropertyValues().add("proxyClass", entry.getValue());
            definition.setBeanClass(ProxyBeanFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(StringHelper.toLowerCaseFirstOne(entry.getKey().getSimpleName()), definition);
            log.info("bean : {} has inject in spring", entry.getValue().getName());
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

}
