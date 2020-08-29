package com.zhengguoqiang.condition;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author zhengguoqiang
 */
public class MacCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //1.
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        //2.获取类加载器
        ClassLoader classLoader = context.getClassLoader();
        //3.获取当前应用的环境变量
        Environment environment = context.getEnvironment();
        //4.获取bean的注册信息
        BeanDefinitionRegistry registry = context.getRegistry();

        String property = environment.getProperty("os.name");
        assert property != null;
        return property.contains("Mac");
    }
}
