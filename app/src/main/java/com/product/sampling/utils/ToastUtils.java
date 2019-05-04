package com.product.sampling.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * 避免同样的信息多次触发重复弹出的问题
 */
public class ToastUtils {

    private static Context sContext;
    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    private ToastUtils() {
        throw new RuntimeException("ToastUtils cannot be initialized");
    }

    public static void init(Context context) {
        sContext = context;
    }

    public static void showToast(String message) {

        if (TextUtils.isEmpty(message))return;


        //非Ui线程不能toast
        if (Thread.currentThread() != Looper.getMainLooper().getThread())return;

        if (toast == null) {
            toast = Toast.makeText(sContext, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
            oneTime = twoTime;
        }
    }

    public static void showToast(@StringRes int resId) {
        if (sContext == null)return;
        String str = sContext.getResources().getString(resId);
        showToast(str);
    }




}
