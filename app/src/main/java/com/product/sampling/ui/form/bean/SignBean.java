package com.product.sampling.ui.form.bean;

import java.io.Serializable;

/**
 * Created by 陆伟 on 2020/3/18.
 * Copyright (c) 2020 . All rights reserved.
 */


public class SignBean implements Serializable {
    private String imgType;
    private String id;

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
