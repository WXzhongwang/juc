package com.example.demo.chapter3;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/16
 */
public class WaitSleepDemo {

    public static void main(String[] args) {

        // 可以理解为竞争资源
        Object lock = new Object();

        new Thread(() -> {
            System.out.println("Thread A is waiting to get lock");
            synchronized (lock) {
                try {
                    System.out.println("Thread A get lock");
                    Thread.sleep(1000);
                    System.out.println("Thread A is done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 休息20 ms 让 A 先执行
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("Thread B is waiting to get lock");
            synchronized (lock) {
                try {
                    System.out.println("Thread B get lock");
                    lock.wait(1000);
                    System.out.println("Thread B is done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
