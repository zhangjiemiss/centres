package org.origin.centres.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.reflection.MetaObject;
import org.origin.centres.interfaces.IPreHandle;

/**
 * @author zhangjie
 * @version 2019-06-18
 * @apiNote Mybatis 元对象字段填充控制器,处理更新和插入之前的操作
 */
public class MyObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object et;
        if (metaObject.hasGetter(Constants.ENTITY)) {
            et = metaObject.getValue(Constants.ENTITY);
        } else {
            et = metaObject.getOriginalObject();
        }
        if (et instanceof IPreHandle) {
            IPreHandle handle = (IPreHandle) et;
            handle.preInsert();
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object et;
        if (metaObject.hasGetter(Constants.ENTITY)) {
            et = metaObject.getValue(Constants.ENTITY);
        } else {
            et = metaObject.getOriginalObject();
        }
        if (et instanceof IPreHandle) {
            IPreHandle handle = (IPreHandle) et;
            handle.preUpdate();
        }
    }
}
