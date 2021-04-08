package com.example.demo.chapter7;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.google.common.collect.Sets;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/22
 */
public class ShareLock implements SimpleLock {

    /**
     * Lock有可重入的语义，一个线程拥有锁之后再次调用lock应该完全没有任何问题，所以锁的实现中需要维护一个已经获取锁的线程队列；
     */
    private volatile Set<Thread> threadsOwnsLock = Sets.newConcurrentHashSet();

    private final AtomicInteger state;

    /**
     * 等待队列
     */
    private final ConcurrentLinkedQueue<Thread> waitThreadsQueue = new ConcurrentLinkedQueue<>();

    public ShareLock(int shareNum) {
        this.state = new AtomicInteger(shareNum);
    }

    @Override
    public void lock() {
        tryLock(-1L);
    }

    @Override
    public void unLock() {
        tryRelease(-1);
        threadsOwnsLock.remove(Thread.currentThread());
        if (!waitThreadsQueue.isEmpty()) {
            for (Thread thread : waitThreadsQueue) {
                LockSupport.unpark(thread);
            }
        }
    }

    @Override
    public boolean tryLock() {
        if (!(threadsOwnsLock.contains(Thread.currentThread()))) {
            return true;
        }
        if (tryAcquire(1)) {
            threadsOwnsLock.add(Thread.currentThread());
            return true;
        }

        return false;
    }

    @Override
    public boolean tryLock(long maxWaitInMills) {
        Thread currentThread = Thread.currentThread();
        try {
            waitThreadsQueue.add(currentThread);
            if (maxWaitInMills > 0) {
                boolean acquired = false;
                long left = TimeUnit.MILLISECONDS.toNanos(maxWaitInMills);
                long cost = 0;
                while (true) {
                    if (tryAcquire(1)) {
                        threadsOwnsLock.add(Thread.currentThread());
                        acquired = true;
                        break;
                    }

                    left = left - cost;
                    long mark = System.nanoTime();
                    if (left <= 0) {
                        break;
                    }
                    LockSupport.parkNanos(left);
                    cost = mark - System.nanoTime();
                    // 有可能是被唤醒重新去获取锁,没获取到还得继续等待剩下的时间(并不精确)
                }
                return acquired;
            } else {
                while (true) {
                    if (tryAcquire(1)) {
                        threadsOwnsLock.add(Thread.currentThread());
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
        if (state.getAndDecrement() > 0) {
            return true;
        } else {
            state.getAndIncrement();
            // 恢复回来
            return false;
        }
    }

    private void tryRelease(int release) {
        if (!(threadsOwnsLock.contains(Thread.currentThread()))) {
            System.out.println("Wrong state, this thread don't own this lock.");
        }
        state.getAndIncrement();
    }
}
