package com.product.sampling.ui.eventmessage;

/**
 * Created by 陆伟 on 2019/11/28.
 * Copyright (c) 2019 . All rights reserved.
 */


public class CurItemMessage {
    private int curPost;
    public CurItemMessage(int curPost){
        this.curPost = curPost;

    }

    public int getCurPost() {
        return curPost;
    }

    public void setCurPost(int curPost) {
        this.curPost = curPost;
    }
}
