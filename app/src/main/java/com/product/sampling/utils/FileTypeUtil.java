package com.product.sampling.utils;


import java.io.File;

/**
 * Created by 陆伟 on 2020/5/7.
 * Copyright (c) 2020 . All rights reserved.
 */


public class FileTypeUtil {
    public static String APK_CONTENTTYPE = "application/vnd.android.package-archive";
    public static String PNG_CONTENTTYPE = "image/png";
    public static String JPG_CONTENTTYPE = "image/jpg";
    public static String DOC_CONTENTTYPE = "application/msword";
    public static String PDF_CONTENTTYPE = "application/pdf";
    public static String ZIP_CONTENTTYPE = "application/x-zip-compressed";
    public static String XLS_CONTENTTYPE = "application/x-xls";
    public static String DOCX_CONTENTTYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static String XLSX_CONTENTTYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String getMIMEType(File file){
        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        if(end.equals(".docx")){
            type = DOCX_CONTENTTYPE;
        }else if(end.equals(".doc")){
            type = DOC_CONTENTTYPE;
        }else if(end.equals(".pdf")){
            type = PDF_CONTENTTYPE;
        }else if(end.equals(".xls")){
            type = XLS_CONTENTTYPE;
        }else if(end.equals(".zip")){
            type = ZIP_CONTENTTYPE;
        }else if(end.equals(".xlsx")){
            type = XLSX_CONTENTTYPE;
        }

        return type;
    }
}
