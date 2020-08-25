package com.product.sampling.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DelayTaskDispatcher {
    private ScheduledExecutorService dispatcher;
    private static DelayTaskDispatcher instance = new DelayTaskDispatcher();

    private DelayTaskDispatcher(){
        dispatcher = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("delay-task-dispathcer");
                thread.setPriority(Thread.MAX_PRIORITY);
                return thread;
            }
        });

    }

    public static DelayTaskDispatcher getInstance(){
        return  instance;
    }

    public void postDelay(long delay, ExecutorService pool,Runnable task){
        if(delay == 0){
            pool.execute(task);
            return;
        }

        dispatcher.schedule(new Runnable() {
            @Override
            public void run() {
                pool.execute(task);
            }
        },delay, TimeUnit.SECONDS);

    }


}
