package com.example.demo.chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description 手写线程池
 * @date created on 2021/3/21
 */
public class MyThreadPool {

    /**
     * 阻塞队列实现生产消费模型
     */
    BlockingQueue<Runnable> workQueue;

    /**
     * 保存内部工作线程
     */
    List<WorkThread> workThreadList = new ArrayList<>();

    public MyThreadPool(int coreSize, BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        for (int i = 0; i < coreSize; i++) {
            WorkThread thread = new WorkThread();
            thread.start();
            workThreadList.add(thread);
        }
    }

    void execute(Runnable runnable) {
        try {
            workQueue.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class WorkThread extends Thread {

        @Override
        public void run() {
            while (true) {
                Runnable task = null;
                try {
                    task = workQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                task.run();
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingDeque<>(5);
        MyThreadPool threadPool = new MyThreadPool(2, blockingQueue);
        for (int i = 0; i < 10; i++) {
            int num = i;
            threadPool.execute(() -> System.out.println("线程" + num + " 执行"));
        }
    }
}
