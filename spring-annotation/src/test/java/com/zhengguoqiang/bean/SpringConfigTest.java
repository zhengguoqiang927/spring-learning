package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfig;
import com.zhengguoqiang.config.SpringConfig2;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Map;

/**
 * @author zhengguoqiang
 */
public class SpringConfigTest {

    @Test
    public void testUseDefaultFilter(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        System.out.println("容器初始化完成...");
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name:beanDefinitionNames){
            System.out.println(name);
        }
    }

    @Test
    public void testOne(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig2.class);
        System.out.println("容器初始化完成...");
        /*String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name:beanDefinitionNames){
            System.out.println(name);
        }*/
        Person bean1 = context.getBean(Person.class);
        Person bean2 = context.getBean(Person.class);
        System.out.println(bean1 == bean2);
    }

    @Test
    public void testConditional(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig2.class);
        Environment environment = context.getEnvironment();
        String property = environment.getProperty("os.name");
        System.out.println(property);

        String[] beanNamesForType = context.getBeanNamesForType(Person.class);
        for (String name : beanNamesForType) {
            System.out.println(name);
        }

        Map<String, Person> beansOfType = context.getBeansOfType(Person.class);
        System.out.println(beansOfType);
    }

    @Test
    public void testImport(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig2.class);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames).forEach(System.out::println);

        Object colorFactoryBean = context.getBean("colorFactoryBean");
        System.out.println(colorFactoryBean.getClass());

        Object bean = context.getBean("&colorFactoryBean");
        System.out.println(bean.getClass());
    }
}