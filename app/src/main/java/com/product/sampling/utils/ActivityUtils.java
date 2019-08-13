package com.product.sampling.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;

import com.product.sampling.bean.Pics;
import com.product.sampling.ui.MainActivity;
import com.product.sampling.ui.MainTaskListActivity;
import com.product.sampling.ui.PhotoViewActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public static void goPhotoViewActivity(Context context, List<Pics> paths, int currentIndex) {
        Intent imgIntent = new Intent(context, PhotoViewActivity.class);
        ArrayList list = new ArrayList();
        for (Pics pic : paths) {
            if (!TextUtils.isEmpty(pic.getId())) {
                list.add(pic.getId());
            } else if (!TextUtils.isEmpty(pic.getOriginalPath())) {
                list.add(pic.getOriginalPath());
            }
        }
        if (list.isEmpty()) {
            return;
        }
        imgIntent.putExtra("paths", list);
        imgIntent.putExtra("title", "图片");
        imgIntent.putExtra("position", currentIndex);
        context.startActivity(imgIntent);
    }


}
