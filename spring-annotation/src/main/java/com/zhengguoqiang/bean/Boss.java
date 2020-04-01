package com.zhengguoqiang.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhengguoqiang
 */
@Component
public class Boss {

    private Car car;

    public Boss() {
        System.out.println("boss...无参构造");
    }

//    @Autowired
    public Boss(Car car) {
        System.out.println("boss...有参构造");
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    @Autowired
    public void setCar(Car car) {
        System.out.println("boss...setter方法");
        this.car = car;
    }

    @Override
    public String toString() {
        return "Boss{" +
                "car=" + car +
                '}';
    }
}
