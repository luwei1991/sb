package com.product.sampling.thread;

import android.text.TextUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.product.sampling.Constants;
import com.product.sampling.thread.CallBack;

import static com.product.sampling.thread.LWThread.Builder.TYPE_CACHEABLE;
import static com.product.sampling.thread.LWThread.Builder.TYPE_SCHEDULED;
import static com.product.sampling.thread.LWThread.Builder.TYPE_SINGLE;

public final class LWThread implements Executor {
    private ExecutorService pool;
    private String threadName;
    private CallBack callBack;
    private Executor executor;

    private ThreadLocal<Configs> local;

    private LWThread(int type, int size, int priority, String name, CallBack callback, Executor deliver, ExecutorService pool){
        if(pool == null){
            pool = createPool(type,size,priority);
        }
        this.pool = pool;
        this.threadName = name;
        this.callBack = callback;
        this.executor = deliver;
        this.local = new ThreadLocal<>();
    }

    /**
     * Create thread pool by <b>Executors.newCachedThreadPool()</b>
     * @return Builder itself
     */
    public static Builder createCacheable() {
        return new Builder(0, TYPE_CACHEABLE, null);
    }

    /**
     * Create thread pool by <b>Executors.newFixedThreadPool()</b>
     * @param size thread size
     * @return Builder itself
     */
    public static Builder createFixed(int size) {
        return new Builder(size, Builder.TYPE_FIXED, null);
    }

    /**
     * Create thread pool by <b>Executors.newScheduledThreadPool()</b>
     * @param size thread size
     * @return Builder itself
     */
    public static Builder createScheduled(int size) {
        return new Builder(size, TYPE_SCHEDULED, null);
    }

    /**
     * Create thread pool by <b>Executors.newSingleThreadPool()</b>
     *
     * @return Builder itself
     */
    public static Builder createSingle() {
        return new Builder(0, TYPE_SINGLE, null);
    }

    public LWThread setName(String name){
        getLocalConfigs().name = name;
        return this;
    }

    public LWThread setCallBack(CallBack callBack){
        getLocalConfigs().callBack = callBack;
        return this;
    }

    public LWThread setDelay(long time, TimeUnit unit){
        long delay = unit.toMillis(time);
        getLocalConfigs().delay = delay;
        return this;
    }

    public LWThread setExecutor(Executor executor){
        getLocalConfigs().executor = executor;
        return this;
    }





    private synchronized Configs getLocalConfigs(){
        Configs configs = local.get();
        if(configs == null){
            configs = new Configs();
            configs.name = threadName;
            configs.callBack = callBack;
            configs.executor = executor;
            local.set(configs);

        }
        return  configs;
    }


    private synchronized void resetLocalConfigs(){
        local.set(null);
    }

    /**
     * 执行
     * @param runnable
     */
    @Override
    public void execute(Runnable runnable) {
        Configs configs = getLocalConfigs();
        runnable = new RunnableWrapper(configs).setRunnable(runnable);
        DelayTaskDispatcher.getInstance().postDelay(configs.delay,pool,runnable);
        resetLocalConfigs();
    }

    public <T> void async(Callable<T> callable,AsyncCallBack<T> callBack){
        Configs configs = getLocalConfigs();
        configs.asyncCallBack = callBack;
        Runnable runnable = new RunnableWrapper(configs).setCallable(callable);
        DelayTaskDispatcher.getInstance().postDelay(configs.delay,pool,runnable);
        resetLocalConfigs();
    }

//    public <T>Future<T> submit(Callable<T> callable){
//        Future<T> result;s
//        callable = new Callable<T>() {
//        }
//    }

    private ExecutorService createPool(int type,int size, int priority){
        switch (type) {
            case TYPE_CACHEABLE:
                return Executors.newCachedThreadPool(new DefaultFactory(priority));
            case Builder.TYPE_FIXED:
                return Executors.newFixedThreadPool(size, new DefaultFactory(priority));
            case TYPE_SCHEDULED:
                return Executors.newScheduledThreadPool(size, new DefaultFactory(priority));
            case TYPE_SINGLE:
            default:
                return Executors.newSingleThreadExecutor(new DefaultFactory(priority));
        }
    }



    private static class DefaultFactory implements ThreadFactory {

        private int priority;
        DefaultFactory(int priority) {
            this.priority = priority;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setPriority(priority);
            return thread;
        }
    }
    /**
     * Thread 建造者
     */
    public static class Builder{
        final static int TYPE_CACHEABLE = 0;
        final static int TYPE_FIXED = 1;
        final static int TYPE_SINGLE = 2;
        final static int TYPE_SCHEDULED = 3;

        int type;
        int size;
        int priority = Thread.NORM_PRIORITY;
        String name;
        ExecutorService pool;
        CallBack callBack;
        Executor executor;
        private Builder(int size,  int type, ExecutorService pool){
            this.size = Math.max(1,size);
            this.type = type;
            this.pool = pool;
        }

        public static Builder create(ExecutorService pool){
            return new Builder(1,TYPE_SINGLE,pool);
        }

        /**
         * 设置线程名称
         * @param name
         * @return
         */
        public Builder setName(String name){
            if(!TextUtils.isEmpty(name)){
                this.name = name;
            }
            return this;
        }

        /**
         * 设置优先级
         * @param priority
         * @return
         */
        public Builder setPriority(int priority){
            this.priority = priority;
            return this;
        }

        /**
         * 设置监听callback
         * @param callBack
         * @return
         */
        public Builder setCallBack(CallBack callBack){
            this.callBack = callBack;
            return this;
        }

        public Builder setExecutor(Executor executor){
            this.executor = executor;
            return this;
        }

        public LWThread build(){
            priority = Math.max(Thread.MIN_PRIORITY,priority);
            priority = Math.min(Thread.MAX_PRIORITY,priority);
            size = Math.max(1,size);
            if(TextUtils.isEmpty(name)){
                name = "LwThread-default";
            }

            if(executor == null){
                executor = ExecutorManager.getInstance();
            }

            return new LWThread(type,size,priority,name,callBack,executor,pool);

        }

    }
}
