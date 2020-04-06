package com.zhengguoqiang.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Random;
import java.util.UUID;

@Repository
public class UserDao {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(){
        String sql = "insert into user(name,age) values(?,?)";
        String name = UUID.randomUUID().toString().substring(0,5);
        return jdbcTemplate.update(sql, name, new Random().nextInt(100));
    }
}
