package com.zhengguoqiang.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public int insertUser(){
        int result = userDao.insert();
        System.out.println("插入完成...");
        int r = result/0;
        return result;
    }
}
