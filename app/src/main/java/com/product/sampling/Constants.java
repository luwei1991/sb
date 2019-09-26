package com.product.sampling;

import android.os.Environment;

import com.product.sampling.BuildConfig;

import java.io.File;

public class Constants {

/*        public static String BASE_URL = "http://lw123.longwi.com:9080/exa/";*/
     public static String BASE_URL = "http://172.16.88.26:8080/";
/*       public static String BASE_URL = "http://114.116.133.77:1025/";*/
/*      public static String BASE_URL = "http://172.16.82.33:9080/exa/";*/


    public static String IMAGE_BASE_URL = BASE_URL + "base/tBaFile/showImage?id=";
    public static String IMAGE_DOWNBASE_URL =BASE_URL + "base/tBaFile/downFile?id=";
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
