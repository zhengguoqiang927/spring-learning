package com.zhengguoqiang.bean;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhengguoqiang
 */
public class Person {

    //使用@Value赋值
    //1. 基本数值
    //2. SpEL：#{}
    //3. ${}：读取配置文件的内容或运行环境变量的值
    @Value("张三")
    private String name;
    @Value("#{20-2}")
    private int age;

    @Value("${person.nickName}")
    private String nickName;

    public Person() {
    }

    public Person(String name, int age, String nickName){
        this.name = name;
        this.age = age;
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
