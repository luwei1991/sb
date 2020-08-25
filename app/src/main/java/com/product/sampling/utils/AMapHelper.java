package com.product.sampling.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.product.sampling.MainApplication;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;

import io.reactivex.disposables.Disposable;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by 陆伟 on 2020/4/8.
 * Copyright (c) 2020 . All rights reserved.
 */


public class AMapHelper {
    private volatile static AMapHelper aMapHelper = null;
    private Context context;
    int alarmInterval = 5;//唤醒周期
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Intent alarmIntent = null;
    private PendingIntent alarmPi = null;
    private AlarmManager alarm = null;
    private BroadcastReceiver alarmReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("LOCATION")){
                if(null != locationClient){
                    locationClient.startLocation();
                }
            }
        }
    };

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                //开始定位
                case Utils.MSG_LOCATION_START:
                    break;
                // 定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = Utils.getLocationStr(loc);
                    MainApplication.INSTANCE.setMyLocation(loc);
                    if(AccountManager.getInstance().getUserInfoBean() != null){
                        uploadaddress(AccountManager.getInstance().getUserId(),loc.getLatitude(), loc.getLongitude());
                    }
//                    Log.e("lwlw",loc.getCity());
                    break;
                //停止定位
                case Utils.MSG_LOCATION_STOP:
                    break;
                default:
                    break;
            }
        };
    };
    private AMapHelper(Context context){
        this.context = context;
        initLocation();
    }

    public static AMapHelper getInstance(Context context) {
        if (aMapHelper == null) {
            synchronized (AMapHelper.class) {
                if (aMapHelper == null) {
                    aMapHelper = new AMapHelper(context);
                }
            }
        }
        return aMapHelper;
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(context.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (null != aMapLocation) {
                    if(aMapLocation.getErrorCode() == 0){
                        Message msg = mHandler.obtainMessage();
                        msg.obj = aMapLocation;
                        msg.what = Utils.MSG_LOCATION_FINISH;
                        mHandler.sendMessage(msg);
//                        Log.e("lwlw",aMapLocation.getCity());
                    }else{
//                        Log.e("lwlw","location Error, ErrCode: "+ aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                    }

                }
            }
        });
        // 创建Intent对象，action为LOCATION
        alarmIntent = new Intent();
        alarmIntent.setAction("LOCATION");
//        IntentFilter ift = new IntentFilter();
        // 定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        // 也就是发送了action 为"LOCATION"的intent
        alarmPi = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        // AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        context.registerReceiver(alarmReceiver, filter);

        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);

        if(null != alarm){
            //设置一个闹钟，2秒之后每隔一段时间执行启动一次定位程序
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2*1000,
                    alarmInterval * 1000, alarmPi);
        }

    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(AccountManager.getInstance().getInterval()*1000);//可选，设置定位间隔。默认为2秒
//        mOption.setInterval(4000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

//    public AMapLocationClient getLocationClient(){
//        return locationClient;
//    }

    public void destory(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        if(null != alarmReceiver){
            context.unregisterReceiver(alarmReceiver);
            alarmReceiver = null;
        }
    }

    private void uploadaddress(String userid, double latitude, double longitude) {
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .uploadaddress(userid, longitude + "", latitude + "")
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
    }
}
