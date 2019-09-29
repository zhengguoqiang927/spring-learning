package com.zhengguoqiang.bean;

import com.zhengguoqiang.service.BookService;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * @author zhengguoqiang
 */
@Configuration
//指定哪些类型不进行组件扫描
/*
@ComponentScan(value = "com.zhengguoqiang",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class, Service.class})
})
*/
//指定哪些类型进行组件扫描,useDefaultFilters用于禁用默认过滤规则
//FilterType.ANNOTATION：基于注解
//FilterType.ASSIGNABLE_TYPE：基于给定的类型
//FilterType.ASPECTJ：基于ASPECTJ表达式
//FilterType.REGEX：基于正则表达式
//FilterType.CUSTOM：自定义
@ComponentScan(value = "com.zhengguoqiang",includeFilters = {
        /*@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class}),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {BookService.class}),*/
        @ComponentScan.Filter(type = FilterType.CUSTOM,classes = {MyTypeFilter.class})
},useDefaultFilters = false)

//同时指定多个包扫描规则
/*@ComponentScans(value = {
        @ComponentScan(value = "com.zhengguoqiang",includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})
        },useDefaultFilters = false)
})*/
public class SpringConfig {

    //注入容器的bean类型就是返回值类型，id默认为方法名，也可通过bean注解value属性改id名称
    @Bean("person01")
    public Person getPerson(){
        return new Person("lisi",20);
    }
}
