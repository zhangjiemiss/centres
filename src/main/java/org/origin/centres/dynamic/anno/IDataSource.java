package org.origin.centres.dynamic.anno;

import java.lang.annotation.*;

/**
 * @author zhangjie
 * @version 2020-07-06
 * @apiNote 动态数据源注解 （优先级：先方法，后类，如果方法覆盖了类上的数据源类型，以方法的为准，否则以类上的为准）
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IDataSource {

    String value() default "default";

}
