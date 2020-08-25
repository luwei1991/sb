package com.product.sampling.ui.widget;

import android.graphics.Color;

/**
 * Created by 陆伟 on 2019/11/25.
 * Copyright (c) 2019 . All rights reserved.
 */


public class ListTipItem {
    private String title;

    private int textColor = Color.WHITE;

    public ListTipItem(String title) {
        this.title = title;
    }

    public ListTipItem(String title, int textColor) {
        this.title = title;

        this.textColor = textColor;
    }

    public String getTitle() {
        return title;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
