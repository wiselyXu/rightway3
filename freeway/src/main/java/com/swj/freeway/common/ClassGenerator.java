package com.swj.freeway.common;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * 类生成工具类
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/10 10:12
 **/
@Slf4j
public class ClassGenerator {

    private static final Map<ClassLoader, ClassPool> classPools = new HashMap<>();
    private ClassPool classPool;
    private CtClass   ctClass;
    private String    className;
    private Set<String>  interfaces = null;
    private List<String> fields     = null;
    private List<String> methods    = null;

    public ClassGenerator() {
        this.classPool = getClassPool(Thread.currentThread().getContextClassLoader());
    }

    public ClassGenerator(ClassLoader classLoader) {
        this.classPool = getClassPool(classLoader);
    }

    public Class<?> toClass() {
        try {
            // 若ctClass不为空，则将其从classPool移除，避免OutOfMemory
            if (ctClass != null) ctClass.detach();

            ctClass = classPool.makeClass(className);

            if (interfaces != null) {
                for (String api : interfaces) ctClass.addInterface(classPool.get(api));
            }

            if (fields != null) {
                for (String field : fields) ctClass.addField(CtField.make(field, ctClass));
            }

            if (methods != null) {
                for (String method : methods) ctClass.addMethod(CtNewMethod.make(method, ctClass));
            }

            return ctClass.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public void addInterface(Class clazz) {
        if (interfaces == null) {
            interfaces = new HashSet<>();
        }
        interfaces.add(clazz.getName());
    }

    public void addMethod(String code) {
        if (methods == null) {
            methods = new ArrayList<>();
        }
        methods.add(code);
    }

    public void addField(String code) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(code);
    }

    public void addMethod(Method method, String body) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getModifier(method.getModifiers())).append(" ").append(method.getReturnType().getName()).append(" ").append(method.getName());
        buffer.append('(');
        Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(parameterTypes[i].getName());
            buffer.append(" arg").append(i);
        }
        buffer.append(')');
        buffer.append('{').append(body).append('}');
        addMethod(buffer.toString());
    }

    public void release() {
        if (ctClass != null) ctClass.detach();
        if (interfaces != null) interfaces.clear();
        if (fields != null) fields.clear();
        if (methods != null) methods.clear();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private String getModifier(int mod) {
        if (java.lang.reflect.Modifier.isPublic(mod)) return "public";
        if (java.lang.reflect.Modifier.isProtected(mod)) return "protected";
        if (java.lang.reflect.Modifier.isPrivate(mod)) return "private";
        return "";
    }

    private ClassPool getClassPool(ClassLoader classLoader) {
        if (classLoader == null) {
            return ClassPool.getDefault();
        }

        if (!classPools.containsKey(classLoader)) {
            synchronized (classPools) {
                if (!classPools.containsKey(classLoader)) {
                    ClassPool pool = ClassPool.getDefault();
                    pool.appendClassPath(new LoaderClassPath(classLoader));
                    classPools.put(classLoader, pool);
                }
            }
        }

        return classPools.get(classLoader);
    }

}
