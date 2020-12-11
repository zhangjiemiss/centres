package org.origin.centres.result.entity;

import org.origin.centres.result.interfaces.IResult;

/**
 * @author zhangjie
 * @version 2020-06-23
 * @apiNote 数据返回实体类
 */
public class IResultEntity<T> implements IResult {
    private Integer status;
    private String message;
    private T data;

    public IResultEntity() {
    }

    public IResultEntity(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    public IResultEntity setStatus(Integer status) {
        this.status = status;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public IResultEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public IResultEntity setData(T data) {
        this.data = data;
        return this;
    }
}
