package org.origin.centres.config;

import org.origin.centres.content.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

/**
 * @author zhangjie
 * @version 2019-10-18
 * @apiNote 自动加载配置;
 */
@Configuration
public class IAutoLoadConfig {

    @Bean
    @Lazy(false)
    @Order(-2147481648)
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
