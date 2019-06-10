package com.product.sampling;

import android.os.Environment;

import com.product.sampling.BuildConfig;

import java.io.File;

public class Constants {

    public static String BASE_URL = "http://lw123.longwi.com:9080/exa/";
//    public static String BASE_URL = "http://ajfnc2.natappfree.cc/";


    public static String IMAGE_BASE_URL = BASE_URL + "base/tBaFile/showImage?id=";
    public static final boolean DEBUG = true;
    public static final String FLAVOR = "";

    public static String getPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
}
