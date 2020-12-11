package org.origin.centres.dynamic.holder;

/**
 * @author zhangjie
 * @version 2020-07-06
 * @apiNote 动态数据源切换线程维护
 */
public class DynamicDataSourceHolder {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void setDataSource(String dataSource) {
        HOLDER.set(dataSource);
    }

    /**
     * 获得数据源的变量
     */
    public static String getDataSource() {
        return HOLDER.get();
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSource() {
        HOLDER.remove();
    }
}
