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
 *      原理：
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
 *      原理：
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
 * 原理：ContextRefreshedEvent、SpringConfigExtTest$1[source=发布事件]、ContextClosedEvent
 * 1.容器启动或刷新发布ContextRefreshedEvent事件;publishEvent(new ContextRefreshedEvent(this));
 *      1.创建容器对象,注册完配置类后调用refresh()方法
 *      2.finishRefresh();发布相应事件
 * 2.自己手动发布事件applicationContext.publishEvent
 * 3.容器关闭发布ContextClosedEvent事件;publishEvent(new ContextClosedEvent(this));
 *
 * 事件发布流程：
 *      1.getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);获取事件多播器并派发事件
 *      2.获取所有的事件监听器ApplicationListener
 *          ApplicationListener<?> listener : getApplicationListeners(event, type)
 *          1.如果有Executor,则异步执行调用监听器方法executor.execute(() -> invokeListener(listener, event));
 *          2.如果没有,直接invokeListener(listener, event);
 *          3.回调监听器的onApplicationEvent方法:listener.onApplicationEvent(event);
 *
 * 事件多播器：
 *      1.创建容器对象,refresh();
 *      2.initApplicationEventMulticaster();为容器初始化事件多播器
 *          1.先去容器中查找id="applicationEventMulticaster"的组件
 *          2.如果没有就创建一个简单事件多播器并注册到容器中
 *              this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
 *              其他组件发布事件时就会调用SimpleApplicationEventMulticaster.multicastEvent方法
 * 事件监听器：
 *      1.创建容器对象,refresh();
 *      2.registerListeners();注册事件监听器
 *          从容器中拿到所有的监听器,并把他们注册到applicationEventMulticaster中
 *          String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
 *          //将listener添加到applicationEventMulticaster
 *          getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
 *
 * @EventListener:业务逻辑组件中通过该注解监听想监听的事件
 *      该注解通过EventListenerMethodProcessor进行实现,EventListenerMethodProcessor实现SmartInitializingSingleton接口
 *      而SmartInitializingSingleton接口的afterSingletonsInstantiated()方法的触发时机是在组件预实例化结束阶段
 *
 *      SmartInitializingSingleton原理 => afterSingletonsInstantiated()
 *          1.创建容器对象,refresh();
 *          2.finishBeanFactoryInitialization(beanFactory);初始化剩余的单实例Bean
 *              1.先创建所有的单实例Bean,getBean(beanName)
 *              2.获取所有已经创建好的单实例Bean,判断Bean是否是SmartInitializingSingleton类型的
 *                  如果是就调用afterSingletonsInstantiated()方法
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
