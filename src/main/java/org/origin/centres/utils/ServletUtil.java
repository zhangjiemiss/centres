package org.origin.centres.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author zhangjie
 * @version 2020-09-30
 * @apiNote 系统工具
 */
@SuppressWarnings("unused")
public class ServletUtil {

    /**
     * 获取网络请求 HttpSession
     *
     * @return HttpServletRequest
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getAttributes().getRequest();
        return request.getSession();
    }

    /**
     * 获取网络请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return getAttributes().getRequest();
    }

    /**
     * 获取网络请求响应
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return getAttributes().getResponse();
    }

    /**
     * 获取 Attributes
     *
     * @return ServletRequestAttributes
     */
    private static ServletRequestAttributes getAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        if (attributes == null) throw new IllegalArgumentException("请求为非客户端请求");
        return attributes;
    }
}
