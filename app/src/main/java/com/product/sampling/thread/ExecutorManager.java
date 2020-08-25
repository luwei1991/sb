package com.product.sampling.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class ExecutorManager implements Executor {
    private static ExecutorManager instance = new ExecutorManager();
    private Handler main = new Handler(Looper.getMainLooper());
    private ExecutorManager (){
    }

    public static ExecutorManager getInstance(){
        return instance;
    }
    @Override
    public void execute(final Runnable runnable) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            runnable.run();
            return;
        }

        main.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });

    }
}
