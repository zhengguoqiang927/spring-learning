package com.zhengguoqiang.bean;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhengguoqiang
 */
public class SpringConfigTest {

    @Test
    public void testOne(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name:beanDefinitionNames){
            System.out.println(name);
        }
    }
}