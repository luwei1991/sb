package com.product.sampling.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.product.sampling.R;

/**
 * 自定义字体TextView
 * Created by 陆伟 on 2019/11/5.
 * Copyright (c) 2019 . All rights reserved.
 */


public class LWTextView extends AppCompatTextView {
    public LWTextView(Context context){
        this(context,null);
    }

    public LWTextView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public LWTextView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initTypefaceTextView(context,attrs);
    }

    /**
     * 初始化TextView
     */
    private void initTypefaceTextView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LWTextView);
        String type = typedArray.getString(R.styleable.LWTextView_typeface);
        if(type == null) return;
        Typeface typeface = null;
        switch (type){
            case "micro_light"://微软雅黑Light字体
                typeface = Typeface.createFromAsset(context.getAssets(),"MicrosoftLight.ttf");
                setTypeface(typeface);
                break;
            case "simsun"://宋体
                typeface = Typeface.createFromAsset(context.getAssets(),"simsun.ttf");
                setTypeface(typeface);
                break;
        }
        if(typedArray != null){
            typedArray.recycle();
        }
        typeface = null;

    }

}
