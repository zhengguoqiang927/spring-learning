package com.zhengguoqiang.service;

import com.zhengguoqiang.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 * @author zhengguoqiang
 */
//@Service
public class BookService {

//    @Qualifier("bookDao")
//    @Autowired(required = false)
//    @Resource(name = "bookDao2")
//    @Inject
    private BookDao bookDao;

    @Override
    public String toString() {
        return "BookService{" +
                "bookDao=" + bookDao +
                '}';
    }
}
