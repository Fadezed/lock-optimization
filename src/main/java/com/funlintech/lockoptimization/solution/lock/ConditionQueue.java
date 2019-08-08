package com.funlintech.lockoptimization.solution.lock;


import com.funlintech.lockoptimization.util.ThreadPoolBuilder;

import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述:
 * 条件队列
 * 通过ReentrantLock 实现打印和停止打印的条件队列，每次容量为5
 *
 * @author zed
 * @since 2019-07-01 10:59 AM
 */
public class ConditionQueue {
    private static final int LIMIT = 5;
    /**
     * 输出记录计数器
     */
    private int messageCount = 0;
    private Lock lock = new ReentrantLock();
    /**
     * 停止打印条件
     */
    private Condition limitStopCondition = lock.newCondition();
    /**
     * 打印条件
     */
    private Condition limitPrintCondition = lock.newCondition();
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("Condition Queue").build();

    /**
     * 停止打印
     * @throws InterruptedException e
     */
    private void stopMessages() throws InterruptedException {
        lock.lock();
        try {
            //输出小于限制时停止条件不满足 打印条件满足
            while (messageCount < LIMIT) {
                limitStopCondition.await();
            }
            System.err.println("Limit reached. Wait 2s");
            Thread.sleep(2000);
            messageCount = 0;
            limitPrintCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 打印
     * @param message mes
     * @throws InterruptedException e
     */
    private void printMessages(String message) throws InterruptedException {
        lock.lock();
        try {
            //输出等于限制时停止条件满足 打印条件不满足
            while (messageCount == LIMIT) {
                limitPrintCondition.await();
            }
            System.out.println(message);
            messageCount++;
            limitStopCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionQueue queue = new ConditionQueue();
        // Will run indefinitely
        threadPoolExecutor.execute(() -> {
            while (true) {
                String uuidMessage = UUID.randomUUID().toString();
                try {
                    queue.printMessages(uuidMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadPoolExecutor.execute(() -> {
            while (true) {
                try {
                    queue.stopMessages();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

