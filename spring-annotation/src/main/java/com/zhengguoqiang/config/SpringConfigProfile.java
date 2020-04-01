package com.zhengguoqiang.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

/**
 * @see Profile 指定组件在哪个环境下才能注册到容器中.如果不指定,所有组件都能注册到容器中
 *
 * 1.加了环境标识的bean,只有在这个环境被激活的时候才能注册到容器中.默认是default环境
 * 2.作用于类上表示在指定的环境下整个配置类中的类全部注入到容器中
 * 3.没有指定环境标识的bean,无论在哪个环境都能注册
 */
//@Profile("test")
@PropertySource("classpath:/db.properties")
@Configuration
public class SpringConfigProfile implements EmbeddedValueResolverAware {

    @Value("${db.username}")
    private String username;

    private String jdbcUrl;

    private static HikariConfig config = new HikariConfig();

    static {
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts","true");
        config.addDataSourceProperty("prepStmtCacheSize","250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit","2048");
    }

    @Profile("test")
    @Bean
    public HikariDataSource testDataSource(@Value("${db.password}") String password){
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        return new HikariDataSource(config);
    }

    @Profile("dev")
    @Bean
    public HikariDataSource devDataSource(@Value("${db.password}") String password){
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        return new HikariDataSource(config);
    }

    @Profile("prod")
    @Bean
    public HikariDataSource prodDataSource(@Value("${db.password}") String password){
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        return new HikariDataSource(config);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        jdbcUrl = resolver.resolveStringValue("${db.jdbcUrl}");
        System.out.println("jdbcUrl:" + jdbcUrl);
    }
}
