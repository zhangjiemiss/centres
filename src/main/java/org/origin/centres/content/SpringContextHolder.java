package org.origin.centres.content;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author zhangjie
 * @version 2019-10-18
 * @apiNote 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出 ApplicationContext
 */
@SuppressWarnings({"unused", "unchecked"})
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     * 清除SpringContextHolder中的ApplicationContext为Null.
     */
    @Override
    public void destroy() throws Exception {
        applicationContext = null;
    }
}
