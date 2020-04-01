package com.zhengguoqiang.bean;

import com.zhengguoqiang.config.SpringConfigAutowired;
import com.zhengguoqiang.dao.BookDao;
import com.zhengguoqiang.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhengguoqiang
 */
public class SpringConfigAutowiredTest {

    @Test
    public void test1(){
        ApplicationContext context =
                new AnnotationConfigApplicationContext(SpringConfigAutowired.class);
        BookService bookService = context.getBean(BookService.class);
        System.out.println(bookService);

//        BookDao bookDao = context.getBean(BookDao.class);
//        System.out.println(bookDao);
        Boss boss = context.getBean(Boss.class);
        System.out.println(boss);
        Car car = context.getBean(Car.class);
        System.out.println(car);
    }
}
