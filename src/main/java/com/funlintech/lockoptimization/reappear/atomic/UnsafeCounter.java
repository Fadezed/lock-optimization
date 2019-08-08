package com.funlintech.lockoptimization.reappear.atomic;


import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 还原原子问题
 * @author zed
 */
public class UnsafeCounter {
    /**
     * 定义volatile变量 保证可见性
     */
    private volatile int i =0;

    public static void main(String[] args) {
        final UnsafeCounter counter = new UnsafeCounter();
        List<Thread> ts = new ArrayList<>(100);
        StopWatch start = new StopWatch("start add");
        start.start();
        for (int j = 0; j < 100;j++){
            Thread t = new Thread(() -> {
                for(int i = 0;i < 100000; i++){
                    counter.count();
                }
            });
            ts.add(t);
        }
        for(Thread t :ts){
            t.start();
        }
        //等待所有线程执行完成
        for(Thread t:ts){
            try{
                t.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println("result:"+counter.i);
        start.stop();
        System.out.println(start.prettyPrint());

    }

    /**
     * 非线程安全计数器
     */
    private void count() {
        i++;
    }
}
