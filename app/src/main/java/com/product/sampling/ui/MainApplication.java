package com.product.sampling.ui;

import android.app.Application;
import android.util.Log;

import com.product.sampling.net.NetWorkManager;
import com.product.sampling.utils.GdLocationUtil;
import com.product.sampling.utils.ToastUtils;

public class MainApplication extends Application {
    public static MainApplication INSTANCE;

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

        ToastUtils.init(MainApplication.INSTANCE);
    }

    private void initUM() {

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i("mzh", "onTerminate");
        //停止定位服务
        GdLocationUtil.getInstance().destoryLocationService();
    }
}
