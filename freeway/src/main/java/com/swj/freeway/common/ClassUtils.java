package com.swj.freeway.common;

import com.swj.basic.annotation.FreewayPath;
import com.swj.basic.helper.ListHelper;
import com.swj.freeway.service.ServicesConfig;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;

import java.util.List;

/**
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/19 9:59
 **/
public class ClassUtils {

    public static List<Class> getApiClasses(ServicesConfig servicesConfig) {
        List<Class> apis;
        // 自定义注册/订阅服务
        if (!ListHelper.isNullOrEmpty(servicesConfig.getApis())) {
            apis = servicesConfig.getApis();
        }
        // 未自定义注册/订阅服务时，扫描包下带有@Path注解的api，默认扫描包为com.swj.api
        else {
            ClassScanner classScanner = new ClassScanner();
            TypeFilter typeFilter = (metadataReader, metadataReaderFactory) -> {
                String pathAnnotation = FreewayPath.class.getName();
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                return annotationMetadata.getAnnotationTypes().contains(pathAnnotation);
            };
            classScanner.addIncludeFilter(typeFilter);
            apis = classScanner.doScan(servicesConfig.getScanPackage());
        }
        return apis;
    }

}
