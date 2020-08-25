package com.product.sampling.thread;

public interface CallBack {

    void onError(String threadName,Throwable throwable);

    void onComplete(String threadName);

    void onStart(String threadName);
}
