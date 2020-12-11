package org.origin.centres.security.remote.exception;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

/**
 * @author zhangjie
 * @version 2020-10-08
 * @apiNote Invalid Exception 为远程调用抛出的异常处理
 */
public class InvalidException extends ClientAuthenticationException {

    public InvalidException(String msg) {
        super(msg);
    }

    @Override
    public int getHttpErrorCode() {
        return 403;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid";
    }
}
