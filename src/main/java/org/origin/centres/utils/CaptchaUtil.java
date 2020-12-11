package org.origin.centres.utils;

import java.util.Date;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote 验证码抽象
 */
@SuppressWarnings("ALL")
public class CaptchaUtil {

    /**
     * 组装验证码有效时间信息
     *
     * @param code 验证码
     * @return 验证码信息
     */
    public static String getValue(String code) {
        return getValue(code, 3 * 60 * 1000);
    }

    /**
     * 组装验证码有效时间信息
     *
     * @param code 验证码
     * @param time 时长
     * @return 验证码信息
     */
    public static String getValue(String code, int time) {
        return code + "_#_" + (new Date().getTime() + time);
    }

    /**
     * 获取真实验证码-该验证码是由getValue()方法生成
     *
     * @param val 验证码信息
     * @return 真实验证码
     */
    public static String getCaptcha(Object val) {
        if (val instanceof String) {
            String value = (String) val;
            if (value.contains("_#_")) {
                String[] split = value.split("_#_");
                if (split.length == 2) {
                    String timeStr = split[1];
                    if (isNumeric(timeStr)) {
                        long time = Long.parseLong(timeStr);
                        if (time > 0) {
                            if (time >= new Date().getTime()) {
                                if (split[0] != null) {
                                    return split[0];
                                } else {
                                    throw new IllegalArgumentException("验证码无效");
                                }
                            } else {
                                throw new IllegalArgumentException("验证码无效");
                            }
                        } else {
                            throw new IllegalArgumentException("验证码错误");
                        }
                    } else {
                        throw new IllegalArgumentException("验证码错误");
                    }
                } else {
                    throw new IllegalArgumentException("验证码错误");
                }
            } else {
                throw new IllegalArgumentException("验证码错误");
            }
        } else {
            throw new IllegalArgumentException("验证码无效");
        }
    }

    /**
     * 验证验证码是否匹配
     *
     * @param info    真实验证码信息
     * @param captcha 提交验证码
     */
    public static void validate(String info, String captcha) {
        if (captcha == null) {
            throw new IllegalArgumentException("请输入验证码");
        }
        if (info == null) {
            throw new IllegalArgumentException("验证码无效");
        }
        String code = getCaptcha(info);
        if (!captcha.trim().toUpperCase().equals(code.trim().toUpperCase())) {
            throw new IllegalArgumentException("验证码错误");
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        } else {
            int sz = str.length();
            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
