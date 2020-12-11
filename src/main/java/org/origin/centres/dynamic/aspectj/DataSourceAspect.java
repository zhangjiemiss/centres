package org.origin.centres.dynamic.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.origin.centres.dynamic.anno.IDataSource;
import org.origin.centres.dynamic.holder.DynamicDataSourceHolder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

/**
 * @author zhangjie
 * @version 2020-07-06
 * @apiNote 动态数据源切换处理
 */
@Aspect
@Order(1)
public class DataSourceAspect {

    @Around("@annotation(org.origin.centres.dynamic.anno.IDataSource)|| @within(org.origin.centres.dynamic.anno.IDataSource)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取需要切换的数据源注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        IDataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), IDataSource.class);
        dataSource = dataSource != null ? dataSource : AnnotationUtils.findAnnotation(signature.getDeclaringType(), IDataSource.class);
        if (dataSource != null) {
            DynamicDataSourceHolder.setDataSource(dataSource.value());
        }
        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceHolder.clearDataSource();
        }
    }

}
