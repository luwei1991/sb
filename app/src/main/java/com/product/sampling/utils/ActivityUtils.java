package com.product.sampling.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.product.sampling.bean.Pics;
import com.product.sampling.ui.MainActivity;
import com.product.sampling.ui.PhotoViewActivity;

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
                imgIntent.putExtra("remote", "1");

            } else if (!TextUtils.isEmpty(pic.getOriginalPath())) {
                list.add(pic.getOriginalPath());
                imgIntent.putExtra("remote", "0");
            }
        }
        if (list.isEmpty()) {
            Toast.makeText(context,"您暂时还没有选择图片，无图片详情！",Toast.LENGTH_LONG).show();
            return;
        }
        imgIntent.putExtra("paths", list);
        imgIntent.putExtra("title", "图片");
        imgIntent.putExtra("position", currentIndex);
        context.startActivity(imgIntent);
    }


}
