package com.product.sampling.ui.form.utils;

import android.text.TextUtils;

import com.product.sampling.ui.form.ReType;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 陆伟 on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 */


public class FormSubmitUtil {
    public static MultipartBody.Builder getMultBody(Object object){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        Class obClass = object.getClass();
        Field[] fields = obClass.getDeclaredFields();
        for (Field fields1 : fields) {
            try {
                ReType reType = fields1.getAnnotation(ReType.class);
                fields1.setAccessible(true);
                Object value = fields1.get(object);
                if (reType != null) {
                    if (value != null) {
                        if(value instanceof  List){
                            List<String> imgList = (List<String>) value;
                            for(int i = 0; i < imgList.size(); i ++){
                                addFilePart(fields1.getName()+"[" + i + "]",multipartBodyBuilder,imgList.get(i));
//                                File pngFile = new File(imgList.get(i));
//                                RequestBody pngFileBody = RequestBody.create(MultipartBody.FORM, pngFile);
//                                multipartBodyBuilder.addFormDataPart(fields1.getName()+"[" + i + "]", pngFile.getName(), pngFileBody);
                            }
                        }else{
                            addFilePart(fields1.getName(),multipartBodyBuilder,(String) value);
//                            File pngFile = new File((String) value);
//                            RequestBody pngFileBody = RequestBody.create(MultipartBody.FORM, pngFile);
//                            multipartBodyBuilder.addFormDataPart(fields1.getName(), pngFile.getName(), pngFileBody);
                        }

                    }
                } else {
                    multipartBodyBuilder.addFormDataPart(fields1.getName(), checkNullString((String) value));
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;

            }

        }

        return multipartBodyBuilder;

    }

    /**
     * 将地址转换成文件提交
     * @param multipartBodyBuilder
     * @param imgPath
     */
    private static void addFilePart(String tagString,MultipartBody.Builder multipartBodyBuilder,String imgPath){
        File pngFile = new File(imgPath);
        RequestBody pngFileBody = RequestBody.create(MultipartBody.FORM, pngFile);
        multipartBodyBuilder.addFormDataPart(tagString, pngFile.getName(), pngFileBody);
    }

    private static String checkNullString(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        } else {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}
