package com.product.sampling.net;

public class LoadingStatus {


    public static final int LOADING = 0;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;

    //0 loading 1 success 2 error
    public int loadingStatus;
    public int code;
    public String msg;

    public LoadingStatus() {
        loadingStatus = LOADING;
    }

    public LoadingStatus(int loadingStatus) {
        this.loadingStatus = loadingStatus;
    }

    public LoadingStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.loadingStatus = ERROR;
    }

    public int getLoadingStatus() {
        return loadingStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isLoading(){
        return loadingStatus == LOADING;
    }

    public boolean isSuccess(){
        return loadingStatus == SUCCESS;
    }

    public boolean isError(){
        return loadingStatus == ERROR;
    }
}
