package com.product.sampling;

import android.os.Environment;

import java.io.File;

public class Constants {
    /**
    * 测试库外网
    */
//     public static String BASE_URL = "http://172.16.82.35:9080/exa/";

//    /**
//     * 正式库内网
//     */
//     public static String BASE_URL = "http://114.116.133.77:1025/";
    /**
     * 湖南测试库
     */
//    public static String BASE_URL = "http://172.16.82.103:9080/exa/";
    /**
     * 个人
     */
    public static String BASE_URL = "http://172.20.10.2:8080/";
//    public static String BASE_URL = "http://114.116.133.77:9084/";

    public static String IMAGE_BASE_URL = BASE_URL + "base/tBaFile/showImage?id=";
    public static String IMAGE_DOWNBASE_URL =BASE_URL + "base/tBaFile/downFile?id=";
    public static final boolean DEBUG = false;
    public static final String FLAVOR = "";

    public static String getPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
    /**
     * 默认PDF位置B
     */
    public static final String PDF_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/lWing/pdf";
    public static final String SIGN_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/lWing/simage/.nomedia";
    public static final String COMPRESS_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/lWing/compressimage/.nomedia";

}
