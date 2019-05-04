package com.product.sampling.utils;

import android.content.Context;

import com.product.sampling.ui.MainApplication;

/**

 * 创建时间：2018/7/3
 * 描述：
 */
public class PreferenceUtils extends BasePreference {
    private static PreferenceUtils preferenceUtils;
    /**
     * 需要增加key就在这里新建
     */
    //用户名的key
    public static String USER_NAME = "user_name";
    //是否首次启动的key
    private String FIRST_LAUNCH = "first_launch";

    public static String USER_CITY = "user_city";

    public static String USER_ADDRESS = "user_address";


    private PreferenceUtils(Context context) {
        super(context);
    }

    /**
     * 这里我通过自定义的Application来获取Context对象，所以在获取preferenceUtils时不需要传入Context。
     *
     * @return
     */
    public synchronized static PreferenceUtils getInstance() {
        if (null == preferenceUtils) {
            preferenceUtils = new PreferenceUtils(MainApplication.INSTANCE);
        }
        return preferenceUtils;
    }

    public void setFirstLaunch(boolean isFirst) {
        setBoolean(FIRST_LAUNCH, isFirst);
    }

    public boolean getFirstlaunch() {
        return getBoolean(FIRST_LAUNCH);
    }

    public void setStringValue(String key, String value) {
        setString(key, value);
    }

    public String getStringValue(String key) {
        return getString(key);
    }

}
