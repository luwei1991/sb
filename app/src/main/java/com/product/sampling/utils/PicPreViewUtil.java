package com.product.sampling.utils;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.product.sampling.R;
import com.product.sampling.manager.GlideManager;
import com.qmuiteam.qmui.layout.QMUIFrameLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.popup.QMUIFullScreenPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

/**
 * Created by 陆伟 on 2020/5/8.
 * Copyright (c) 2020 . All rights reserved.
 */


public class PicPreViewUtil {


    public static void showPreView(View v, Context context, String imgPath){
        QMUIFrameLayout frameLayout = new QMUIFrameLayout(context);
        frameLayout.setBackground(
                QMUIResHelper.getAttrDrawable(context, R.attr.qmui_skin_support_popup_bg));
        frameLayout.setRadius(QMUIDisplayHelper.dp2px(context, 12));
        int padding = QMUIDisplayHelper.dp2px(context, 20);
        frameLayout.setPadding(padding, padding, padding, padding);

        final ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);//铺满屏幕
        GlideManager.getInstance().ImageLoad(context,imgPath,imageView,true);

        int size = QMUIDisplayHelper.dp2px(context, 500);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(1500, 800);
        frameLayout.addView(imageView, lp);

        QMUIPopups.fullScreenPopup(context)
                .addView(frameLayout)
                .closeBtn(true)
                .onBlankClick(new QMUIFullScreenPopup.OnBlankClickListener() {
                    @Override
                    public void onBlankClick(QMUIFullScreenPopup popup) {
                    }
                })
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                    }
                })
                .show(v);
    }
}
