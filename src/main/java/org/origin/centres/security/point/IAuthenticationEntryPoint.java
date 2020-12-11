package org.origin.centres.security.point;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.ThrowableAnalyzer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote 无效TOKEN异常类重写
 */
@SuppressWarnings("ALL")
public class IAuthenticationEntryPoint implements AuthenticationEntryPoint {

    protected ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        OAuth2Exception ex = this.getOAuthException(exception);
        if (ex instanceof InvalidTokenException) {
            this.responseError(response, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "访问失败，凭证已过期");
        } else {
            // 其他异常信息
            this.responseError(response, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), ex.getMessage());
        }
        /*else if (ex instanceof OAuth2AccessDeniedException) {
            // 远程调用返回无访问资源异常信息
            this.responseError(response, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), ex.getMessage());
        } else if (ex instanceof InvalidException) {
            // 远程调用返回异常信息
            this.responseError(response, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), ex.getMessage());
        }*/
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

    /**
     * 获取到真实的异常信息
     *
     * @param exception 异常信息
     * @return 真实的异常信息
     */
    protected OAuth2Exception getOAuthException(AuthenticationException exception) {
        Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(exception);
        return (OAuth2Exception) this.throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
    }
}
