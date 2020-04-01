package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfigProfile;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringConfigProfileTest {

    public static void main(String[] args) {

        //1.配置jvm启动参数 -Dspring.profiles.active=dev
//        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfigProfile.class);
        //2.代码配置方式
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        //设置需要激活的环境
        context.getEnvironment().setActiveProfiles("test","dev");
        //注册配置类
        context.register(SpringConfigProfile.class);
        //启动刷新容器
        context.refresh();

        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name:beanDefinitionNames){
            System.out.println(name);
        }
    }
}
