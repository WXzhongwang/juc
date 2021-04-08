package com.example.demo.chapter9;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/26
 */
public interface ObjectFactory<T> {

    /**
     * 获取对象
     * 
     * @return T
     */
    T getObject();
}
