package com.product.sampling.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.product.sampling.R;
import com.qmuiteam.qmui.widget.QMUIVerticalTextView;

/**
 * Created by 陆伟 on 2019/11/5.
 * Copyright (c) 2019 . All rights reserved.
 */


public class LWVerticalTextView extends QMUIVerticalTextView {
    public LWVerticalTextView(Context context){
        this(context,null);
    }

    public LWVerticalTextView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public LWVerticalTextView(Context context,AttributeSet attrs,int defStyleAttr){
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
