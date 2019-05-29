package com.product.sampling.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class New {
    @SerializedName("id")
    public String id;
    @SerializedName("newstitle")
    public String title;
    @SerializedName("newstext")
    public String conent;
    @SerializedName("createTime")
    public String pubdate;
    @SerializedName("newspic")
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

