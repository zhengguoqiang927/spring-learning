package com.zhengguoqiang.bean;

import com.zhengguoqiang.aop.MathCalculator;
import com.zhengguoqiang.config.SpringConfigAOP;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringConfigAOPTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(SpringConfigAOP.class);
        MathCalculator calculator = applicationContext.getBean(MathCalculator.class);
        calculator.div(1, 1);
    }
}
