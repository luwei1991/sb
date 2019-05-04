package com.product.sampling.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {
    @SerializedName("title")
    public String title;
    @SerializedName("conent")
    public String conent;
    @SerializedName("pubdate")
    public String pubdate;
    @SerializedName("imgurl")
    public String imgurl;

    public String getTitle() {
        return title;
    }

    public String getConent() {
        return conent;
    }

    public String getPubdate() {
        return pubdate;
    }

    public String getImgurl() {
        return imgurl;
    }
}

