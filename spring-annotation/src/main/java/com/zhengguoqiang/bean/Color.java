package com.zhengguoqiang.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @author zhengguoqiang
 */
@Component
public class Color implements ApplicationContextAware, EmbeddedValueResolverAware, BeanNameAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("注入ApplicationContext");
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        System.out.println("注入StringValueResolver");
        String s = resolver.resolveStringValue("系统：${os.name},版本：#{20-2}");
        System.out.println(s);
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("传入BeanName");
        System.out.println("Bean Name is :" + name);
    }
}
