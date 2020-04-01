package com.zhengguoqiang.config;

import com.zhengguoqiang.bean.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * bean的生命周期
 *      bean创建----初始化----销毁的过程
 * 容器管理bean的生命周期：
 * 我们可以自定义初始化和销毁方法；容器在bean进行到当前生命周期的时候调用我们自定义的初始化和销毁方法
 *
 * 构造对象：
 *  单实例：容器启动的时候创建
 *  多实例：每次获取的时候创建
 *
 * 初始化：
 *  对象创建完成，并将属性赋值完成后，才执行初始化方法
 *
 * 销毁：
 *  单实例：容器关闭的时候
 *  多实例：容器不会管理这个bean，容器不会调用销毁方法
 *
 * 1. 指定初始化和销毁方法：
 *      通过@Bean指定init-method="",destroy-method=""(xml配置文件里指定)
 * 2. 通过让Bean实现InitializingBean（定义初始化逻辑），DisposableBean（定义销毁逻辑）
 * 3. 可以使用JSR250（javax.annotation）：
 *         @PostConstruct ：在bean创建完成并且属性赋值完成后执行初始化方法
 *         @PreDestroy ：在容器销毁bean之前通知我们进行清理工作
 *         注：这两个注解是通过InitDestroyAnnotationBeanPostProcessor处理完成的，与前两种方式不一样
 *
 * 4. BeanPostProcessor[interface]：bean的后置处理器
 *      在bean初始化前后进行一些处理工作：
 *      postProcessBeforeInitialization：在初始化（InitializingBean's afterPropertiesSet || a custom init-method）之前工作
 *      postProcessAfterInitialization：在初始化（InitializingBean's afterPropertiesSet || a custom init-method）之后工作
 *
 * BeanPostProcessor原理：
 * populateBean(beanName, mbd, instanceWrapper);给bean属性赋值
 * initializeBean(beanName, exposedObject, mbd)
 * {
 *     遍历容器中的所有BeanPostProcessor:挨个执行postProcessBeforeInitialization
 *     一旦返回null，跳出for循环，不在执行后面的BeanPostProcessor的postProcessBeforeInitialization方法
 *     AfterInitialization同理
 *     applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
 *     invokeInitMethods(beanName, wrappedBean, mbd);执行初始化操作（afterPropertiesSet || init-method方法）
 *     applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
 *
 * }
 *
 * Spring底层对BeanPostProcessor的使用：
 *      Bean的赋值，注入其他组件，@Autowired，生命周期注解功能，@Async，XXXBeanPostProcessor;
 *
 *
 *
 *
 * @author zhengguoqiang
 */
@ComponentScan("com.zhengguoqiang.bean")
@Configuration
public class SpringConfigLifeCycle {

    //@Scope("prototype")
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public Car car(){
        return new Car();
    }
}
