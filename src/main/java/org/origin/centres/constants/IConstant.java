package org.origin.centres.constants;

/**
 * @author zhangjie
 * @version 2020-06-29
 * @apiNote 静态常量
 */
public class IConstant {
    // ++++++++++++++++ 缓存名称 ++++++++++++++++
    public static final String CacheName = "alive";                 // 默认缓存名称
    public static final String CacheDict = "'sys:dict:info'";         // 字典缓存名称
    public static final String CacheConfig = "'sys:config:info'";     // 配置缓存名称
    // ++++++++++++++++ 配置名称 ++++++++++++++++
    public static final String ConfigName = "sys_config_value";     // 配置名称
    public static final String ConfigPass = "sys:user:password";     // 配置名称
    // ++++++++++++++++ 数据状态 ++++++++++++++++
    public static final Integer Usable = 0;         // 可用
    public static final Integer Disable = 1;        // 禁用
    public static final Integer Useful = 2;         // 有用-可用
    public static final Integer Useless = 3;        // 无用-禁用
    // ++++++++++++++++ 默认状态 ++++++++++++++++
    public static final Integer DefaultYes = 0;     // 默认
    public static final Integer DefaultNo = 1;      // 非默认
    // ++++++++++++++++ 显示状态 ++++++++++++++++
    public static final Integer DisplayYes = 0;     // 显示
    public static final Integer DisplayNo = 1;      // 隐藏
    // ++++++++++++++++ 日志类型 ++++++++++++++++
    public static final Integer LogsLogin = 1;      // 登录
    public static final Integer LogsError = 2;      // 错误
    public static final Integer LogsOption = 3;     // 操作
    // ++++++++++++++++ 父级ID ++++++++++++++++
    public static final Integer TopPid = 0;         // 最顶级父ID
    public static final Integer TopId = 1;          // 最顶ID
    // ++++++++++++++++ 查询名称 ++++++++++++++++
    public static final String Page = "page";       // 查询的分页参数的名称
    public static final String Entity = "entity";   // 查询的实体参数的名称
}
