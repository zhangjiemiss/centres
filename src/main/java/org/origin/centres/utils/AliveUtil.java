package org.origin.centres.utils;

import org.origin.centres.interfaces.IBaseEnum;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjie
 * @version 2018-04-15
 * @apiNote 系统工具
 */
@SuppressWarnings({"ALL","unchecked"})
public class AliveUtil {

    /**
     * 执行对象方法
     *
     * @param object 执行对象
     * @param name   方法名称
     * @param <T>    方法返回值泛型
     * @return 方法返回值
     */
    public static <T> T invokeObj(Object object, String name) {
        try {
            if (object != null) {
                Class clazz = object.getClass();
                Method method = clazz.getMethod(name);
                Object value = method.invoke(object);
                return value != null ? (T) value : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Get方法获取对象方法返回值
     *
     * @param object 执行对象
     * @param name   方法名称
     * @param <T>    方法返回值泛型
     * @return 方法返回值
     */
    public static <T> T getObjValue(Object object, String name) {
        try {
            if (object != null) {
                char[] chars = name.toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                String getName = "get" + String.valueOf(chars);
                Class clazz = object.getClass();
                Method method = clazz.getMethod(getName);
                Object value = method.invoke(object);
                return value != null ? (T) value : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Get方法获取对象方法返回值
     *
     * @param object 执行对象
     * @param name   方法名称
     * @return 方法返回值
     */
    public static Object getObjVal(Object object, String name) {
        try {
            if (object != null) {
                char[] chars = name.toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                String getName = "get" + String.valueOf(chars);
                Class clazz = object.getClass();
                Method method = clazz.getMethod(getName);
                Object value = method.invoke(object);
                return value != null ?  value : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Set方法设置对象值
     *
     * @param object 执行对象
     * @param name   方法名称
     * @param value  设置对象值
     */
    public static boolean setObjValue(Object object, String name, Object value) {
        try {
            if (object != null) {
                char[] chars = name.toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                String getName = "set" + String.valueOf(chars);
                Class clazz = object.getClass();
                Method method = clazz.getMethod(getName, value.getClass());
                method.invoke(object, value);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取所有的class的属性
     *
     * @param clazz Class
     * @return Fields
     */
    public static List<Field> getFilesByClass(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            List<Field> supperFiles = getFilesByClass(superclass);
            if (supperFiles != null && supperFiles.size() > 0) {
                result = supperFiles;
            }
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field != null) {
                field.setAccessible(true);
                result.add(field);
            }
        }
        return result;
    }

    /**
     * 通过字段名获取字段
     *
     * @param clazz Class
     * @param name  字段名
     * @return 属性
     */
    public static Field getFieldByName(Class<?> clazz, String name) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            if (declaredField != null) {
                declaredField.setAccessible(true);
                return declaredField;
            }
        } catch (NoSuchFieldException e) {
            return getFieldByName(clazz.getSuperclass(), name);
        }
        return null;
    }

    /**
     * 通过字段名获取字段值
     *
     * @param object Object
     * @param name   字段名
     * @return 字段值
     */
    public static <T> T getFieldValue(Object object, String name) {
        try {
            Field field = getFieldByName(object.getClass(), name);
            if (field != null) {
                Object value = field.get(object);
                return value != null ? (T) value : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取并解析Map键名称的值并返回
     *
     * @param params Map 参数
     * @param name   键名称
     * @param <T>    解析返回值泛型
     * @return 返回值
     */
    public static <T> T resolveMap(Map<String, ?> params, String name) {
        if (params != null && params.containsKey(name)) {
            Object obj = params.get(name);
            try {
                return (T) obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取并解析Map键名称的值并返回（集合）
     *
     * @param params Map 参数
     * @param name   键名称
     * @param <T>    解析返回值泛型
     * @return 返回值
     */
    public static <T> List<T> resolveList(Map<String, ?> params, String name) {
        if (params != null && params.containsKey(name)) {
            Object listObj = params.get(name);
            if (listObj instanceof List) {
                try {
                    return (List<T>) listObj;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将Map中的值返回到一个集合中
     *
     * @param params Map参数
     * @param <T>    集合泛型
     * @return 返回集合
     */
    public static <T> List<T> translateList(Map<String, T> params) {
        List<T> list = new ArrayList<>();
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.containsKey(key)) {
                    T value = params.get(key);
                    if (value != null) {
                        list.add(value);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 将集合中对象中的某个属性值作为键名，以每一个集合项作为值返回Map对象
     *
     * @param list    集合参数
     * @param keyName 属性名称
     * @param <T>     返回Map对象泛型
     * @return 返回Map对象
     */
    public static <T> Map<String, T> translateMap(List<T> list, String keyName) {
        if (list != null && list.size() > 0) {
            Map<String, T> map = new HashMap<>();
            for (T t : list) {
                try {
                    Object objKey = getObjValue(t, keyName);
                    if (objKey != null) {
                        if (objKey instanceof String) {
                            map.put((String) objKey, t);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return map;
        }
        return null;
    }

    /**
     * 将集合中对象中的某个属性值作为键名，以每一个集合项中的某属性值作为值返回Map对象
     *
     * @param list    集合参数
     * @param keyName 属性名称
     * @param <T>     传入List对象泛型
     * @param <X>     返回Map对象泛型
     * @return 返回Map对象
     */
    public static <T, X> Map<String, X> translateMap(List<T> list, String keyName, String valueName) {
        if (list != null && list.size() > 0) {
            Map<String, X> map = new HashMap<>();
            for (T t : list) {
                try {
                    Object objKey = getObjValue(t, keyName);
                    Object objValue = getObjValue(t, valueName);
                    if (objKey != null && objValue != null) {
                        if (objKey instanceof String) {
                            map.put((String) objKey, (X) objValue);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return map;
        }
        return null;
    }

    /**
     * 获取集合中每一项中的对象中的某个属性值作为集合返回回来
     *
     * @param list    传入集合对象
     * @param keyName 属性名称
     * @return 属性值集合
     */
    public static List<Object> findListByKey(List<?> list, String keyName) {
        if (list != null && keyName != null) {
            try {
                List<Object> mList = new ArrayList<>();
                for (Object object : list) {
                    if (object != null) {
                        Object value = getObjValue(object, keyName);
                        if (value != null) {
                            mList.add(value);
                        }
                    }
                }
                return mList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取集合中每一项中的对象中的某个属性值作为集合返回回来
     *
     * @param list    传入集合对象
     * @param keyName 属性名称
     * @param <T>     属性值泛型
     * @return 属性值集合
     */
    public static <T> List<T> gainListByKey(List<?> list, String keyName) {
        if (list != null && keyName != null) {
            try {
                List<T> mList = new ArrayList<>();
                for (Object object : list) {
                    if (object != null) {
                        Object value = getObjValue(object, keyName);
                        if (value != null) {
                            mList.add((T) value);
                        }
                    }
                }
                return mList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取集合中每一项中的ID值作为集合返回回来
     *
     * @param list 传入集合对象
     * @return ID值集合
     */
    public static List<String> getListById(List<?> list) {
        return gainListByKey(list, "id");
    }

    /**
     * 通过key获取到继承IBaseEnum的枚举类型值
     *
     * @param values 传入枚举类型所有值
     * @param key    key值
     * @param <T>    参数泛型
     * @return 枚举类型值
     */
    public static <T extends IBaseEnum> T getBaseEnumByKey(T[] values, Integer key) {
        if (values != null && values.length > 0) {
            if (key != null) {
                for (T value : values) {
                    if (value != null) {
                        if (value.getKey() != null &&
                                value.getKey().equals(key)) {
                            return value;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 通过value获取到继承IBaseEnum的枚举类型值
     *
     * @param values 传入枚举类型所有值
     * @param val    val值
     * @param <T>    参数泛型
     * @return 枚举类型值
     */
    public static <T extends IBaseEnum> T getBaseEnumByValue(T[] values, String val) {
        if (values != null && values.length > 0) {
            if (val != null) {
                for (T value : values) {
                    if (value != null) {
                        if (value.getValue() != null &&
                                value.getValue().trim().equals(val.trim())) {
                            return value;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取到异常中堆栈信息
     *
     * @param e 异常
     * @return 堆栈信息
     */
    public static String getStackTrace(Exception e) {
        /**
         * 获取堆栈信息
         *
         * @param e 异常
         * @return 堆栈信息
         */
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw, true)) {
            e.printStackTrace(pw);
            return sw.getBuffer().toString();
        } catch (Exception ex) {
            // ignore ex.printStackTrace();
        }
        // ignore ex.printStackTrace();
        // ignore ex.printStackTrace();

        return null;
    }
}
