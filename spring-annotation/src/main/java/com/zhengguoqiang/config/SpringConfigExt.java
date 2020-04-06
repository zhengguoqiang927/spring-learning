package com.zhengguoqiang.config;

import com.zhengguoqiang.ext.Blue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * BeanFactoryPostProcessor:
 *      BeanPostProcessor：bean后置处理器,Bean创建及初始化前后进行拦截
 *      BeanFactoryPostProcessor：BeanFactory的后置处理器
 *      执行时机：在BeanFactory标准初始化之后,所有普通bean的定义已经被加载到BeanFactory中,但还有Bean被实例化
 *
 *      1.IOC容器创建对象
 *      2.refresh() => invokeBeanFactoryPostProcessors(beanFactory);调用在容器中已经注册的工厂处理器BeanFactoryPostProcessor
 *          如何找到所有的BeanFactoryPostProcessor并执行他们的方法
 *              1.从容器中获取BeanFactoryPostProcessor.class类型的实例,并按PriorityOrdered、Ordered、其他分成三组
 *                  String[] postProcessorNames =
 * 				    beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
 * 			      并执行postProcessor.postProcessBeanFactory(beanFactory);
 * 		        2.在初始化创建其他组件之前执行
 *
 * BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor:
 *      postProcessBeanDefinitionRegistry();
 *      执行时机：在BeanFactory标准初始化之后,所有普通Bean定义将要被加载,但还没有Bean被实例化
 *
 *      优先于BeanFactoryPostProcessor执行
 *      可以利用该组件给容器中再额外添加一些组件
 *
 *      1.IOC容器创建对象
 *      2.refresh() => invokeBeanFactoryPostProcessors(beanFactory);调用在容器中已经注册的工厂处理器BeanFactoryPostProcessor
 *          1.从容器中获取BeanDefinitionRegistryPostProcessor组件
 *          2.触发postProcessBeanDefinitionRegistry()方法
 *          3.触发postProcessBeanFactory()方法
 *          4.从容器中获取BeanFactoryPostProcessor.class类型的实例,并执行postProcessor.postProcessBeanFactory(beanFactory);
 *
 * ApplicationListener:监听容器中发布的事件,基于事件驱动模型开发
 *      public interface ApplicationListener<E extends ApplicationEvent>
 *      监听ApplicationEvent及其子事件
 *
 *      步骤：
 *          1.写一个监听器来监听某个事件（ApplicationEvent及其子类）
 *          2.把监听器注入到容器中
 *          3.只要容器中有相关事件的发布,就能监听到事件
 *              ContextRefreshedEvent:容器初始化或刷新完成（所有Bean都完全创建）触发的事件
 *              ContextClosedEvent:容器关闭时触发的事件
 *          4.发布一个事件
 *              applicationContext.publishEvent()
 *
 *
 */

@ComponentScan("com.zhengguoqiang.ext")
@Configuration
public class SpringConfigExt {

    @Bean
    public Blue blue() {
        return new Blue();
    }
}
