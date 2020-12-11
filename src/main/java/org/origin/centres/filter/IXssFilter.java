package org.origin.centres.filter;


import org.origin.centres.security.xss.XssHttpServletRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author zhangjie
 * @version 2020-07-03
 * @apiNote xss攻击，cros恶意访问过滤器-抽象
 * 需要添加注解 @Configuration @WebFilter
 */
public abstract class IXssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        XssHttpServletRequest xssHttpServletRequest = new XssHttpServletRequest(httpServletRequest);
        chain.doFilter(xssHttpServletRequest, response);
    }
}
