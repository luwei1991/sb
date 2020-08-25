package com.product.sampling.thread;

import java.util.concurrent.Callable;

public class RunnableWrapper implements Runnable {
    private String name;
    private CallBackDeletgate callBackDeletgate;
    private Runnable runnable;
    private Callable callable;

    RunnableWrapper(Configs configs){
        this.name = configs.name;
        this.callBackDeletgate = new CallBackDeletgate(configs.callBack,configs.executor,configs.asyncCallBack);
    }

    RunnableWrapper setRunnable(Runnable runnable){
        this.runnable = runnable;
        return this;
    }

    RunnableWrapper setCallable(Callable callable){
        this.callable = callable;
        return this;
    }


    @Override
    public void run() {
        Thread current = Thread.currentThread();
        resetThread(current,name,callBackDeletgate);
        callBackDeletgate.onStart(name);
        if(runnable != null){
            runnable.run();
        }else if(callable != null) {
            try {
                Object result = callable.call();
                callBackDeletgate.onSuccess(result);
            } catch (Exception e) {
                e.printStackTrace();
                callBackDeletgate.onError(name,e);
            }
        }

        callBackDeletgate.onComplete(name);

    }


    private void resetThread(Thread thread,String name, CallBack callBack){
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if(callBack != null){
                    callBack.onError(name,e);
                }
            }
        });
        thread.setName(name);
    }
}
