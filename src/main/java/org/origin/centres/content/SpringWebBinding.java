package org.origin.centres.content;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;

import java.beans.PropertyEditorSupport;

/**
 * @author zhangjie
 * @version 2018-11-01
 * @apiNote WebBindingInitializer 全局的数据绑定(未使用)
 * 查看适配器 {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter}
 */
public class SpringWebBinding extends ConfigurableWebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        // 转换空字符串
        //binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
            }

            @Override
            public String getAsText() {
                return getValue() != null ? getValue().toString() : "";
            }
        });
    }

    // 加入适配器
    // @Bean
    // public ConfigurableWebBindingInitializer webBindingInitializer(){
    //     return new SpringWebBinding();
    // }
}
