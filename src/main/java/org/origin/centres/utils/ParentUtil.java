package org.origin.centres.utils;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.origin.centres.constants.IConstant;
import org.origin.centres.content.SpringContextHolder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjie
 * @version 2018-06-29
 * @apiNote 父关系工具
 */
@SuppressWarnings({"ALL","unchecked"})
public class ParentUtil {
    // org.mybatis.spring.SqlSessionTemplate;
    private static JdbcTemplate jdbcTemplate = SpringContextHolder.getBean(JdbcTemplate.class);

    /**
     * 预处理上级
     *
     * @param source 待处理数据
     * @param parent 上级数据
     */
    public static void preParent(Object source, Object parent) {
        if (source != null) {
            try {
                Class clazz = source.getClass();
                Method getId = clazz.getMethod("getId");
                Method getParentId = clazz.getMethod("getParentId");
                Method getParentIds = clazz.getMethod("getParentIds");
                Method setParentId = clazz.getMethod("setParentId", String.class);
                Method setParentIds = clazz.getMethod("setParentIds", String.class);
                existParent(source, parent, getParentId);
                if (parent != null) {
                    Object IdOfParent = getId.invoke(parent);
                    if (IdOfParent != null && IdOfParent.toString().trim().length() > 0) {
                        String parentId = IdOfParent.toString().trim();
                        String newParentIds = null;
                        Object ParentParentIds = getParentIds.invoke(parent);
                        String parentParentIds = ParentParentIds != null ? ParentParentIds.toString().trim() : null;
                        if (parentParentIds != null && parentParentIds.length() > 0) {
                            newParentIds = parentParentIds + "," + parentId;
                        }
                        if (newParentIds == null || newParentIds.trim().length() <= 0) {
                            throw new IllegalArgumentException("parentIds does not exist");
                        }
                        setParentId.invoke(source, parentId);
                        setParentIds.invoke(source, newParentIds);
                    } else {
                        throw new IllegalArgumentException("parent does not exist");
                    }
                } else {
                    Object parentIdOfSource = getParentId.invoke(source);
                    String parentId = parentIdOfSource.toString().trim();
                    setParentIds.invoke(source, parentId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 预处理状态
     *
     * @param source 待处理数据
     * @param parent 上级数据
     */
    public static void preStatus(Object source, Object parent) {
        if (source != null) {
            try {
                Class clazz = source.getClass();
                Method getId = clazz.getMethod("getId");
                Method getStatus = clazz.getMethod("getStatus");
                Method setStatus = clazz.getMethod("setStatus", Integer.class);
                Method getParentId = clazz.getMethod("getParentId");
                existParent(source, parent, getParentId);
                if (parent != null) {
                    Object IdOfParent = getId.invoke(parent);
                    if (IdOfParent != null) {
                        String parentId = IdOfParent.toString();
                        if (parentId.trim().length() > 0) {
                            Object StatusOfParent = getStatus.invoke(parent);
                            if (StatusOfParent != null && StatusOfParent.toString().trim().length() > 0) {

                                Object Status = getStatus.invoke(source);
                                if (Status == null) {
                                    // 未设置状态默认设置为启用状态
                                    setStatus.invoke(source, IConstant.Usable);
                                }
                                Status = getStatus.invoke(source);
                                String status = Status.toString();
                                String pStatus = StatusOfParent.toString();

                                if (pStatus.equals(IConstant.Usable.toString())) {
                                    // 上级为启用状态
                                    if (status.equals(IConstant.Useful.toString())) {
                                        // 有用状态修改为启用状态
                                        setStatus.invoke(source, IConstant.Usable);
                                    } else if (status.equals(IConstant.Useless.toString())) {
                                        // 无用状态修改为禁用状态
                                        setStatus.invoke(source, IConstant.Disable);
                                    }
                                } else if (pStatus.equals(IConstant.Disable.toString()) ||
                                        pStatus.equals(IConstant.Useful.toString()) ||
                                        pStatus.equals(IConstant.Useless.toString())) {
                                    // 上级为禁用状态
                                    if (status.equals(IConstant.Usable.toString())) {
                                        // 启用状态修改为有用状态
                                        setStatus.invoke(source, IConstant.Useful);
                                    } else if (status.equals(IConstant.Disable.toString())) {
                                        // 禁用状态修改为无用状态
                                        setStatus.invoke(source, IConstant.Useless);
                                    }
                                } else {
                                    throw new IllegalArgumentException("parent status unknown");
                                }
                            } else {
                                throw new IllegalArgumentException("parent status unknown");
                            }
                        } else {
                            throw new IllegalArgumentException("parent does not exist");
                        }
                    } else {
                        throw new IllegalArgumentException("parent does not exist");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理上级
     *
     * @param source 待处理数据
     */
    public static void afterParent(Object source) {
        afterParent(source, null);
    }

    /**
     * 处理上级
     *
     * @param source 待处理数据
     */
    public static void afterParent(Object source, String tableName) {
        if (source != null) {
            try {
                Class clazz = source.getClass();
                tableName = getTableName(clazz, tableName);
                Method getId = clazz.getMethod("getId");
                Method getParentIds = clazz.getMethod("getParentIds");
                Object Id = getId.invoke(source);
                String id = Id != null ? Id.toString().trim() : null;
                Object ParentIds = getParentIds.invoke(source);
                String parentIds = ParentIds != null ? ParentIds.toString().trim() : null;
                if (id != null && parentIds != null && id.length() > 0 && parentIds.length() > 0) {
                    String sql = String.format("UPDATE `%s` " +
                            "SET `parent_ids` = CONCAT('%s',',','%s',REPLACE(SUBSTRING(`parent_ids`, LOCATE(CONCAT(',','%s',','),`parent_ids`)),CONCAT(',','%s',','),',')) " +
                            "WHERE FIND_IN_SET('%s',`parent_ids`)", tableName, parentIds, id, id, id, id);
                    jdbcTemplate.update(sql);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理状态
     *
     * @param source 待处理数据
     */
    public static void afterStatus(Object source) {
        afterStatus(source, null);
    }

    /**
     * 处理状态
     *
     * @param source    待处理数据
     * @param tableName 表名称
     */
    public static void afterStatus(Object source, String tableName) {
        if (source != null) {
            try {
                Class clazz = source.getClass();
                tableName = getTableName(clazz, tableName);
                Method getId = clazz.getMethod("getId");
                Method getStatus = clazz.getMethod("getStatus");
                Object Id = getId.invoke(source);
                String id = Id != null ? Id.toString().trim() : null;
                Object Status = getStatus.invoke(source);
                String status = Status != null ? Status.toString().trim() : null;
                if (id != null && status != null && id.length() > 0 && status.length() > 0) {
                    if (status.equals(IConstant.Usable.toString())) {
                        // String sql = String.format("UPDATE `%s` " +
                        //         "        SET `status` = IF(`status` IN(0,2),0,1)" +
                        //         "        WHERE FIND_IN_SET('%s',`parent_ids`)", tableName, id);
                        // jdbcTemplate.update(sql);
                        // String sql2 = String.format("UPDATE `%s` a" +
                        //        "        INNER JOIN `%s` b ON FIND_IN_SET(b.`id`,a.`parent_ids`) AND b.`status` IN(1,3) " +
                        //         "        SET a.`status` = IF(a.`status` IN(0,2),2,3) " +
                        //         "        WHERE FIND_IN_SET('%s',a.`parent_ids`)", tableName, tableName, id);
                        // jdbcTemplate.update(sql2);
                        String sql = String.format("UPDATE `%s` a" +
                                "        LEFT JOIN `%s` b ON FIND_IN_SET(b.`id`,a.`parent_ids`) AND b.`status` IN(1,3) " +
                                "        LEFT JOIN `%s` c ON FIND_IN_SET(b.`id`,c.`parent_ids`) " +
                                "        SET a.`status` = IF(a.`status` IN(0,2),0,1), c.`status` = IF(c.`status` IN(0,2),2,3)" +
                                "        WHERE FIND_IN_SET('%s',a.`parent_ids`)", tableName, tableName, tableName, id);
                        jdbcTemplate.update(sql);
                    } else {
                        String sql = String.format("UPDATE `%s` " +
                                "        SET `status` = IF(`status` IN(0,2),2,3) " +
                                "        WHERE FIND_IN_SET('%s',`parent_ids`)", tableName, id);
                        jdbcTemplate.update(sql);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取子列表，只有所有字段设置规则都是下划线转驼峰才能使用
     *
     * @param entities 上级数据
     * @param <T>      数据泛型
     * @return 下级数据
     */
    public static <T> List<T> getChildrens(List<T> entities, String orderBy) {
        if (entities != null && entities.size() > 0) {
            Class clazz = null;
            Field idField = null;
            StringBuilder builder = new StringBuilder();
            for (T entity : entities) {
                if (entity != null) {
                    String id = null;
                    if (clazz == null) {
                        clazz = entity.getClass();
                        List<Field> fields = AliveUtil.getFilesByClass(clazz);
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(TableId.class)) {
                                idField = field;
                            }
                        }
                    }
                    if (clazz != null && idField != null) {
                        try {
                            Object ID = idField.get(entity);
                            if (ID != null) {
                                id = ID.toString().trim();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (id != null && id.length() > 0) {
                        if (builder.length() > 0) {
                            builder.append(" OR");
                        }
                        builder.append(String.format(" FIND_IN_SET('%s',`parent_ids`)", id));
                    }
                }
            }
            String tableName = getTableName(clazz, null);
            builder.insert(0, String.format("SELECT * FROM %s WHERE ", tableName));
            if (orderBy != null) {
                builder.append(" ");
                builder.append(orderBy);
            }
            try {
                List<T> children = jdbcTemplate.query(builder.toString(), new Object[]{}, new BeanPropertyRowMapper<>(clazz));
                if (children != null && children.size() > 0) {
                    entities.addAll(children);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entities;
    }

    /**
     * 通过 names 或者实体注解获取表名称
     *
     * @param clazz     实体class
     * @param tableName 表名称
     * @return 表名称
     */
    private static String getTableName(Class clazz, String tableName) {
        if (tableName == null) {
            if (clazz.isAnnotationPresent(TableName.class)) {
                TableName annotation = (TableName) clazz.getAnnotation(TableName.class);
                tableName = annotation.value();
                if (tableName != null && tableName.trim().length() > 0) {
                    tableName = tableName.trim();
                }
            }
        }
        return tableName;
    }

    /**
     * 判断是否存在上级
     *
     * @param source      当前
     * @param parent      上级
     * @param getParentId 获取parentId
     * @throws Exception 异常信息
     */
    private static void existParent(Object source, Object parent, Method getParentId) throws Exception {
        Object parentIdOfSource = getParentId.invoke(source);
        if (parentIdOfSource == null) {
            throw new IllegalArgumentException("parent does not exist");
        }
        if (parentIdOfSource != null && parent == null) {
            String parentId = parentIdOfSource.toString().trim();
            if (!parentId.equals(IConstant.TopPid.toString())) {
                throw new IllegalArgumentException("parent does not exist");
            }
        }
    }

    /**
     * 组装并获取树形结构数据
     *
     * @param list-map 传入列表数据
     * @param rootIds  参数集 [id] [parent_id] [children] [rootIds]
     * @return 树形结构数据
     */
    public static List<Map> gainTreeByMap(List<Map> list, String... rootIds) {
        return getTreeByMap(list, "id", "parentId", "children", rootIds != null && rootIds.length > 0 ? rootIds[0] : null);
    }

    /**
     * 组装并获取树形结构数据
     *
     * @param list-map 传入列表数据
     * @param params   参数集 [id] [parent_id] [children] [rootIds]
     * @return 树形结构数据
     */
    public static List<Map> getTreeByMap(List<Map> list, String... params) {
        List<Map> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            // 取值
            String keyId = params != null && params.length > 0 ? params[0] : null;
            String keyPid = params != null && params.length > 1 ? params[1] : null;
            String keyChild = params != null && params.length > 2 ? params[2] : null;
            String rootIds = params != null && params.length > 3 ? params[3] : null;
            // 初始化
            keyId = keyId != null && keyId.trim().length() > 0 ? keyId : "id";
            keyPid = keyPid != null && keyPid.trim().length() > 0 ? keyPid : "parent_id";
            keyChild = keyChild != null && keyChild.trim().length() > 0 ? keyChild : "children";
            rootIds = rootIds != null && rootIds.trim().length() > 0 ? (',' + rootIds + ',') : null;
            boolean hasRoot = rootIds != null && rootIds.trim().length() > 0;
            Map<String, Map> temp = new HashMap<>();
            for (Map map : list) {
                Object ID = map.get(keyId);
                if (ID != null) {
                    String id = String.valueOf(ID);
                    map.put(keyId, id);
                    temp.put(id, map);
                }
            }
            for (Map map : list) {
                Object PID = map.get(keyPid);
                if (PID != null) {
                    String pid = String.valueOf(PID);
                    if (pid.trim().length() > 0) {
                        String xPid = ',' + pid + ',';
                        // 如果设置rootId，那么要更加rootId为顶级ID
                        if (hasRoot) {
                            if (rootIds.contains(xPid)) {
                                result.add(map);
                            } else {
                                Map parent = temp.get(pid);
                                if (parent != null) {
                                    Object CHILDREN = parent.get(keyChild);
                                    if (CHILDREN == null) CHILDREN = new ArrayList<Map<String, Object>>();
                                    List<Map<String, Object>> children = (List<Map<String, Object>>) CHILDREN;
                                    children.add(map);
                                    parent.put(keyChild, children);
                                }
                            }
                        } else {
                            Map parent = temp.get(pid);
                            if (parent != null) {
                                Object CHILDREN = parent.get(keyChild);
                                if (CHILDREN == null) CHILDREN = new ArrayList<Map<String, Object>>();
                                List<Map<String, Object>> children = (List<Map<String, Object>>) CHILDREN;
                                children.add(map);
                                parent.put(keyChild, children);
                            } else {
                                result.add(map);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 组装并获取树形结构数据
     *
     * @param list-T 传入列表数据
     * @param params 参数集 [id] [parent_id] [children] [rootIds]
     * @param <T>    真数据泛型
     * @return 树形结构数据
     */
    public static <T> List<T> getTreeByEntity(List<T> list, String... params) {
        List<T> result = new ArrayList<>();
        try {
            if (list != null && list.size() > 0) {
                // 取值
                String keyId = params != null && params.length > 0 ? params[0] : null;
                String keyPid = params != null && params.length > 1 ? params[1] : null;
                String keyChild = params != null && params.length > 2 ? params[2] : null;
                String rootIds = params != null && params.length > 3 ? params[3] : null;
                // 初始化
                keyId = keyId != null && keyId.trim().length() > 0 ? keyId : "id";
                keyPid = keyPid != null && keyPid.trim().length() > 0 ? keyPid : "parentId";
                keyChild = keyChild != null && keyChild.trim().length() > 0 ? keyChild : "children";
                rootIds = rootIds != null && rootIds.trim().length() > 0 ? (',' + rootIds + ',') : null;
                boolean hasRoot = rootIds != null && rootIds.trim().length() > 0;
                // 取反射
                Class<?> clazz = list.get(0).getClass();
                keyId = Character.toUpperCase(keyId.charAt(0)) + keyId.substring(1);
                keyPid = Character.toUpperCase(keyPid.charAt(0)) + keyPid.substring(1);
                keyChild = Character.toUpperCase(keyChild.charAt(0)) + keyChild.substring(1);
                // 获取方法
                Method keyIdGetMethod = clazz.getMethod("get" + keyId);
                Method keyPidGetMethod = clazz.getMethod("get" + keyPid);
                Method keyChildGetMethod = clazz.getMethod("get" + keyChild);
                Method keyChildSetMethod = clazz.getMethod("set" + keyChild, List.class);

                Map<String, T> temp = new HashMap<>();
                for (T object : list) {
                    if (object != null) {
                        Object ID = keyIdGetMethod.invoke(object);
                        if (ID != null) {
                            String id = String.valueOf(ID);
                            temp.put(id, object);
                        }
                    }
                }
                for (T object : list) {
                    Object PID = keyPidGetMethod.invoke(object);
                    if (PID != null) {
                        String pid = String.valueOf(PID);
                        if (pid.trim().length() > 0) {
                            String xPid = ',' + pid + ',';
                            // 如果设置rootId，那么要更加rootId为顶级ID
                            if (hasRoot) {
                                if (rootIds.contains(xPid)) {
                                    result.add(object);
                                } else {
                                    T parent = temp.get(pid);
                                    if (parent != null) {
                                        Object CHILDREN = keyChildGetMethod.invoke(parent);
                                        if (CHILDREN == null) CHILDREN = new ArrayList<T>();
                                        List<T> children = (List<T>) CHILDREN;
                                        children.add(object);
                                        keyChildSetMethod.invoke(parent, children);
                                    }
                                }
                            } else {
                                T parent = temp.get(pid);
                                if (parent != null) {
                                    Object CHILDREN = keyChildGetMethod.invoke(parent);
                                    if (CHILDREN == null) CHILDREN = new ArrayList<T>();
                                    List<T> children = (List<T>) CHILDREN;
                                    children.add(object);
                                    keyChildSetMethod.invoke(parent, children);
                                } else {
                                    result.add(object);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将树形结构数据转为非树形结构数据
     *
     * @param tree 树形结构数据
     * @return 非树形结构数据
     */
    public static List<Map> getListByTree(List<Map> tree, String... params) {
        List<Map> result = new ArrayList<>();
        if (tree != null && tree.size() > 0) {
            // 取值
            String keyChild = params != null && params.length > 0 ? params[0] : null;
            // 初始化
            keyChild = keyChild != null && keyChild.trim().length() > 0 ? keyChild : "children";
            for (int i = 0; i < tree.size(); i++) {
                Map item = tree.get(i);
                if (item != null) {
                    result.add(item);
                    Object children = item.get(keyChild);
                    if (children != null) {
                        List<Map> mList = getListByTree((List<Map>) children, params);
                        if (mList != null && mList.size() > 0) {
                            result.addAll(mList);
                        }
                    }
                    item.remove(keyChild);
                }
            }

        }
        return result;
    }

    /**
     * 将树形结构数据转为非树形结构数据
     *
     * @param tree 树形结构数据
     * @return 非树形结构数据
     */
    public static <T> List<T> getListWithTree(List<T> tree, String... params) {
        List<T> result = new ArrayList<>();
        if (tree != null && tree.size() > 0) {
            try {
                // 取值
                String keyChild = params != null && params.length > 0 ? params[0] : null;
                // 初始化
                keyChild = keyChild != null && keyChild.trim().length() > 0 ? keyChild : "children";
                // 取反射
                keyChild = Character.toUpperCase(keyChild.charAt(0)) + keyChild.substring(1);
                // 获取方法
                Class<?> clazz = tree.get(0).getClass();
                Method keyChildGetMethod = clazz.getMethod("get" + keyChild);
                Method keyChildSetMethod = clazz.getMethod("set" + keyChild, List.class);
                for (int i = 0; i < tree.size(); i++) {
                    T item = tree.get(i);
                    if (item != null) {
                        result.add(item);
                        Object CHILDREN = keyChildGetMethod.invoke(item);
                        if (CHILDREN != null) {
                            List<T> mList = getListWithTree((List<T>) CHILDREN, params);
                            if (mList != null && mList.size() > 0) {
                                result.addAll(mList);
                            }
                            keyChildSetMethod.invoke(item, new ArrayList<T>());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
