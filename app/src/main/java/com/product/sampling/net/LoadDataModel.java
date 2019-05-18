package com.product.sampling.net;

public class LoadDataModel<T> extends LoadingStatus {


    private T data;


    public LoadDataModel(){
        super();
    }

    public LoadDataModel(T data) {
        super(LoadingStatus.SUCCESS);
        this.data = data;
    }



    public LoadDataModel(int code, String msg) {
        super(code, msg);
    }

    public T getData() {
        return data;
    }
}
