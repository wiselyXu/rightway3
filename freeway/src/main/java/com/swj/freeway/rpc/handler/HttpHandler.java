package com.swj.freeway.rpc.handler;

import com.alibaba.fastjson.JSON;
import com.swj.basic.SwjConfig;
import com.swj.basic.helper.ArrayHelper;
import com.swj.basic.helper.HttpHelper;
import com.swj.basic.helper.StringHelper;
import com.swj.freeway.common.Charset;
import com.swj.freeway.common.Constant;
import com.swj.freeway.common.StringGenerator;
import com.swj.freeway.rpc.exception.RpcException;
import com.swj.freeway.servlet.ApiServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.util.StopWatch;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/10 14:40
 **/
@Slf4j
public class HttpHandler {

    public Object handle(HandleParam handleParam, Object[] args, Class returnType) {

        String serviceIp = SwjConfig.getServiceAddress().get(handleParam.getAppNode());

        if (!StringHelper.isNullOrEmpty(serviceIp)) {
            try {
                // HTTP请求
                String url = "http://" + serviceIp + "/api/" + handleParam.getServicePath();

                String params = "";
                if (ArrayHelper.isNotEmpty(args)) {
                    if (args.length == 1) {
                        params = JSON.toJSONString(args[0]);
                    } else {
                        params = JSON.toJSONString(args);
                    }
                }

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                String result = HttpHelper.post(url, Charset.UTF_8, params);
                String reqSeq = StringGenerator.getReqSeq((String) MDC.get(Constant.REQ_SEQ));
                MDC.put(Constant.REQ_SEQ, reqSeq);
                log.info("{\"RequestId\":\"{}\",\"RequestSeq\":\"{}\",\"SourceIP\":\"{}\",\"SourceApp\":\"{}\",\"command\":\"{}\",\"totalTimeMillis\":{}," +
                        "\"params\":{},\"LogDate\":\"{}\",\"LocalHost\":\"{}\",\"LocalApp\":\"{}\"}", MDC.get(Constant.REQ_ID), MDC.get(Constant.REQ_SEQ),
                        MDC.get(Constant.REQ_SOURCE_HOST), MDC.get(Constant.REQ_SOURCE_APP), "api/" + handleParam.getServicePath(), stopWatch
                                .getTotalTimeMillis(), params, ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), ApiServlet.localIp,
                        ApiServlet.appName);
                if (Void.TYPE.equals(returnType)) return null;
                return JSON.parseObject(result, returnType);

            } catch (Exception e) {
                log.info("freeway fail", e);
                throw new RpcException("freeway fail");
            }
        }

        throw new RpcException("Can not found service : " + handleParam.getApiName());
    }

}
