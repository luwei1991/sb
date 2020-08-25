package com.product.sampling.httpmoudle.error;

public class ServerException extends RuntimeException {


    private int code;
    private String msg;
    public String info;

    public ServerException(int code, String msg,String info) {
        this.code = code;
        this.msg = msg;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
