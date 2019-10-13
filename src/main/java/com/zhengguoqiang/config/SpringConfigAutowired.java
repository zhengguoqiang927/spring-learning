package com.zhengguoqiang.config;

import com.zhengguoqiang.bean.Boss;
import com.zhengguoqiang.bean.Car;
import com.zhengguoqiang.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 自动装配：
 *      Spring利用依赖注入（DI），完成对IOC容器中各个组件的依赖关系赋值；
 *
 *
 * 1. @Autowired 自动注入：
 *      1. 默认优先按照类型去容器中查找组件：applicationContext.getBean(BookDao.class);
 *      2. 如果找到多个相同类型的组件时，在将属性名作为组件的ID去容器中查找：applicationContext.getBean("bookDao");
 *      3. @Qualifier("bookDao"):使用@Qualifier指定需要装配的组件的ID，而不使用属性名
 *      4. 自动装配默认一定要将属性装配好，没有就会报错；可以采用@Autowired(required=false)来避免强制装配
 *      5. @Primary Spring自动装配的时候，首选的bean，优先级低于@Qualifier
 *
 * 2. Spring还支持@Resource（JSR250）和@Inject（JSR330）【java规范的注解】
 *      @Resource ：
 *          可以和@Autowired一样实现自动装配功能，默认是按照组件名称进行装配的
 *          不支持@Primary、@Qualifier注解也不支持@Autowired的require=false功能
 *      @Inject ：
 *          需要导入javax.inject包,和@Autowired的功能一样。只不过没有require=false的功能。
 *
 * @Autowired：Spring定义的；@Resource、@Inject是java规范
 *
 * AutowiredAnnotationBeanPostProcessor：上面三个注解都是由这个处理器完成的。
 *
 * 3. @Autowired：构造器、参数、方法、属性：都是从容器中获取参数组件的值
 *       1. 标注在方法上：@Bean + 方法参数：参数从容器中获取；默认不写@Autowired效果是一样的；都能自动装配
 *       2. 标在构造器上：如果组件只有一个有参构造器，这个有参构造器的@Autowired可以省略
 *       3. 标在方法参数上：
 *
 * @author zhengguoqiang
 */

@ComponentScan({"com.zhengguoqiang.service",
        "com.zhengguoqiang.dao","com.zhengguoqiang.controller",
        "com.zhengguoqiang.bean"})
@Configuration
public class SpringConfigAutowired {

    @Primary
    @Bean("bookDao2")
    public BookDao bookDao(){
        BookDao bookDao = new BookDao();
        bookDao.setLabel("2");
        return bookDao;
    }

    @Bean
    public Boss boss(Car car){
        Boss boss = new Boss();
        boss.setCar(car);
        return boss;
    }
}
