package com.zhengguoqiang.ext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("MyBeanFactoryPostProcessor...postProcessBeanFactory start execute...");
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
        System.out.println("Bean Definition的数量：" + beanDefinitionCount);
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("Bean Definition Names:" + Arrays.asList(beanDefinitionNames));
    }
}
