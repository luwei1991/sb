package com.product.sampling.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.product.sampling.R;

public class ToastUtil {
    private static Toast toast;

    private static View view;

    private static TextView textView;

    private ToastUtil() {
    }

    private static void getToast(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.toast_layout, null);
            textView = view.findViewById(R.id.toast_tv);
        }
        toast.setView(view);
    }

    public static void showShortToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, CharSequence msg, int duration) {
        try {
            getToast(context);
//            toast.setText(msg);
            textView.setText(msg);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }


    private static void showToast(Context context, int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast(context);
            toast.setText(resId);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

//    public static void showBigToast(Context context, String msg) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        //自定义布局
//        view = inflater.inflate(R.layout.toast_layout, null);
//        //自定义toast文本
//        TextView mTextView = (TextView) view.findViewById(R.id.toast_tv);
//        mTextView.setText(msg);
//        Log.i("ToastUtil", "Toast start...");
//        if (toast == null) {
//            toast = new Toast(context.getApplicationContext());
//            Log.i("ToastUtil", "Toast create...");
//        }
//        //设置toast居中显示
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(view);
//        toast.show();
//    }

    public static void showToast(Context context, String msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }
}