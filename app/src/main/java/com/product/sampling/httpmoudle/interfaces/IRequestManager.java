package com.product.sampling.httpmoudle.interfaces;

import java.util.Map;

/**
 *
 * 创建时间：2018/5/26
 * 描述：
 */
public interface IRequestManager {
    abstract void get(String url, Map<String, String> params, IRequestCallback requestCallback);

    abstract void post(String url, Map<String, String> params, IRequestCallback requestCallback);

    abstract void download(String url, String fileDir, String fileName, IDownloadCallback downloadCallback);
}
