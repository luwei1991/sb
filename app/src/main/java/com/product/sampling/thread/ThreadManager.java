package com.product.sampling.thread;

import android.util.Log;

public final class ThreadManager {


    private static final LWThread dbThread;

    static {
        dbThread = LWThread.createSingle().setName("db-thread").setPriority(7).setCallBack(new DefaultCallback()).build();
    }

    public static LWThread getDbThread(){
        return dbThread;
    }

    private static class DefaultCallback implements CallBack {


        @Override
        public void onError(String threadName, Throwable throwable) {
            Log.e("Task with thread %s has occurs an error: %s", throwable.getMessage());
        }

        @Override
        public void onComplete(String threadName) {
            Log.d("Task with thread %s completed", threadName);
        }

        @Override
        public void onStart(String threadName) {
            Log.d("Task with thread %s start running!", threadName);
        }
    }


}
