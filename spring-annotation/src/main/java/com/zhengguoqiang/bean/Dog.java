package com.zhengguoqiang.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author zhengguoqiang
 */
@Component
public class Dog implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Dog() {
        System.out.println("dog...constructor...");
    }

    //对象创建并赋值后调用
    @PostConstruct
    public void init(){
        System.out.println("dog...@PostConstruct...");
    }

    //容器销毁bean之前调用
    @PreDestroy
    public void destroy(){
        System.out.println("dog...@PreDestroy...");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
