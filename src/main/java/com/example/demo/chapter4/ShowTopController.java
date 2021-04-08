package com.example.demo.chapter4;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/16
 */
@RestController
@RequestMapping("top")
public class ShowTopController {

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    @RequestMapping("test")
    public String test() {
        return "success";
    }

    @RequestMapping("loop")
    public String loop() {
        System.out.println("start");
        while (true) {
        }
    }

    @RequestMapping("deadlock")
    public String deadlock() {
        new Thread(() -> {
            synchronized (lock1) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                }
                synchronized (lock2) {
                    System.out.println("thread1 over");
                }
            }
        }).start();
        new Thread(() -> {
            synchronized (lock2) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                }
                synchronized (lock1) {
                    System.out.println("thread2 over");
                }
            }
        }).start();
        return "success";
    }
}