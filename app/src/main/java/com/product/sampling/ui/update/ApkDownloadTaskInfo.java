package com.product.sampling.ui.update;

import java.io.Serializable;

/**
 * newto
 * author:wangqiong on 2018/3/8.
 * mail:wqandroid@gmail.com
 */

public class ApkDownloadTaskInfo implements Serializable {


    public String apkUrl;
    public String apkVer;
    public String apkLocalPath;

    //h5的下载路径
    public String getApkFileName(){
        return "v"+ apkVer+"-sample.apk";
    }

    //h5下载的落地页
    public String getH5DownPage(){
        return "www.baidu.com";
    }
}
