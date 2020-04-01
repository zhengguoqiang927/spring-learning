package com.zhengguoqiang.config;

import com.zhengguoqiang.aop.LogAspect;
import com.zhengguoqiang.aop.MathCalculator;
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
