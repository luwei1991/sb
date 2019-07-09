package com.product.sampling.bean;

public class UploadModel {
    int cutNum;
   String path;
    int cutSize;
    int start;
    public int getCutNum() {
        return cutNum;
    }

    public void setCutNum(int cutNum) {
        this.cutNum = cutNum;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCutSize() {
        return cutSize;
    }

    public void setCutSize(int cutSize) {
        this.cutSize = cutSize;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
