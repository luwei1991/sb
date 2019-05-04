package com.product.sampling.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import q.rorbin.badgeview.QBadgeView;

/**

 * 创建时间：2018/6/23
 * 描述：
 */
public class DefaultBadgeViewUtils {
    public static QBadgeView createDefaultBageView(Context context, int num) {
        QBadgeView qBadgeView = new QBadgeView(context);
        qBadgeView.setBadgeNumber(num);
        qBadgeView.stroke(Color.WHITE, 1, true);
        qBadgeView.setBadgeTextSize(10, true);
//        qBadgeView.setGravityOffset(10, 10, true);
        qBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        return qBadgeView;
    }
}
