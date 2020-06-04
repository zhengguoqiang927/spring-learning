package com.zhengguoqiang.ext;

import com.zhengguoqiang.bean.Car;
import org.springframework.beans.factory.annotation.Autowired;

public class Blue {

    int order = 0;

    @Autowired
    Car car;

    public Blue() {
        System.out.println("Blue...constructor...");
    }

    @Override
    public String toString() {
        return "Blue{" +
                "order=" + order +
                '}';
    }
}
