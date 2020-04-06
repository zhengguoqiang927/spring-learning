package com.zhengguoqiang.ext;

public class Blue {

    int order = 0;

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
