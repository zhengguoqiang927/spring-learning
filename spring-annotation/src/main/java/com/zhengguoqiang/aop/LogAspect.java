package com.zhengguoqiang.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

@Aspect
public class LogAspect {

    @Pointcut("execution(public int com.zhengguoqiang.aop.MathCalculator.*(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void logStart(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        System.out.println(joinPoint.getSignature().getName() + "方法前运行logstart,参数列表：" + Arrays.asList(args));
    }

    @After("pointcut()")
    public void logAfter(JoinPoint joinPoint){
        System.out.println(joinPoint.getSignature().getName() + "方法后运行logafter");
    }

    //JoinPoint参数一定要出现在参数列表的第一位，否则会报异常
    @AfterReturning(value = "pointcut()",returning = "result")
    public void logReturning(JoinPoint joinPoint,Object result){
        System.out.println(joinPoint.getSignature().getName() + "方法正常返回后运行,接受的结果：" + result);
    }

    @AfterThrowing(value = "pointcut()",throwing = "exception")
    public void logThrowing(JoinPoint joinPoint,Exception exception){
        System.out.println(joinPoint.getSignature().getName() + "方法出现异常后执行,异常信息：" + exception);
    }

//    @Around("pointcut()")
//    public void logAround(){
//        System.out.println("logaround...");
//    }
}
