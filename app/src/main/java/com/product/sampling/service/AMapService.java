//package com.product.sampling.service;
//
//import android.annotation.SuppressLint;
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.os.Binder;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.SystemClock;
//import android.util.Log;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.product.sampling.MainApplication;
//import com.product.sampling.R;
//import com.product.sampling.utils.Utils;
//
//import androidx.annotation.Nullable;
//
///**
// * Created by 陆伟 on 2020/4/8.
// * Copyright (c) 2020 . All rights reserved.
// */
//
//
//public class AMapService extends Service{
//
//
////    public class MapBinder extends Binder{
////        public AMapService getService() {
////            return AMapService.this;
////        }
////    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//
//    public void doNotBackground(){//onResume:true
//
//    }
//
//    public void doBackground(){
//
//    }
//
//
//
//    /**
//     * 定位监听
//     */
//    AMapLocationListener locationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation location) {
//
//        }
//    };
//
//
//
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//    }
//}
