package com.product.sampling.ui.eventmessage;

/**
 * Created by 陆伟 on 2019/11/28.
 * Copyright (c) 2019 . All rights reserved.
 */


public class CurDeleteSampleItemMessage {
    private int curTaskPost;
    private int curPost;
    public CurDeleteSampleItemMessage(int curPost, int curTaskPost){
        this.curPost = curPost;
        this.curTaskPost = curTaskPost;
    }

    public int getCurPost() {
        return curPost;
    }

    public void setCurPost(int curPost) {
        this.curPost = curPost;
    }

    public int getCurTaskPost() {
        return curTaskPost;
    }

    public void setCurTaskPost(int curTaskPost) {
        this.curTaskPost = curTaskPost;
    }
    
}
