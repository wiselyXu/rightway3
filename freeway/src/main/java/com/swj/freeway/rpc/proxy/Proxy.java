package com.swj.freeway.rpc.proxy;

import com.swj.basic.annotation.FreewayPath;
import com.swj.basic.helper.StringHelper;
import com.swj.freeway.common.ClassGenerator;
import com.swj.freeway.rpc.handler.HandleParam;
import com.swj.freeway.rpc.handler.HttpHandler;
import com.swj.freeway.zookeeper.ZnodeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 生成代理类
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/10 14:26
 **/
@Slf4j
public class Proxy {

    public static Class getProxy(Class clazz) {
        log.info("开始生成代理类:{}", clazz.getName());
        try {
            ClassGenerator classGenerator = new ClassGenerator(clazz.getClassLoader());
            log.info("ClassGenerator ClassLoader : {}", clazz.getClassLoader());

            String proxyClassName = clazz.getPackage().getName() + "." + clazz.getSimpleName() + "$proxy";
            classGenerator.setClassName(proxyClassName);
            classGenerator.addField("public static " + HandleParam.class.getName() + "[] handleParams;");
            classGenerator.addField("public static Class[] returnTypes;");
            classGenerator.addField("private " + HttpHandler.class.getName() + " handler = new " + HttpHandler.class.getName() + "();");
            classGenerator.addInterface(clazz);

            Method[] methods = clazz.getMethods();
            HandleParam[] handleParams = new HandleParam[methods.length];
            Class[] returnTypes = new Class[methods.length];
            for (int i = 0; i < methods.length; i++) {
                handleParams[i] = getHandleParam(clazz, methods[i]);
                returnTypes[i] = methods[i].getReturnType();

                StringBuilder code = new StringBuilder();
                code.append("Object[] args = $args;");
                code.append("Object result = handler.handle(handleParams[").append(i).append("], args, returnTypes[").append(i).append("]);");
                if (!Void.TYPE.equals(returnTypes[i])) code.append("return ").append(getReturnCode(returnTypes[i], "result")).append(";");

                classGenerator.addMethod(methods[i], code.toString());
            }

            Class proxyClass = classGenerator.toClass();

            proxyClass.getField("returnTypes").set(null, returnTypes);
            proxyClass.getField("handleParams").set(null, handleParams);
            log.info("生成代理类:{}", proxyClass.getName());
            return proxyClass;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换方法返回参数类型
     */
    private static String getReturnCode(Class clazz, String name) {
        if (clazz.isPrimitive()) {
            if (Boolean.TYPE == clazz) return name + "==null?false:((Boolean)" + name + ").booleanValue()";
            if (Byte.TYPE == clazz) return name + "==null?(byte)0:((Byte)" + name + ").byteValue()";
            if (Character.TYPE == clazz) return name + "==null?(char)0:((Character)" + name + ").charValue()";
            if (Double.TYPE == clazz) return name + "==null?(double)0:((Double)" + name + ").doubleValue()";
            if (Float.TYPE == clazz) return name + "==null?(float)0:((Float)" + name + ").floatValue()";
            if (Integer.TYPE == clazz) return name + "==null?(int)0:((Integer)" + name + ").intValue()";
            if (Long.TYPE == clazz) return name + "==null?(long)0:((Long)" + name + ").longValue()";
            if (Short.TYPE == clazz) return name + "==null?(short)0:((Short)" + name + ").shortValue()";
            throw new RuntimeException(name + " is unknown primitive type.");
        }
        return "(" + clazz.getName() + ")" + name;
    }

    private static HandleParam getHandleParam(Class clazz, Method method) {
        FreewayPath classPath = (FreewayPath) clazz.getAnnotation(FreewayPath.class);
        FreewayPath methodPath = method.getAnnotation(FreewayPath.class);
        HandleParam handleParam = new HandleParam();
        handleParam.setApiName(clazz.getName());
        String apiName;
        String methodName;
        if (StringHelper.isNullOrWhiteSpace(classPath.value())) {
            apiName = StringHelper.toLowerCaseFirstOne(clazz.getSimpleName());
        } else {
            apiName = classPath.value();
        }
        if (methodPath == null || StringHelper.isNullOrWhiteSpace(methodPath.value())) {
            methodName = StringHelper.toLowerCaseFirstOne(method.getName());
        } else {
            methodName = methodPath.value();
        }
        handleParam.setServicePath(apiName + "/" + methodName);
        handleParam.setAppNode(ZnodeUtils.getAppNode(clazz));
        return handleParam;
    }

}
