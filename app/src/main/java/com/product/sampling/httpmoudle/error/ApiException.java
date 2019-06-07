package com.product.sampling.httpmoudle.error;

import android.text.TextUtils;

/**
 * DevBase
 * author:wangqiong on 16/9/7.
 * mail:wqandroid@gmail.com
 */

public class ApiException extends Exception {

    private int code;
    private String displayMessage;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;

    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        if (TextUtils.isEmpty(displayMessage)){
            return getLocalizedMessage();
        }
        return displayMessage;
    }

    public int getCode() {
        return code;
    }





}
