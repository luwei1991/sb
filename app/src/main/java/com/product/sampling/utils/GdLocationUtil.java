package com.product.sampling.utils;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.product.sampling.ui.MainApplication;

/**
 * 创建时间：2018/6/7
 * 描述：
 */
public class GdLocationUtil {
    private static final GdLocationUtil ourInstance = new GdLocationUtil();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    public static GdLocationUtil getInstance() {
        return ourInstance;
    }

    private GdLocationUtil() {
        init();
    }

    private void init() {
        mLocationClient = new AMapLocationClient(MainApplication.INSTANCE.getApplicationContext());
    }

    /**
     * 单次定位
     */
    private void initOnceAmapLocationClientOption() {
        if (mLocationOption != null) {
            mLocationOption = null;
        }
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 连续定位
     */
    private void initContinueAmapLocationClientOption() {
        if (mLocationOption != null) {
            mLocationOption = null;
        }
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setHttpTimeOut(20000);
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(60 * 2000);
    }


    /**
     * 开启单次定位
     *
     * @param aMapLocationListener
     */
    public void startOnceLocation(AMapLocationListener aMapLocationListener) {
        initOnceAmapLocationClientOption();
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(aMapLocationListener);
        mLocationClient.startLocation();
    }

    /**
     * 开启连续定位
     *
     * @param aMapLocationListener
     */
    public void startContinueLocation(AMapLocationListener aMapLocationListener) {
        initContinueAmapLocationClientOption();
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(aMapLocationListener);
        mLocationClient.startLocation();
    }

    public void stopLoaction() {
        if (mLocationOption != null) {
            mLocationOption = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    /**
     * 销毁定位服务
     */
    public void destoryLocationService() {
        stopLoaction();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

}
