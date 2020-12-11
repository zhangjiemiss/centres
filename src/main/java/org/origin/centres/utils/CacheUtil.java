package org.origin.centres.utils;

import org.origin.centres.constants.IConstant;
import org.origin.centres.content.SpringContextHolder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author: zhangjie
 * @version: 2019-08-13
 * @apiNote 系统缓存工具
 */
@SuppressWarnings("ALL")
public class CacheUtil {
    private static final CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);

    /**
     * 保存
     *
     * @param key   键
     * @param value 值
     */
    public static Object put(String key, Object value) {
        return put(IConstant.CacheName, key, value);
    }

    /**
     * 保存
     *
     * @param key   键
     * @param value 值
     */
    public static Object put(String name, String key, Object value) {
        if (cacheManager == null) {
            throw new IllegalArgumentException("no init cache manager");
        }
        Cache cache = cacheManager.getCache(name);
        if (cache != null) cache.put(key, value);
        return value;
    }

    /**
     * 获取
     *
     * @param key   键
     * @param clazz 值类型
     */
    public static <T> T get(String key, Class<T> clazz) {
        return get(IConstant.CacheName, key, clazz);
    }

    /**
     * 获取
     *
     * @param key   键
     * @param clazz 值类型
     */
    public static <T> T get(String name, String key, Class<T> clazz) {
        if (cacheManager == null) {
            throw new IllegalArgumentException("no init cache manager");
        }
        Cache cache = cacheManager.getCache(name);
        return cache != null ? cache.get(key, clazz) : null;
    }

    /**
     * 删除
     *
     * @param key 键
     */
    public static void remove(String key) {
        remove(IConstant.CacheName, key);
    }

    /**
     * 删除
     *
     * @param key 键
     */
    public static void remove(String name, String key) {
        if (cacheManager == null) {
            throw new IllegalArgumentException("no init cache manager");
        }
        Cache cache = cacheManager.getCache(name);
        if (cache != null) cache.evict(key);
    }

    /**
     * 删除所有
     */
    public static void clear() {
        clear(IConstant.CacheName);
    }

    /**
     * 删除所有
     */
    public static void clear(String name) {
        if (cacheManager == null) {
            throw new IllegalArgumentException("no init cache manager");
        }
        Cache cache = cacheManager.getCache(name);
        if (cache != null) cache.clear();
    }
}
