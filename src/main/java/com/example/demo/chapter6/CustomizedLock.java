package com.example.demo.chapter6;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;

import sun.misc.Unsafe;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/21
 */
public class CustomizedLock {

    private volatile int state;

    private List<Thread> threads = new CopyOnWriteArrayList<>();

    private static final Unsafe unsafe;

    private static final long stateOffset;

    public CustomizedLock() {
        System.out.println("初始化");
    }

    static {
        try {
            // 获取成员变量
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            // 是静态字段,用null来获取Unsafe实例
            unsafe = (Unsafe)field.get(null);
            // 获取state变量在类中的偏移值
            stateOffset = unsafe.objectFieldOffset(CustomizedLock.class.getDeclaredField("state"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private boolean compareAndSetState(int expect, int update) {
        System.out.println("expect: " + expect + ", update: " + update);
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    public void lock() {
        while (!compareAndSetState(0, 1)) {
            // 阻塞
            park();
        }
    }

    public void unLock() {
        while (compareAndSetState(1, 0)) {
            // 唤醒
            unPark();
        }
    }

    public void park() {
        System.out.println(Thread.currentThread().getName() + "因阻塞进入threads list");
        threads.add(Thread.currentThread());
        // 阻塞当前线程
        LockSupport.park(Thread.currentThread());
    }

    public void unPark() {
        if (!threads.isEmpty()) {
            Thread thread = threads.get(0);
            // 唤醒其他线程
            System.out.println(thread.getName() + "被唤醒");
            LockSupport.unpark(thread);
            threads.remove(0);
        }
    }
}
