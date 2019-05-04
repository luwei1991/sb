package com.product.sampling.httpmoudle.interfaces;

import java.io.File;

/**
 *
 * 创建时间：2018/5/26
 * 描述：
 */
public interface IDownloadCallback {
    abstract void onFinish(File downloadSuccessFile);

    abstract void onProgress(long currentBytes, long totalBytes);

    abstract void onFailure(String msgError);
}
