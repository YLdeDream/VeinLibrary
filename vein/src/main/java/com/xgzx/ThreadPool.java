package com.xgzx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public static ExecutorService fixedThreadPool;

    public ThreadPool() {
    }

    public static void runCachedThread(Runnable command) {
        cachedThreadPool.execute(command);
    }

    public static void initFixedThreadPool(int nThread) {
        fixedThreadPool = Executors.newFixedThreadPool(10);
    }

    public static void runFixedThread(Runnable command) {
        fixedThreadPool.execute(command);
    }

    public static void run(Runnable command) {
        runCachedThread(command);
    }

    public static void shutdownCachedThread() {
        if (null != cachedThreadPool) {
            cachedThreadPool.shutdown();
            cachedThreadPool = Executors.newCachedThreadPool();
        }

    }

    public static void shutdownFixedThread() {
        if (null != fixedThreadPool) {
            fixedThreadPool.shutdown();
        }

    }
}
