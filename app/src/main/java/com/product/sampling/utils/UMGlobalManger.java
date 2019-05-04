package com.product.sampling.utils;

import com.umeng.commonsdk.UMConfigure;

/**

 * 创建时间：2018/7/2
 * 描述：
 */
public class UMGlobalManger {
    private static final UMGlobalManger ourInstance = new UMGlobalManger();
//    private UMShareAPI umShareAPI;
//
//    public UMShareAPI getUmShareAPI() {
//        return umShareAPI;
//    }
//
//    public static UMGlobalManger getInstance() {
//        return ourInstance;
//    }
//
//    private UMGlobalManger() {
//        init();
//    }
//
//    public void init() {
//        //移动统计
//        UMConfigure.init(MainApplication.INSTANCE, null, null, UMConfigure.DEVICE_TYPE_PHONE, null);
//        //分享
//        umShareAPI = UMShareAPI.get(MainApplication.INSTANCE);
//        //第三方登录以及分享
//        PlatformConfig.setSinaWeibo("3163111192", "d6a50a54a967198b5e58e01fa92d448d", "http://sns.whalecloud.com");
//        if (BuildConfig.DEBUG) {
//            UMConfigure.setLogEnabled(true);
//        }else{
//            UMConfigure.setLogEnabled(false);
//        }
//    }
}
