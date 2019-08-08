package com.funlintech.lockoptimization.solution.sync;


import com.funlintech.lockoptimization.util.ThreadPoolBuilder;
import com.funlintech.lockoptimization.util.ThreadUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 基于Object等待通知队列
 * wait() and notify
 *
 * @author zed
 * @since 2019-07-01 11:12 AM
 */
public class WaitNotifyQueue {
    /**
     * 打印条件
     */
    private boolean continueToPrint;
    private int count;
    /**
     * 阻塞队列
     */
    private BlockingQueue<String> messages;
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("WaitNotifyQueue").build();

    private WaitNotifyQueue(List<String> messages) {
        this.messages = new LinkedBlockingQueue<>(messages);
        this.continueToPrint = true;
    }
    /**
     * 停止打印
     */
    private synchronized void stopsMessaging() {
        continueToPrint = false;
    }
    /**
     * 通知继续打印
     */
    private synchronized void notifyPrint(){
        System.out.println("通知继续输出完队列内容");
        continueToPrint = true;
        notifyAll();
    }
    /**
     * 队列中获取数据并输出
     * @throws InterruptedException e
     */
    private synchronized String printMessage() throws InterruptedException {
        while (!continueToPrint){
            wait();
        }
        String message = messages.poll();
        count++;
        System.out.println(message);
        return message;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List messages = new LinkedList<String>();
        //初始化队列内容
        for (int i = 0; i < 10; i++) {
            messages.add(UUID.randomUUID().toString());
        }
        WaitNotifyQueue waitNotifyQueue = new WaitNotifyQueue(messages);
        threadPoolExecutor.execute(() -> {
            try {
                while (true) {
                    if(waitNotifyQueue.printMessage() == null){
                        break;
                    }
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });

        threadPoolExecutor.execute(() -> {
            while (true) {
                if (waitNotifyQueue.count == 5) {
                    break;
                }
                ThreadUtil.sleep(500);
            }
            waitNotifyQueue.stopsMessaging();
            /*
             * 停止3s后唤醒等待线程继续执行
             */
            ThreadUtil.sleep(3, TimeUnit.SECONDS);
            waitNotifyQueue.notifyPrint();

        });
        threadPoolExecutor.shutdown();

    }
}

