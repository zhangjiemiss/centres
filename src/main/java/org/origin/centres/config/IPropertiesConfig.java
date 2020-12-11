package org.origin.centres.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * @author zhangjie
 * @version 2019-10-18
 * @apiNote 自定义config private DefaultListableBeanFactory beanFactory;
 */
@SuppressWarnings("unused")
public interface IPropertiesConfig {

    // 加载YML格式自定义配置文件
    default PropertySourcesPlaceholderConfigurer loadYml(String... paths) {
        if (paths != null && paths.length > 0) {
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
            ClassPathResource[] resources = new ClassPathResource[paths.length];
            for (int i = 0; i < paths.length; i++) {
                resources[i] = new ClassPathResource(paths[i]);
            }
            yml.setResources(resources);//File引入
            //yml.setResources(new ClassPathResource("classpath:wexinfo.yml"));//class引入
            configurer.setProperties(yml.getObject());
            return configurer;
        }
        return null;
    }
}
