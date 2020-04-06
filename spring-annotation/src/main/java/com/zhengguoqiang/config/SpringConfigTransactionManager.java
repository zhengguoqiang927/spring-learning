package com.zhengguoqiang.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 声明式事务
 *      环境搭建
 *      1.导入相关依赖
 *          数据源、数据库驱动、spring-jdbc模块或者spring-orm模块
 *      2.配置数据源、JdbcTemplate(Spring提供的简化数据库操作的工具)操作数据
 *      3.方法上标注@Transactional注解表示当前方法是一个事务方法
 *      4.@EnableTransactionManagement开启基于注解的事务管理功能
 *      5.配置事务管理器来管理事务
 *          @Bean
 *          public PlatformTransactionManager transactionManager()
 *
 * 原理
 * 1.@EnableTransactionManagement
 *      利用TransactionManagementConfigurationSelector组件给容器中导入AutoProxyRegistrar和ProxyTransactionManagementConfiguration组件
 * 2.AutoProxyRegistrar组件给容器中注册InfrastructureAdvisorAutoProxyCreator组件,该组件与AnnotationAwareAspectJAutoProxyCreator功能类似,都属于后置处理器
 *      利用后置处理器机制在对象创建完成之后,包装对象,返回一个增强过得代理对象,代理对象执行方法利用拦截器链进行拦截
 * 3.ProxyTransactionManagementConfiguration是一个配置类,给容器中注入Bean
 *      1.给容器中注册事务增强器BeanFactoryTransactionAttributeSourceAdvisor,该增强器需要两个属性
 *          1.事务属性解析器：AnnotationTransactionAttributeSource解析事务注解信息
 *          2.事务拦截器：TransactionInterceptor,保存了事务属性解析器和事务管理器
 *              它是一个MethodInterceptor:目标方法执行的时候执行方法拦截器链,与AOP类似
 *              1.先获取事务属性信息
 *              2.获取事务管理器,如果事先没有指定transactionManager属性,就从容器中获取TransactionManager.class的Bean实例
 *              3.执行目标方法
 *                  如果异常,获取事务管理器并执行回滚操作txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
 *                  如果正常,获取事务管理器执行commit,txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
 *
 *
 *
 */


@EnableTransactionManagement
@ComponentScan("com.zhengguoqiang.tx")
@Configuration
public class SpringConfigTransactionManager {

    @Bean
    public HikariDataSource dataSource(){
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("root");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/world");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts","true");
        config.addDataSourceProperty("prepStmtCacheSize","250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit","2048");
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }

}
