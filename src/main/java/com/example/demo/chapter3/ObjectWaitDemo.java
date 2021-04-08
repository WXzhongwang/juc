package com.example.demo.chapter3;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/16
 */
public class ObjectWaitDemo {

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();

        new Thread(() -> {
            System.out.println("Thread A is waiting to get lock");
            synchronized (lock) {
                System.out.println("Thread A get lock");
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread A is done");
            }
        }).start();

        Thread.sleep(20);

        new Thread(() -> {
            System.out.println("Thread B is waiting to get lock");
            synchronized (lock) {
                System.out.println("Thread B get lock");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread B is done");
            }
        }).start();
    }

}
