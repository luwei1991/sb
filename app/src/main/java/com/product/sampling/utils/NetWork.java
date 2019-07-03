package com.product.sampling.utils;

import com.product.sampling.ui.MainApplication;
import com.product.sampling.maputil.ToastUtil;

/**
 * @author wlj
 * @date 2017/3/29
 * @email wanglijundev@gmail.com
 * @packagename wanglijun.vip.androidutils.utils
 * @desc: 判断手机网络类型，是否连接
 */

public class NetWork {
    public static void internal() {
        int networkType = NetWorkUtils.getNetworkType(MainApplication.INSTANCE);
        String networkTypeName = NetWorkUtils.getNetworkTypeName(MainApplication.INSTANCE);
        if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_WIFI)) {
            ToastUtil.showShortToast(MainApplication.INSTANCE, "你目前处于wifi网络");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
            ToastUtil.showShortToast(MainApplication.INSTANCE, "你目前处于断网状态");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_3G)) {
            ToastUtil.showShortToast(MainApplication.INSTANCE, "你目前处于3G状态");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_2G)) {
            ToastUtil.showShortToast(MainApplication.INSTANCE, "你目前处于2G网络");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_WAP)) {
            ToastUtil.showShortToast(MainApplication.INSTANCE, "你目前处于企业网");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_UNKNOWN)) {
            ToastUtil.showShortToast(MainApplication.INSTANCE, "你目前网络类型不知道");
        }
    }
}
