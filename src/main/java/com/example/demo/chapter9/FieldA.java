package com.example.demo.chapter9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/26
 */
@Component
public class FieldA {

    @Autowired
    private FieldB fieldB;
}
