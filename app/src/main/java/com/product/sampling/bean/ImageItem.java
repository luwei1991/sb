package com.product.sampling.bean;

/**
 * 创建时间：2018/7/3
 * 描述：
 */
public class ImageItem extends CommonBean {

    private String title;
    private int res;

    public ImageItem(String text, int res) {
        this.res = res;
        this.title = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
