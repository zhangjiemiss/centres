package org.origin.centres.security.point;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangjie
 * @version 2020-09-27
 * @apiNote 客户端信息异常类重写处理
 */
@SuppressWarnings("ALL")
public class IBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    public IBasicAuthenticationEntryPoint() {
        this.setRealmName("/oauth/token");
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        this.responseError(response, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "客户端信息不存在或不可用");
    }

    /**
     * 响应信息
     *
     * @param response 请求响应
     * @param state    请求响应状态
     * @param status   响应状态
     * @throws IOException 异常
     */
    protected void responseError(HttpServletResponse response, HttpStatus state, int status, String message) throws IOException {
        response.setStatus(state.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"status\":%d,\"message\":\"%s\"}", status, message));
    }
}
