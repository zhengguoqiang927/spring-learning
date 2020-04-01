package com.zhengguoqiang.config;

import com.zhengguoqiang.bean.*;
import com.zhengguoqiang.condition.MacCondition;
import com.zhengguoqiang.condition.WindowCondition;
import org.springframework.context.annotation.*;

/**
 * @author zhengguoqiang
 */
//导入Color类，id为Color类的全类名即com.zhengguoqiang.bean.Color
@Import({Color.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
@Configuration
public class SpringConfig2 {


    /*
    ConfigurableBeanFactory.SCOPE_SINGLETON singleton
    ConfigurableBeanFactory.SCOPE_PROTOTYPE prototype
    WebApplicationContext.SCOPE_REQUEST request
    WebApplicationContext.SCOPE_SESSION session
     */

    /*
    单实例：容器默认作用域，spring容器启动时进行bean的创建，并注册到容器中
    多实例：容器启动时不进行bean的创建，只有在使用bean的时候才会进行创建，并且每次获取都会创建一个新的bean

    懒加载：只在单例模式下使用，容器启动时不在创建bean，只有在使用时才进行创建bean，并注册到容器中
     */

    @Scope(value = "singleton")
    @Lazy
    @Bean
    public Person person(){
        System.out.println("Person inited");
        return new Person("wangwu",25,"xxx");
    }

    /*
    Conditional注解：只有满足条件的bean才会注册到spring容器中
     */
    @Conditional({WindowCondition.class})
    @Bean("bill")
    public Person bill(){
        return new Person("bill",52,"xxx");
    }

    @Conditional(MacCondition.class)
    @Bean("mac")
    public Person linus(){
        return new Person("Qiao",59,"xxx");
    }

    /*
    容器中注册组件的几种方式：
    1. 包扫描+组件标注注解（@Controller/@Service/@Repository/@Component）[适用于自定义的组件]
    2. @Bean [适用于第三方导入的组件]
    3. @Import
        a. @Import({容器中要导入的组件})，容器就会注册组件，id默认为组件全类名
        b. ImportSelector:返回需要注册到容器的组件全类名数组（SpringBoot中常用方式）
        c. ImportBeanDefinitionRegistrar:手动注册bean到容器中
    4. 使用Spring提供的FactoryBean(工厂Bean)
        a. 默认获取到的是工厂bean调用getObject方法创建的对象
        b. 要获取工厂bean本身，需要在id前面加一个&(BeanFacotry接口中定义的FACTORY_BEAN_PREFIX变量指定)
                &colorFactoryBean
     */

    @Bean
    public ColorFactoryBean colorFactoryBean(){
        return new ColorFactoryBean();
    }
}
