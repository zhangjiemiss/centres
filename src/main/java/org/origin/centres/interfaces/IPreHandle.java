package org.origin.centres.interfaces;

import java.io.Serializable;

/**
 * @author zhangjie
 * @version 2019-07-13
 * @apiNote 预处理
 */
@SuppressWarnings("unused")
public interface IPreHandle extends Serializable {
    /**
     * 插入预处理
     */
    default void preInsert() {
    }

    /**
     * 更新预处理
     */
    default void preUpdate() {
    }
}
