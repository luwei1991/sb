package com.product.sampling.httpmoudle.manager;

import com.product.sampling.httpmoudle.interfaces.IRequestManager;

/**
 *
 * 创建时间：2018/5/26
 * 描述：
 */
public class HttpRequestManager {
    public static IRequestManager getRequestManager() {
        return OkHttpRequestManager.getInstance();
    }
}
