package com.example.demo.chapter6;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/22
 */
public class TestLock {

    public static void main(String[] args) {
        CustomizedLock lock = new CustomizedLock();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " start");
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " is running");

                try {
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " is over");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lock.unLock();
            }).start();
        }
    }
}
