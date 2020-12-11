package org.origin.centres.exception;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote XrException 异常
 */
@SuppressWarnings("unused")
public class XrException extends RuntimeException {
    private Object data;

    public XrException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
