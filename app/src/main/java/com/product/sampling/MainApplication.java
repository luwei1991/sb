package com.product.sampling;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
//import com.didichuxing.doraemonkit.DoraemonKit;
import com.product.sampling.db.DbController;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class MainApplication extends Application {
    public static MainApplication INSTANCE;
    public static AMapLocation myLocation = null;
    private int count = 0;
    private int lastCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

//        DoraemonKit.install(this,"b2fb34214898a446459a5217f15bcebe");
        MMKV.initialize(this);
        INSTANCE = this;
        init();
    }

    private void init() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppReportDelay(20000);//设置20s后启动联网数据
        CrashReport.initCrashReport(getApplicationContext(),"16977520a6",true,strategy);
//        NetWorkManager.getInstance().init();
        DbController.getInstance(this).getDaoSession();
        RetrofitServiceManager.getInstance().init();
        initRxJavaError();
        QMUISwipeBackActivityManager.init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                lastCount ++;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                count ++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if(count > 0) {
                    count--;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if(lastCount > 0){
                    lastCount --;
                }
            }
        });
    }

    private void initRxJavaError() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) {
                if (e instanceof UndeliverableException) {
                    e = e.getCause();
                    Log.e("Undeliverable exception", e.getMessage());
                    EventBus.getDefault().post(e);
                }
                if ((e instanceof IOException)) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return;
                }
                if (e instanceof InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return;
                }
                if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                    // that's likely a bug in the application
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    return;
                }
                if (e instanceof IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    return;
                }
            }
        });
    }

    public static AMapLocation getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(AMapLocation myLocation) {
        this.myLocation = myLocation;
    }
    /**
     * 判断app是否在后台
     * @return
     */
    public boolean isBackground(){
        if(count <= 0){
            return true;
        } else {
            return false;
        }
    }
}
