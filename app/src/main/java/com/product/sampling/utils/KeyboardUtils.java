package com.product.sampling.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.product.sampling.ui.MainApplication;

/**

 * 创建时间：2018/7/3
 * 描述：
 */
public class KeyboardUtils {
    /**
     * 弹出键盘
     *
     * @param view
     */
    public static void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) MainApplication.INSTANCE.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * 关闭键盘
     *
     * @param activity
     */
    public static void closeKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) MainApplication.INSTANCE.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
