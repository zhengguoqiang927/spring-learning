package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfigPropertyValues;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author zhengguoqiang
 */
public class SpringConfigPropertyValuesTest {

    @Test
    public void test1(){
        ApplicationContext context =
                new AnnotationConfigApplicationContext(SpringConfigPropertyValues.class);

        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name:beanDefinitionNames){
            System.out.println(name);
        }

        Person person = context.getBean(Person.class);
        System.out.println(person);

        Environment environment = context.getEnvironment();
        String property = environment.getProperty("person.nickName");
        System.out.println(property);
    }
}
