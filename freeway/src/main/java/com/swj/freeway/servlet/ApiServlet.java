package com.swj.freeway.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.swj.basic.annotation.FreewayPath;
import com.swj.basic.annotation.Transaction;
import com.swj.basic.dto.BaseParam;
import com.swj.basic.dto.ResultWrap;
import com.swj.basic.helper.StringHelper;
import com.swj.basic.validation.EntityValidation;
import com.swj.basic.validation.EntityValidationException;
import com.swj.freeway.common.Charset;
import com.swj.freeway.common.ClassScanner;
import com.swj.freeway.common.Constant;
import com.swj.freeway.common.StringGenerator;
import com.swj.freeway.servlet.enums.ErrorCodeEnum;
import com.swj.freeway.servlet.exception.ServiceException;
import com.swj.mustang.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StopWatch;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ApiServlet extends HttpServlet {

    public static String localIp;
    public static String appName;
    private static Map<String, Method> methodMap = new HashMap<>();
    private static Map<String, Object> implMap   = new HashMap<>();
    private ApplicationContext context;
    private ServletConfig      servletConfig;

    public ApiServlet(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    public static ServletRegistrationBean getServletRegistrationBean(ServletConfig servletConfig) {
        return new ServletRegistrationBean<>(new ApiServlet(servletConfig), "/api/*");
    }

    @Override
    public void init() throws ServletException {
        if (StringHelper.isNullOrWhiteSpace(servletConfig.getApiPackage())) {
            throw new RuntimeException("api包路径不能为空");
        }

        if (StringHelper.isNullOrWhiteSpace(servletConfig.getAppName())) {
            throw new RuntimeException("应用名不能为空");
        }
        appName = servletConfig.getAppName();

        // 获取spring上下文
        context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());

        // 获取本机IP
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("get local ip fail", e);
        }

        // 获取@Path注解的接口类
        ClassScanner classScanner = new ClassScanner();
        TypeFilter typeFilter = (metadataReader, metadataReaderFactory) -> {
            String pathAnnotation = FreewayPath.class.getName();
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            return annotationMetadata.getAnnotationTypes().contains(pathAnnotation);
        };
        classScanner.addIncludeFilter(typeFilter);
        List<Class> apis = classScanner.doScan(servletConfig.getApiPackage());

        /**
         * 初始化接口方法methodMap，格式<apiName/methodName, method>
         * 初始化接口实现implMap，格式<apiName/methodName, impl>
         */
        try {
            for (Class api : apis) {
                initImplMethod(api);
            }
        } catch (NoSuchMethodException e) {
            log.info("no such method", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultWrap result = new ResultWrap();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        MDC.put(Constant.REQ_ID, StringHelper.isNullOrWhiteSpace(req.getHeader(Constant.REQ_ID)) ? StringGenerator.getReqId() : req.getHeader(Constant.REQ_ID));
        MDC.put(Constant.REQ_SEQ, StringGenerator.getReqSeq(req.getHeader(Constant.REQ_SEQ)));
        MDC.put(Constant.REQ_SOURCE_HOST, req.getHeader(Constant.REQ_SOURCE_HOST));
        MDC.put(Constant.REQ_SOURCE_APP, req.getHeader(Constant.REQ_SOURCE_APP));

        String command = null;
        String paramJson = null;
        try {
            // 解析请求url，获取command
            command = getCommand(req.getRequestURI());
            if (StringHelper.isNullOrWhiteSpace(command)) {
                result.setErrorCode(ErrorCodeEnum.COMMAND_INVALID.getCode());
                result.setErrorMessage("command is invalid");
                write(req, resp, result, stopWatch, "");
                return;
            }

            // 获取请求Body的json
            paramJson = getJsonBody(req);
            if (StringHelper.isNullOrEmpty(paramJson)) {
                result.setErrorCode(ErrorCodeEnum.REQUEST_PARAM_INVALID.getCode());
                result.setErrorMessage("params is required");
                write(req, resp, result, stopWatch, paramJson);
                return;
            }

            Method method = methodMap.get(command);

            List<Object> params = getMethodArgs(method, paramJson);
            if (params == null) {
                result.setErrorCode(ErrorCodeEnum.REQUEST_PARAM_INVALID.getCode());
                result.setErrorMessage("params error");
                write(req, resp, result, stopWatch, paramJson);
                return;
            }

            Object object = handlerRequest(command, method, params.toArray());
            result.setSuccess(true);
            result.setResult(object);
        } catch (EntityValidationException e) {
            result.setErrorCode(ErrorCodeEnum.REQUEST_PARAM_INVALID.getCode());
            result.setErrorMessage(e.getMessage());
        } catch (JSONException e) {
            result.setErrorCode(ErrorCodeEnum.REQUEST_PARAM_INVALID.getCode());
            result.setErrorMessage("params need to be json format");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ServiceException) {
                ServiceException serviceException = (ServiceException) cause;
                result.setErrorCode(serviceException.getCode());
                result.setErrorMessage(serviceException.getMessage());
            } else {
                log.error("command:{}, params:{}", command, paramJson, e.getTargetException());
                result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
                result.setErrorMessage("system error");
            }
        } catch (Exception e) {
            log.error("command:{}, params:{}", command, paramJson, e);
            result.setErrorCode(ErrorCodeEnum.UNKNOWN_ERROR.getCode());
            result.setErrorMessage("unknown error");
        }

        write(req, resp, result, stopWatch, paramJson);
    }

    /**
     * 接口实现类反射
     */
    private Object handlerRequest(String command, Method method, Object... args) throws Exception {
        Object target = implMap.get(command);
        Object result;

        try {
            // 判断是否需要开启事务
            boolean isOpenTran = method.getAnnotation(Transaction.class) != null;
            if (isOpenTran) {
                TransactionManager.open();
            }
            result = method.invoke(target, args);
            TransactionManager.commit();
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        }
        return result;
    }

    /**
     * 初始化接口方法methodMap，格式<apiName/methodName, method>
     * 初始化接口实现implMap，格式<apiName/methodName, impl>
     */
    @SuppressWarnings("unchecked")
    private void initImplMethod(Class api) throws NoSuchMethodException {
        FreewayPath apiPath = (FreewayPath) api.getAnnotation(FreewayPath.class);
        String apiName = apiPath == null ? "" : apiPath.value();
        if (StringHelper.isNullOrWhiteSpace(apiName)) {
            apiName = api.getSimpleName();
            if (apiName.contains("Service")) {
                apiName = apiName.replace("Service", "");
            }
            apiName = StringHelper.toLowerCaseFirstOne(apiName);
        }

        Object impl;
        for (Method method : api.getMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                FreewayPath methodPath = method.getAnnotation(FreewayPath.class);
                String methodName = methodPath == null ? "" : methodPath.value();
                if (StringHelper.isNullOrWhiteSpace(methodName)) {
                    methodName = StringHelper.toLowerCaseFirstOne(method.getName());
                }
                impl = context.getBean(api);
                methodMap.put(apiName + "/" + methodName, impl.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()));
                implMap.put(apiName + "/" + methodName, impl);
            }
        }
    }

    /**
     * 获取请求Body的json
     */
    private String getJsonBody(HttpServletRequest req) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), Charset.UTF_8));
        StringBuilder sb = new StringBuilder("");
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        return sb.toString();
    }

    /**
     * 解析请求url，获取command
     */
    private String getCommand(String url) {
        String[] command = url.split("/api/", 2);
        if (command.length <= 1) {
            return null;
        }
        if (!methodMap.containsKey(command[1])) {
            return null;
        }
        return command[1];
    }

    /**
     * 返回结果处理
     */
    private void write(HttpServletRequest req, HttpServletResponse resp, ResultWrap result, StopWatch stopWatch, String params) throws IOException {
        resp.setCharacterEncoding(Charset.UTF_8);
        stopWatch.stop();

        log.info("{\"RequestId\":\"{}\",\"RequestSeq\":\"{}\",\"SourceIP\":\"{}\",\"SourceApp\":\"{}\",\"command\":\"{}\",\"totalTimeMillis\":{}," +
                "\"params\":{},\"LogDate\":\"{}\",\"LocalHost\":\"{}\",\"LocalApp\":\"{}\"}", MDC.get(Constant.REQ_ID), MDC.get(Constant.REQ_SEQ), MDC.get
                (Constant.REQ_SOURCE_HOST), MDC.get(Constant.REQ_SOURCE_APP), req.getRequestURI(), stopWatch.getTotalTimeMillis(), params, ZonedDateTime.now
                ().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), localIp, servletConfig.getAppName());
        MDC.clear();
        PrintWriter writer = resp.getWriter();
        writer.print(JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect));
        writer.flush();
    }

    /**
     * 获取方法参数，目前不支持参数为多个的情况
     */
    private List<Object> getMethodArgs(Method method, String paramJson) throws IllegalAccessException {
        List<Object> result = new ArrayList<>();

        Class[] parameterTypes = method.getParameterTypes();

        // 方法参数为1个
        if (parameterTypes.length == 1) {
            Object param = JSON.parseObject(paramJson, parameterTypes[0]);

            if (param == null) return null;

            // 参数为BaseParam类型
            if (BaseParam.class.isAssignableFrom(parameterTypes[0])) {
                BaseParam baseParam = (BaseParam) param;
                EntityValidation.validate(baseParam);

            }
            result.add(param);
        }

        // 方法参数为多个
        else if (parameterTypes.length > 1) {
            return null;
        }

        return result;
    }

}
