package com.zhengguoqiang.config;

import com.zhengguoqiang.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author zhengguoqiang
 */
//使用@PropertySource读取外部配置文件中的k/v保存到运行的环境变量中；使用${}读取配置文件的内容
@PropertySource(value = {"classpath:/person.properties"})
@Configuration
public class SpringConfigPropertyValues {

    @Bean
    public Person person(){
        return new Person();
    }
}
