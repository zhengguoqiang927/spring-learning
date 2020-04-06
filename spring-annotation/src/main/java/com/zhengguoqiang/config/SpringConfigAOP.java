package com.zhengguoqiang.config;

import com.zhengguoqiang.aop.LogAspect;
import com.zhengguoqiang.aop.MathCalculator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP：基于动态代理
 *  指在程序运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程模式
 *
 * 1.导入Spring AOP依赖spring-aspects
 * 2.定义一个业务逻辑类（MathCalculator）,在运行业务逻辑的过程中打印日志（调用方法之前,之后,返回后,异常）
 * 3.定义一个日志切面类（LogAspect）,
 *      通知方法：
 *          前置通知@Before：在目标方法（div）之前执行
 *          后置通知@After：在目标方法（div）之后执行（无论方法正常返回还是异常返回）
 *          返回通知@AfterReturning：在目标方法（div）正常返回之后执行
 *          异常通知@AfterThrowing：在目标方法（div）出现异常之后运行
 *          环绕通知@Around：动态代理,手动推进目标方法运行（joinPoint.procced()）
 * 4.给切面类的目标方法标注何时何地运行（通知注解）
 * 5.将切面类和业务逻辑类注册到容器中
 * 6.告诉Spring哪个是切面类,即在切面类上加@Aspect注解
 * 7.配置类中加@EnableAspectJAutoProxy注解,启动基于注解的AOP模式
 *
 * 简化三步曲：
 *      1.定义切面类和业务逻辑类,并用@Aspect注解标注哪个是切面类
 *      2.切面类中的通知方法上标注通知及切入点,告诉Spring何时何地切入执行通知方法 @Pointcut及@Before等
 *      3.开启基于注解的AOP模式,配置类上加@EnableAspectJAutoProxy
 *
 * AOP原理：
 * @see EnableAspectJAutoProxy:
 *      @Import(AspectJAutoProxyRegistrar.class)给容器中导入AspectJAutoProxyRegistrar组件
 * @see  org.springframework.context.annotation.AspectJAutoProxyRegistrar
 *      实现ImportBeanDefinitionRegistrar接口向容器中注册AnnotationAwareAspectJAutoProxyCreator组件,BeanName为org.springframework.aop.config.internalAutoProxyCreator
 * @see org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator:
 *      extends AspectJAwareAdvisorAutoProxyCreator
 *          extends AbstractAdvisorAutoProxyCreator
 *              extends AbstractAutoProxyCreator
 *                  implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
 *                  主要关注后置处理器BEanPostProcessor以及自动装配BeanFactory
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator:
 *      {@link org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setBeanFactory(BeanFactory)}
 *      {@link org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessBeforeInstantiation(Class, String)}
 *
 * @see org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator
 *      {@link org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#setBeanFactory(BeanFactory)}
 *          => {@link org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#initBeanFactory(ConfigurableListableBeanFactory)}
 *
 * @see org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
 *      {@link org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#initBeanFactory(ConfigurableListableBeanFactory)}
 *
 * BeanPostProcessor创建流程：
 *      1.传入配置类,创建ioc容器
 *      2.注册配置类,调用refresh()刷新容器
 *      3.registerBeanPostProcessors(beanFactory) 注册Bean后置处理器来拦截Bean的创建
 *          1.获取ioc容器中已经定义好的,且类型为BeanPostProcessor的BeanName
 *          2.遍历BeanName,并按照PriorityOrdered,Ordered,the rest分组
 *          3.优先注册实现PriorityOrdered接口的BeanPostProcessor
 *          4.再注册实现Ordered接口的BeanPostProcessor
 *          5.最后注册正常的BeanPostProcessor
 *          6.注册BeanPostProcessor：实际上就是先创建BeanPostProcessor实例,然后在添加到容器中
 *                 这里我们要生成BeanName=org.springframework.aop.config.internalAutoProxyCreator,Bean=AnnotationAwareAspectJAutoProxyCreator的实例
 *                 1.创建Bean实例
 *                 2.populateBean(beanName, mbd, instanceWrapper):给Bean的属性赋值
 *                 3.initializeBean(beanName, exposedObject, mbd):初始化Bean
 *                      1.invokeAwareMethods(beanName, bean):处理Aware接口的方法回调
 *                      2.applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName):执行BeanPostProcessor的postProcessBeforeInitialization方法
 *                      3.invokeInitMethods(beanName, wrappedBean, mbd):执行自定义的初始化方法
 *                      4.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName):执行BeanPostProcessor的postProcessAfterInitialization方法
 *                 4.BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)创建成功,并执行AnnotationAwareAspectJAutoProxyCreator.initBeanFactory方法
 *          7.把BeanPostProcessor添加到BeanFactory中
 *                  beanFactory.addBeanPostProcessor(postProcessor);
 *以上的流程就是创建AnnotationAwareAspectJAutoProxyCreator(本质也是个BeanPostProcessor)组件并注册到容器BeanFactory中的过程
 *      4.finishBeanFactoryInitialization(beanFactory):实例化剩余的非懒加载的Bean
 *          1.获取容器中所有的beanDefinitionNames并遍历,依次调用getBean(beanName)创建实例
 *              getBean => doGetBean => getSingleton => createBean
 *          2.创建Bean
 *              1.先从缓存中获取Bean,如果能获取到,说明是之前被创建过的,直接返回;如果没有获取到在通过getSingleton => createBean 创建Bean
 *              2.createBean创建Bean
 *                  1.resolveBeforeInstantiation(beanName, mbdToUse):
 *                      希望后置处理器能够返回目标实例的一个代理实例,如果有代理实例则直接使用,如果没有则继续创建
 *                      1.后置处理器先尝试返回
 *                          [BeanPostProcessor是在Bean创建完成属性赋值完成之后的初始化前后调用的]
 *                          [InstantiationAwareBeanPostProcessor是在Bean创建之前尝试返回Bean实例的]
 *                          [AnnotationAwareAspectJAutoProxyCreator就是InstantiationAwareBeanPostProcessor,也就是在每个Bean实例创建之前都会执行postProcessBeforeInstantiation,
 *                          来判断当前Bean是否需要增强]
 *                          先拿到所有后置处理器,如果是InstantiationAwareBeanPostProcessor,就执行postProcessBeforeInstantiation
 *                          bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
 * 					        if (bean != null) {
 * 						        bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
 *                          }
 *                  2.doCreateBean(beanName, mbdToUse, args):真正去创建一个Bean实例
 *
 *
 * AnnotationAwareAspectJAutoProxyCreator[InstantiationAwareBeanPostProcessor]的作用：
 * 1.每一个Bean创建之前调用AbstractAutoProxyCreator.postProcessBeforeInstantiation
 *      重点关注业务逻辑类(MathCalculator)和切面类(LogAspect)的处理
 *      1.判断当前Bean是否在advisedBeans(保存所有需要增强的Bean)中
 *      2.判断当前Bean是否是基础类型的Advice,Pointcut,Advisor,AopInfrastructureBean或者是否是@Aspect注解标注过的切面(如果是存入advisedBeans中,但value是false表示不需要增强：切面不需要增强,业务逻辑类才需要增强)
 *      3.shouldSkip(beanClass, beanName)是否需要跳过(如果是就表示该Bean不需要增强)
 *          1.获取候选增强器(切面里的通知方法)[List<Advisor> candidateAdvisors]
 *              判断增强器里是否有AspectJPointcutAdvisor这个类型的,如果有则返回true
 *              通常我们在SpringAOP里声明的增强器都是InstantiationModelAwarePointcutAdvisor类型
 *
 * 2.doCreateBean创建对象
 *      doCreateBean => initializeBean => applyBeanPostProcessorsAfterInitialization => 所有BeanPostProcessor的postProcessAfterInitialization方法 =>
 *      AbstractAutoProxyCreator的postProcessAfterInitialization
 *      这个方法的返回是 return wrapIfNecessary(bean, beanName, cacheKey);//必要的情况下进行包装
 *      wrapIfNecessary(bean, beanName, cacheKey)
 *          1.获取当前Bean的所有增强器(通知方法)：Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
 *              1.找到所有候选的增强器
 *              2.获取能在bean使用的增强器(反射获取Bean的方法并与通知方法上定义的切入点进行匹配，如果匹配就返回)
 *              3.给增强器排序
 *          2.如果当前Bean需要增强,则将其保存到advisedBeans中,并创建对应的代理对象
 *              1.创建代理工厂ProxyFactory,并填入相应信息advisor,targetsource等等
 *              2.创建代理对象 Spring判断目标类（targetClass）是否是接口,如果是则创建JdkDynamicAopProxy动态代理否则创建ObjenesisCglibAopProxy动态代理
 *                  JdkDynamicAopProxy:JDK动态代理 基于接口
 *                  ObjenesisCglibAopProxy:Cglib动态代理 基于类
 *          3.如果Bean需要增强则返回增强后的代理对象,如果不需要则返回普通的Bean
 *          4.以后容器中获取到的就是这个组件的代理对象,执行目标方法的时候,代理对象就会执行通知方法
 *
 * 3.目标方法的执行
 *      容器中保存了组件的代理对象（cglib增强后的对象）,这个对象里保存了详细信息（如增强器,目标对象,xxx）
 *      1.CglibAopProxy.intercept():拦截目标方法的执行
 *      2.根据ProxyFactory对象获取将要执行的目标方法拦截器链
 *          List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
 *          1.获取所有增强器并进行遍历
 *              Advisor[] advisors = config.getAdvisors();
 *          2.判断增强器类型是否属于PointcutAdvisor,IntroductionAdvisor还是其他,最终都将其转换成MethodInterceptor
 *              MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
 *          3.如果通知(advice)是MethodInterceptor,则直接添加到List<MethodInterceptor>集合中
 *              如果不是MethodInterceptor,需要通过AdvisorAdapter适配器将其转成MethodInterceptor,然后在加到集合中
 *          4.返回拦截器链集合List<MethodInterceptor>
 *      3.如果chain是空的,则通过反射直接执行目标方法
 *      4.如果chain不是空的,需要用增强后的代理对象、原对象、目标方法、方法参数、类信息、拦截器链、MethodProxy等信息创建一个CglibMethodInvocation对象,并执行proceed()方法
 *          retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
 *      5.拦截器链的触发过程
 *          1.如果没有拦截器,或者当前拦截器索引currentInterceptorIndex等于拦截器数组大小-1,则直接执行目标方法
 *          2.链式获取每一个拦截器并执行invoke方法,每一个拦截器等待下一个拦截器完成返回以后再执行.
 *              拦截器链的机制保证通知方法和目标方法的执行顺序
 * 4.总结
 *      1.@EnableAspectJAutoProxy 开启AOP功能
 *      2.@EnableAspectJAutoProxy给容器注册一个组件AnnotationAwareAspectJAutoProxyCreator(该组件是一个BeanPostProcessor)
 *      3.容器的创建流程：
 *          1.registerBeanPostProcessors注册后置处理器,创建AnnotationAwareAspectJAutoProxyCreator对象并注册到容器中
 *          2.finishBeanFactoryInitialization初始化剩下的单实例bean
 *              1.创建业务逻辑组件和切面组件
 *              2.AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
 *              3.组件创建完成之后,判断组件是否需要增强
 *                  是：切面的通知方法,包装成增强器(Advisor);给业务逻辑组件创建一个代理对象
 *          3.执行目标方法
 *              1.代理对象执行目标方法
 *              2.CglibAopProxy.intercept()
 *                  1.得到目标方法的拦截器链(增强器包装成拦截器MethodInterceptor)
 *                  2.利用拦截器的链式机制,依次进入每个拦截器进行执行
 *                  3.效果：
 *                      正常执行：前置通知 => 目标方法 => 后置通知 => 返回通知
 *                      异常执行：前置通知 => 目标方法 => 后置通知 => 异常通知
 *
 *
 *
 */

@EnableAspectJAutoProxy
@Configuration
public class SpringConfigAOP {

    @Bean
    public MathCalculator mathCalculator(){
        return new MathCalculator();
    }

    @Bean
    public LogAspect logAspect(){
        return new LogAspect();
    }
}
