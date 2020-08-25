package com.product.sampling.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 陆伟 on 2019/11/12.
 * Copyright (c) 2019 . All rights reserved.
 */


public class TaskCompany implements Serializable {
    private String item;
    private String value;
    private List<TaskCompany> child;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<TaskCompany> getChild() {
        return child;
    }

    public void setChild(List<TaskCompany> child) {
        this.child = child;
    }
}
