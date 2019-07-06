package com.product.sampling.ui;

import android.app.Application;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.utils.GdLocationUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class MainApplication extends Application {
    public static MainApplication INSTANCE;
    public static AMapLocation myLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        init();
    }

    private void init() {
        //初始化数据库
//        GreenDaoManger.getInstance().initDB(getApplicationContext());
        //友盟初始化
//        initUM();
        //初始化网络框架
        NetWorkManager.getInstance().init();
        //初始化极光推送
//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);
        RetrofitService.init();
        initRxJavaError();
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        //停止定位服务
        GdLocationUtil.getInstance().destoryLocationService();
    }

    public static AMapLocation getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(AMapLocation myLocation) {
        this.myLocation = myLocation;
    }
}
