package com.swj.freeway.common;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取某个包下的所有类
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/11 11:29
 **/
public class ClassScanner {

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static MetadataReaderFactory   metadataReaderFactory   = new CachingMetadataReaderFactory(resourcePatternResolver);

    // 过滤规则(包含和排除)
    private List<TypeFilter> includeFilters = new LinkedList<>();
    private List<TypeFilter> excludeFilters = new LinkedList<>();

    public List<Class> doScan(String basePackage) {
        List<Class> classes = new ArrayList<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils
                    .resolvePlaceholders(basePackage)) + "/**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    if ((includeFilters.size() == 0 && excludeFilters.size() == 0) || matches(metadataReader)) {
                        classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new BeanDefinitionStoreException("failure during classpath scanning", e);
        }
        return classes;
    }

    protected boolean matches(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : excludeFilters) {
            if (tf.match(metadataReader, metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : includeFilters) {
            if (tf.match(metadataReader, metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        excludeFilters.add(excludeFilter);
    }

    public void resetFilters() {
        includeFilters.clear();
        excludeFilters.clear();
    }

}
