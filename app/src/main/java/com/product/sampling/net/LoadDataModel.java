package com.product.sampling.net;

public class LoadDataModel<T> extends LoadingStatus {


    private T data;
    private boolean isSearch;


    public LoadDataModel(){
        super();
    }
    public LoadDataModel(T data){
        super(LoadingStatus.SUCCESS);
        this.data = data;
    }
    public LoadDataModel(T data,boolean isSearch) {
        super(LoadingStatus.SUCCESS);
        this.data = data;
        this.isSearch = isSearch;
    }



    public LoadDataModel(int code, String msg,boolean isSearch) {
        super(code, msg);
    }

    public T getData() {
        return data;
    }
}
