package org.origin.centres.result;

import org.origin.centres.result.entity.IResultEntity;
import org.origin.centres.result.enums.Re;
import org.origin.centres.result.interfaces.IResult;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangjie
 * @version 2020-09-29
 * @apiNote 数据返回实体类
 */
public class R {

    public static <T> IResultEntity<T> ok() {
        return result(Re.Sq, null);
    }

    public static <T> IResultEntity<T> ok(T data) {
        return result(Re.Sq, data);
    }

    public static <T> IResultEntity<T> ok(String message, T data) {
        return result(HttpStatus.OK.value(), message, data);
    }

    public static <T> IResultEntity<T> ok(IResult result, T data) {
        return result(HttpStatus.OK.value(), result.getMessage(), data);
    }

    public static <T> IResultEntity<T> fail() {
        return result(Re.Fq, null);
    }

    public static <T> IResultEntity<T> fail(String message) {
        return result(HttpStatus.FORBIDDEN.value(), message, null);
    }

    public static <T> IResultEntity<T> fail(String message, T data) {
        return result(HttpStatus.FORBIDDEN.value(), message, data);
    }

    public static <T> IResultEntity<T> fail(IResult result) {
        return result(HttpStatus.FORBIDDEN.value(), result.getMessage(), null);
    }

    public static <T> IResultEntity<T> fail(IResult result, T data) {
        return result(HttpStatus.FORBIDDEN.value(), result.getMessage(), data);
    }

    public static <T> IResultEntity<T> result(IResult result, T data) {
        return result(result.getStatus(), result.getMessage(), data);
    }

    public static <T> IResultEntity<T> result(Integer status, String message, T data) {
        return new IResultEntity<>(status, message != null ? message : status == HttpStatus.OK.value() ? Re.Sq.getMessage() : Re.Fq.getMessage(), data);
    }

    public static Map<String, Object> rest(Integer status, String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("message", message != null ? message : status == HttpStatus.OK.value() ? "请求成功" : "请求失败");
        if (data != null) result.put("data", data);
        return result;
    }
}
