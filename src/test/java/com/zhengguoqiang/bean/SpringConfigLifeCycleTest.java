package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfigLifeCycle;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhengguoqiang
 */
public class SpringConfigLifeCycleTest {

    @Test
    public void test1(){
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(SpringConfigLifeCycle.class);

        System.out.println("容器创建完成");
//        applicationContext.getBean("car");

        ((AnnotationConfigApplicationContext) applicationContext).close();
    }
}
