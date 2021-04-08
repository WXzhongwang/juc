package com.example.demo.chapter7;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description 以上利用了LockSupport来实现了互斥锁和共享锁，但是实现中并没有完成中断响应。
 * @date created on 2021/3/22
 */
public interface SimpleLock {

    /**
     * 上锁
     */
    void lock();

    /**
     * 解锁
     */
    void unLock();

    /**
     * 尝试上锁
     * 
     * @return 是否上锁成功
     */
    boolean tryLock();

    /**
     * 指定时间范围内上锁
     * 
     * @param maxWaitInMills
     *            最大等待时间
     * @return 是否上锁成功
     */
    boolean tryLock(long maxWaitInMills);
}
