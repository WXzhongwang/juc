package com.example.demo.chapter7;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description 自实现版本互斥锁
 * @date created on 2021/3/22
 */
public class MutexLock implements SimpleLock {

    /**
     * 持有锁的线程
     * 
     */
    private volatile Thread threadOwnsTheLock;

    /**
     * Lock需要维护当前锁的状态（是否可以被获取等） 使用cas实现锁状态
     */
    private final AtomicInteger state = new AtomicInteger(0);

    /**
     * 等待队列
     */
    private final ConcurrentLinkedQueue<Thread> waitThreadsQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void lock() {
        tryLock(-1L);
    }

    @Override
    public void unLock() {
        tryRelease(-1);
        threadOwnsTheLock = null;
        if (!waitThreadsQueue.isEmpty()) {
            for (Thread thread : waitThreadsQueue) {
                // 唤醒其他线程
                LockSupport.unpark(thread);
            }
        }
    }

    @Override
    public boolean tryLock() {
        // 可重入
        if (threadOwnsTheLock != null && (threadOwnsTheLock == Thread.currentThread())) {
            return true;
        }
        if (tryAcquire(1)) {
            threadOwnsTheLock = Thread.currentThread();
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long maxWaitInMills) {
        Thread currentThread = Thread.currentThread();
        try {
            // 加入等待列
            waitThreadsQueue.add(currentThread);
            if (maxWaitInMills > 0) {
                // 存在等待时间
                boolean acquired = false;
                long left = maxWaitInMills * 1000L * 1000L;
                long cost = 0;
                while (true) {
                    // 需要判断一次interrupt

                    if (tryAcquire(1)) {
                        threadOwnsTheLock = currentThread;
                        acquired = true;
                        break;
                    }

                    left = left - cost;
                    long mark = System.nanoTime();
                    if (left <= 0) {
                        // 时间不足
                        break;
                    }
                    LockSupport.parkNanos(left);
                    cost = mark - System.nanoTime();
                }
                return acquired;
            } else {
                while (true) {
                    if (tryAcquire(1)) {
                        threadOwnsTheLock = currentThread;
                        break;
                    }
                    LockSupport.park();
                }
                return true;
            }
        } finally {
            waitThreadsQueue.remove(currentThread);
        }
    }

    private boolean tryAcquire(int acquire) {
        return state.compareAndSet(0, 1);
    }

    private void tryRelease(int release) {
        if (threadOwnsTheLock == null || (threadOwnsTheLock != Thread.currentThread())) {
            System.out.println("Wrong state, this thread don't own this lock.");
        }
        while (true) {
            if (state.compareAndSet(1, 0)) {
                return;
            }
        }
    }
}
