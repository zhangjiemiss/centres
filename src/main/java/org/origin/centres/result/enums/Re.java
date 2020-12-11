package org.origin.centres.result.enums;

import org.origin.centres.result.interfaces.IResult;
import org.springframework.http.HttpStatus;

/**
 * @author zhangjie
 * @version 2020-11-17
 * @apiNote 数据返回实体类枚举
 */
public enum Re implements IResult {
    // 成功返回
    Sq(HttpStatus.OK.value(), "请求成功"),
    Sr(HttpStatus.OK.value(), "查询成功"),
    Si(HttpStatus.OK.value(), "添加成功"),
    Su(HttpStatus.OK.value(), "修改成功"),
    Sd(HttpStatus.OK.value(), "删除成功"),
    Sn(HttpStatus.OK.value(), "启用成功"),
    So(HttpStatus.OK.value(), "禁用成功"),
    // 失败返回
    Fq(HttpStatus.FORBIDDEN.value(), "请求失败"),
    Fr(HttpStatus.FORBIDDEN.value(), "查询失败"),
    Fi(HttpStatus.FORBIDDEN.value(), "添加失败"),
    Fu(HttpStatus.FORBIDDEN.value(), "修改失败"),
    Fd(HttpStatus.FORBIDDEN.value(), "删除失败"),
    Fn(HttpStatus.FORBIDDEN.value(), "启用失败"),
    Fo(HttpStatus.FORBIDDEN.value(), "禁用失败");

    private Integer status;
    private String message;

    Re(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
