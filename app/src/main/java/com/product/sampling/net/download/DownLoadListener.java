package com.product.sampling.net.download;

/**
 * Created by 陆伟 on 2020/3/3.
 * Copyright (c) 2020 . All rights reserved.
 */


public interface DownLoadListener {
    void onStart();//下载开始

    void onProgress(int progress);//下载进度

    void onFinish(String path);//下载完成

    void onFail(String errorInfo);//下载失败
}
