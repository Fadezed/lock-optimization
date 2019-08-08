package com.funlintech.lockoptimization.reappear.visiblity;

import com.funlintech.lockoptimization.util.ThreadPoolBuilder;
import com.funlintech.lockoptimization.util.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * visibility 可见性问题
 *
 * @author zed
 * @since 2019-06-13 9:05 AM
 */
public class Visibility {
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).build();

    /**
     * 通过boolean 变量更加直观
     */
    private static boolean flag = true;
    public static void main(String[] args) {

        System.out.println("start");
        //线程开始
        threadPoolExecutor.execute(() -> {
            while(flag){

            }
            System.out.println("stop");

        });
        ThreadUtil.sleep(1000);
        flag = false;
        threadPoolExecutor.shutdown();
    }

}

