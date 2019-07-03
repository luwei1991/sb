package com.product.sampling.ui;

import android.app.Application;

import com.amap.api.location.AMapLocation;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.utils.GdLocationUtil;

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
    }

    private void initUM() {

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
