package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfigExt;
import com.zhengguoqiang.ext.Blue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringConfigExtTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfigExt.class);
//        Blue bean = applicationContext.getBean(Blue.class);
//        System.out.println(bean);
        //发布事件
        applicationContext.publishEvent(new ApplicationEvent("发布事件") {
            @Override
            public Object getSource() {
                return super.getSource();
            }
        });
        applicationContext.close();
    }
}
