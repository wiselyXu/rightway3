package com.swj.freeway;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class HttpContext {

    public static String getCurrentUserId()
    {
        return getRequest().getHeader("_REQ_USER_ID");
    }

    public static String getCurrentOrgId()
    {
        return getRequest().getHeader("_REQ_ORG_ID");
    }

    public static String getCurrentDeptId()
    {
        return getRequest().getHeader("_REQ_DEPT_ID");
    }

    public static HttpServletRequest getRequest()
    {
        ServletRequestAttributes  requestAttributes =  (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }
}
