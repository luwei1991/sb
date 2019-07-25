package com.product.sampling.utils;

import android.content.Context;
import android.content.Intent;

import com.product.sampling.ui.MainActivity;
import com.product.sampling.ui.MainTaskListActivity;

/**
 * 创建时间：2018/5/30
 * 描述：Activity跳转工具类
 */
public class ActivityUtils {

    /**
     * MainTaskActivity
     *
     * @param context
     */
    public static void goMainTaskActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
