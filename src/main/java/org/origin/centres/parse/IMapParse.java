package org.origin.centres.parse;

import jdk.nashorn.internal.ir.annotations.Ignore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author zhangjie
 * @version 2019-10-22
 * @apiNote Handle Map
 */
@SuppressWarnings({"ALL","unchecked"})
public class IMapParse {
    private String ALWAYS = "ALWAYS";
    private String NULL = "NULL";
    private String EMPTY = "EMPTY";

    public Map<String, Object> getMapByMethod(Object object) {
        return this.getMapByMethod(object, EMPTY);
    }

    public Map<String, Object> getMapByMethod(Object object, String type) {
        // ALWAYS NULL EMPTY
        Object value = this.getObjectValueByMethod(object, type);
        if (value != null) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    private Object getObjectValueByMethod(Object object, String type) {
        Method[] methods = object.getClass().getMethods();
        if (methods.length > 0) {
            Map<String, Object> result = new HashMap<>();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                int count = method.getParameterCount();
                if (count == 0) {
                    // 只处理无参方法
                    String name = this.getName(method.getName());
                    if (name != null && !this.isIgnore(method)) {
                        try {
                            Object val = method.invoke(object);
                            Object value = this.getValueByMethod(val, type);
                            if (this.hasPushVal(value, type)) {
                                result.put(name, value);
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    private Object getValueByMethod(Object object, String type) {
        if (object != null) {
            Class<?> clazz = object.getClass();
            if (this.isSimpleType(clazz)) {
                return object;
            } else if (List.class.isAssignableFrom(clazz)) {
                return this.getListValueByMethod(object, type);
            } else if (Map.class.isAssignableFrom(clazz)) {
                return this.getMapValueByMethod(object, type);
            } else if (clazz.isArray()) {
                return this.getArrayValueByMethod(object, type);
            } else if (this.isMineEntity(clazz)) {
                return this.getObjectValueByMethod(object, type);
            }
        }
        return null;
    }

    private Object getMapValueByMethod(Object object, String type) {
        if (object != null) {
            try {
                Map<String, Object> map = (Map<String, Object>) object;
                Set<String> keySet = map.keySet();
                if (keySet.size() > 0) {
                    Map<String, Object> result = new HashMap<>();
                    for (String key : keySet) {
                        Object val = map.get(key);
                        Object value = this.getValueByMethod(val, type);
                        if (this.hasPushVal(value, type)) {
                            result.put(key, value);
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Object getListValueByMethod(Object object, String type) {
        if (object != null) {
            List result = new ArrayList();
            try {
                List list = (List) object;
                for (Object val : list) {
                    Object value = this.getValueByMethod(val, type);
                    if (value != null) {
                        if (this.hasPushVal(value, type)) {
                            result.add(value);
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    private Object getArrayValueByMethod(Object object, String type) {
        if (object != null) {
            List result = new ArrayList();
            try {
                Object[] array = (Object[]) object;
                for (Object val : array) {
                    Object value = this.getValueByMethod(val, type);
                    if (value != null) {
                        if (this.hasPushVal(value, type)) {
                            result.add(value);
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    public Map<String, Object> getMapByField(Object object) {
        return this.getMapByField(object, EMPTY);
    }

    public Map<String, Object> getMapByField(Object object, String type) {
        // ALWAYS NULL EMPTY
        Object value = this.getObjectValueByField(object, type);
        if (value != null) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    private Object getObjectValueByField(Object object, String type) {
        List<Field> fields = this.getFilesByClass(object.getClass());
        if (fields != null && fields.size() > 0) {
            Map<String, Object> result = new HashMap<>();
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                // 只处理无参方法
                String name = field.getName();
                if (name != null && !this.isIgnore(field)) {
                    try {
                        field.setAccessible(true);
                        Object val = field.get(object);
                        Object value = this.getValueByField(val, type);
                        if (this.hasPushVal(value, type)) {
                            result.put(name, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return null;
    }

    private Object getValueByField(Object object, String type) {
        if (object != null) {
            Class<?> clazz = object.getClass();
            if (this.isSimpleType(clazz)) {
                return object;
            } else if (List.class.isAssignableFrom(clazz)) {
                return this.getListValueByField(object, type);
            } else if (Map.class.isAssignableFrom(clazz)) {
                return this.getMapValueByField(object, type);
            } else if (clazz.isArray()) {
                return this.getArrayValueByField(object, type);
            } else if (this.isMineEntity(clazz)) {
                return this.getObjectValueByField(object, type);
            }

        }
        return null;
    }

    private Object getMapValueByField(Object object, String type) {
        if (object != null) {
            try {
                Map<String, Object> map = (Map<String, Object>) object;
                Set<String> keySet = map.keySet();
                if (keySet.size() > 0) {
                    Map<String, Object> result = new HashMap<>();
                    for (String key : keySet) {
                        Object val = map.get(key);
                        Object value = this.getValueByField(val, type);
                        if (this.hasPushVal(value, type)) {
                            result.put(key, value);
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Object getListValueByField(Object object, String type) {
        if (object != null) {
            List result = new ArrayList();
            try {
                List list = (List) object;
                for (Object val : list) {
                    Object value = this.getValueByField(val, type);
                    if (value != null) {
                        if (this.hasPushVal(value, type)) {
                            result.add(value);
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    private Object getArrayValueByField(Object object, String type) {
        if (object != null) {
            List result = new ArrayList();
            try {
                Object[] array = (Object[]) object;
                for (Object val : array) {
                    Object value = this.getValueByField(val, type);
                    if (value != null) {
                        if (this.hasPushVal(value, type)) {
                            result.add(value);
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    private List<Field> getFilesByClass(Class<?> clazz) {
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

    private String getName(String methodName) {
        if (methodName.startsWith("get")) {
            return this.lowerFirst(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return this.lowerFirst(methodName.substring(2));
        }
        return null;
    }

    private String getMethodName(String name) {
        return "get" + this.upperFirst(name);
    }

    private String setMethodName(String name) {
        return "set" + this.upperFirst(name);
    }

    private String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return String.valueOf(chars);
    }

    private String upperFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

    private boolean hasPushVal(Object value, String type) {
        if (type != null) {
            if (type.equals(ALWAYS)) {
                return true;
            } else if (type.equals(NULL)) {
                if (value != null) {
                    return true;
                }
            } else if (type.equals(EMPTY)) {
                if (value != null) {
                    if (value instanceof String) {
                        if (((String) value).trim().length() > 0) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSimpleType(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        switch (simpleName) {
            case "Date":
            case "String":
            case "String[]":

            case "int":
            case "long":
            case "float":
            case "double":
            case "boolean":
            case "byte":
            case "short":
            case "char":

            case "int[]":
            case "long[]":
            case "float[]":
            case "double[]":
            case "boolean[]":
            case "byte[]":
            case "short[]":
            case "char[]":

            case "Integer":
            case "Long":
            case "Float":
            case "Double":
            case "Boolean":
            case "Byte":
            case "Short":
            case "Character":

            case "Integer[]":
            case "Long[]":
            case "Float[]":
            case "Double[]":
            case "Boolean[]":
            case "Byte[]":
            case "Short[]":
            case "Character[]":
                return true;
        }
        return false;
    }

    private boolean isMineEntity(Class<?> clazz) {
        if (clazz.isEnum() ||
                clazz.isInterface() ||
                clazz.isAnnotation() ||
                clazz.isPrimitive() ||
                clazz.isMemberClass() ||
                clazz.isAnonymousClass() ||
                clazz.isLocalClass() ||
                clazz.isSynthetic()) {
            return false;
        } else {
            // 是否是核心类库
            return clazz.getClassLoader() != null;
        }
    }

    private boolean isIgnore(Method method) {
        if (method.isAnnotationPresent(Ignore.class)) {
            return true;
        } else if (method.getName().equals("getClass")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isIgnore(Field field) {
        if (field.isAnnotationPresent(Ignore.class)) {
            return true;
        }
        return false;
    }

    public <T> T setEntityByMethod(Map<String, ?> params, Class<T> clazz) {
        try {
            Object instance = this.getEntityByMethod(params, clazz);
            if (instance != null) {
                return (T) instance;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    private Object getEntityByMethod(Map<String, ?> params, Class<?> clazz) {
        if (params != null) {
            Set<String> keySet = params.keySet();
            if (keySet.size() > 0) {
                try {
                    Object instance = clazz.newInstance();
                    for (String key : keySet) {
                        try {
                            Method getMethod = clazz.getMethod(this.getMethodName(key));
                            if (getMethod != null) {
                                Class<?> returnType = getMethod.getReturnType();
                                Method method = clazz.getMethod(this.setMethodName(key), returnType);
                                if (method != null) {
                                    Object object = params.get(key);
                                    if (object != null) {
                                        Class<?> clazzMethod = method.getParameterTypes()[0];
                                        Class<?> classType = this.getClassTypeByMethod(method);
                                        Object value = this.getValueByMethod(clazzMethod, classType, object);
                                        if (value != null) {
                                            method.invoke(instance, value);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                    return instance;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Object getValueByMethod(Class<?> clazz, Class<?> classType, Object object) throws Exception {
        if (Date.class.isAssignableFrom(clazz)) {
            if (object instanceof Long) {
                return new Date((Long) object);
            } else if (object instanceof Date) {
                return object;
            }
        } else if (this.isSimpleType(clazz)) {
            return object;
        } else if (clazz.isArray()) {
            // 暂不处理
            if (classType != null) {
                return this.getValueArrayByMethod(object, classType);
            }
        } else if (List.class.isAssignableFrom(clazz)) {
            if (object instanceof List) {
                if (classType != null) {
                    return this.getValueListByMethod(object, classType);
                }
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            if (object instanceof Map) {
                if (classType != null) {
                    return this.getValueMapByMethod(object, classType);
                }
            }
        } else if (this.isMineEntity(clazz)) {
            if (object instanceof Map) {
                return this.getEntityByMethod((Map) object, clazz);
            }
        }
        return null;
    }

    private Object getValueArrayByMethod(Object object, Class<?> clazz) {
        if (object != null) {
            try {
                Object[] array = (Object[]) object;
                if (array.length > 0) {
                    List result = new ArrayList();
                    Class<?> typeByClass = getClassTypeByClass(clazz);
                    for (Object item : array) {
                        Object value = this.getValueByMethod(clazz, typeByClass, item);
                        result.add(value);
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Object getValueMapByMethod(Object object, Class<?> clazz) {
        if (object != null) {
            try {
                Map<String, Object> map = (Map<String, Object>) object;
                Set<String> keySet = map.keySet();
                if (keySet.size() > 0) {
                    Map<String, Object> result = new HashMap<>();
                    Class<?> typeByClass = getClassTypeByClass(clazz);
                    for (String key : keySet) {
                        Object val = map.get(key);
                        Object value = this.getValueByMethod(clazz, typeByClass, val);
                        result.put(key, value);
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Object getValueListByMethod(Object object, Class<?> clazz) {
        if (object != null) {
            try {
                List list = (List) object;
                if (list.size() > 0) {
                    List result = new ArrayList();
                    Class<?> typeByClass = getClassTypeByClass(clazz);
                    for (Object item : list) {
                        Object value = this.getValueByMethod(clazz, typeByClass, item);
                        result.add(value);
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    public <T> T setEntityByField(Map<String, ?> params, Class<T> clazz) {
        try {
            Object instance = this.getEntityByField(params, clazz);
            if (instance != null) {
                return (T) instance;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    private Object getEntityByField(Map<String, ?> params, Class<?> clazz) {
        if (params != null) {
            Set<String> keySet = params.keySet();
            if (keySet.size() > 0) {
                try {
                    Object instance = clazz.newInstance();
                    for (String key : keySet) {
                        try {
                            Field field = clazz.getDeclaredField(key);
                            if (field != null) {
                                Object object = params.get(key);
                                if (object != null) {
                                    field.setAccessible(true);
                                    Class<?> clazzFiled = field.getType();
                                    Class<?> classType = this.getClassTypeByField(field);
                                    Object value = this.getValueByField(clazzFiled, classType, object);
                                    if (value != null) {
                                        field.set(instance, value);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                    return instance;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Object getValueByField(Class<?> clazz, Class<?> classType, Object object) throws Exception {
        if (Date.class.isAssignableFrom(clazz)) {
            if (object instanceof Long) {
                return new Date((Long) object);
            } else if (object instanceof Date) {
                return object;
            }
        } else if (this.isSimpleType(clazz)) {
            return object;
        } else if (clazz.isArray()) {
            // 暂不处理
            if (classType != null) {
                return this.getValueArrayByField(object, classType);
            }
        } else if (List.class.isAssignableFrom(clazz)) {
            if (object instanceof List) {
                if (classType != null) {
                    return this.getValueListByField(object, classType);
                }
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            if (object instanceof Map) {
                if (classType != null) {
                    return this.getValueMapByField(object, classType);
                }
            }
        } else if (this.isMineEntity(clazz)) {
            if (object instanceof Map) {
                return this.getEntityByField((Map) object, clazz);
            }
        }
        return null;
    }

    private Object getValueArrayByField(Object object, Class<?> clazz) {
        if (object != null) {
            try {
                Object[] array = (Object[]) object;
                if (array.length > 0) {
                    List result = new ArrayList();
                    Class<?> typeByClass = getClassTypeByClass(clazz);
                    for (Object item : array) {
                        Object value = this.getValueByField(clazz, typeByClass, item);
                        result.add(value);
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Object getValueMapByField(Object object, Class<?> clazz) {
        if (object != null) {
            try {
                Map<String, Object> map = (Map<String, Object>) object;
                Set<String> keySet = map.keySet();
                if (keySet.size() > 0) {
                    Map<String, Object> result = new HashMap<>();
                    Class<?> typeByClass = getClassTypeByClass(clazz);
                    for (String key : keySet) {
                        Object val = map.get(key);
                        Object value = this.getValueByField(clazz, typeByClass, val);
                        result.put(key, value);
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Object getValueListByField(Object object, Class<?> clazz) {
        if (object != null) {
            try {
                List list = (List) object;
                if (list.size() > 0) {
                    List result = new ArrayList();
                    Class<?> typeByClass = getClassTypeByClass(clazz);
                    for (Object item : list) {
                        Object value = this.getValueByField(clazz, typeByClass, item);
                        result.add(value);
                    }
                    return result;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    private Class<?> getClassTypeByMethod(Method method) {
        Type[] types = method.getGenericParameterTypes();
        if (types != null && types.length > 0) {
            Type type = types[0];
            if (type != null) {
                if (type instanceof ParameterizedType) {
                    Type[] classTypes = ((ParameterizedType) type).getActualTypeArguments();
                    if (classTypes != null && classTypes.length > 0) {
                        Type classType = classTypes[0];
                        if (classType != null) {
                            return (Class<?>) classType;
                        }
                    }
                } else if (type instanceof Class) {
                    return (Class<?>) type;
                }
            }
        }
        return null;
    }

    private Class<?> getClassTypeByClass(Class clazz) {
        Type type = clazz.getGenericSuperclass();
        if (type != null) {
            if (type instanceof ParameterizedType) {
                Type[] classTypes = ((ParameterizedType) type).getActualTypeArguments();
                if (classTypes != null && classTypes.length > 0) {
                    Type classType = classTypes[0];
                    if (classType != null) {
                        return (Class<?>) classType;
                    }
                }
            } else if (type instanceof Class) {
                return (Class<?>) type;
            }
        }
        return null;
    }

    private Class getClassTypeByField(Field field) {
        Type type = field.getGenericType();
        if (type != null) {
            if (type instanceof ParameterizedType) {
                Type[] classTypes = ((ParameterizedType) type).getActualTypeArguments();
                if (classTypes != null && classTypes.length > 0) {
                    Type classType = classTypes[0];
                    if (classType != null) {
                        return (Class<?>) classType;
                    }
                }
            } else if (type instanceof Class) {
                return (Class<?>) type;
            }
        }
        return null;
    }
}
