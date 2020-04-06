package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfigTransactionManager;
import com.zhengguoqiang.tx.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringConfigTxTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(SpringConfigTransactionManager.class);

        UserService userService = applicationContext.getBean(UserService.class);
        int r = userService.insertUser();
    }
}
