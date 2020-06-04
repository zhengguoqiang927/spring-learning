package com.zhengguoqiang.bean;

import com.zhengguoqiang.ext.Blue;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhengguoqiang
 */
//@Component
public class Car {

    @Autowired
    Blue blue;

    public Car() {
        System.out.println("car...constructor...");
    }

    public void init(){
        System.out.println("car...init...");
    }

    public void destroy(){
        System.out.println("car...destroy...");
    }
}
