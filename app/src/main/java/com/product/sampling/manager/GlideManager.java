package com.product.sampling.manager;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * Created by 陆伟 on 2019/11/20.
 * Copyright (c) 2019 . All rights reserved.
 */


public class GlideManager {
    private static GlideManager instance;

    private GlideManager(){

    }

    public static GlideManager getInstance(){
        if(instance == null){
            synchronized (GlideManager.class){
                if(instance == null){
                    instance = new GlideManager();
                }
            }
        }
        return instance;
    }

    public void ImageLoad(Context context,String imagePath, ImageView imageView, boolean notCache){
        RequestOptions options = new RequestOptions().skipMemoryCache(notCache).diskCacheStrategy(DiskCacheStrategy.NONE);
        File file = new File(imagePath);
        Glide.with(context).load(file).apply(options).into(imageView);
    }


}
