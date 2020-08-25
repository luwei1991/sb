package com.product.sampling.thread;

import java.util.concurrent.Executor;

public class CallBackDeletgate implements CallBack,AsyncCallBack {
    private CallBack callBack;
    private AsyncCallBack asyncCallBack;
    private Executor executor;

    CallBackDeletgate(CallBack callBack, Executor executor, AsyncCallBack asyncCallBack){
        this.callBack = callBack;
        this.asyncCallBack = asyncCallBack;
        this.executor = executor;

    }


    @Override
    public void onSuccess(Object o) {
        if(asyncCallBack == null){
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                asyncCallBack.onSuccess(o);
            }
        });

    }

    @Override
    public void onFailed(Throwable throwable) {
        if(asyncCallBack == null){
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                asyncCallBack.onFailed(throwable);
            }
        });

    }

    @Override
    public void onError(String threadName, Throwable throwable) {
        onFailed(throwable);
        if(callBack == null){
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callBack.onError(threadName,throwable);
            }
        });

    }

    @Override
    public void onComplete(String threadName) {
        if(callBack == null){
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callBack.onComplete(threadName);
            }
        });

    }

    @Override
    public void onStart(String threadName) {
        if(callBack == null){
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                callBack.onStart(threadName);
            }
        });

    }
}
