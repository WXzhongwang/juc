package com.example.demo.chapter7;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/22
 */
public class LockSupportTest {

    public static void main(String[] args) {
        try {
            Thread thread = new Thread(() -> {
                System.out.println("Thread start: " + Thread.currentThread().getName());
                LockSupport.park();
                // 阻塞自己
                System.out.println("Thread end: " + Thread.currentThread().getName());
            });

            thread.setName("A");
            thread.start();

            System.out.println("Main thread sleep 3 second: " + Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(3);
            // 唤醒线程 A
            LockSupport.unpark(thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
