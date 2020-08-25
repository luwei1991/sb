package com.product.sampling.net;


/**
 * 可以携带当前的下啦刷新状态的load指示器
 * @param <T>
 */
public class LoadListDataModel<T> extends LoadDataModel<T> {


    //是否是刷新
    private boolean isPullRefresh;
    //是否是loadmore
    private boolean isLoadMore;




    public LoadListDataModel(boolean isPullRefresh, boolean isLoadMore) {
        super();
        this.isPullRefresh = isPullRefresh;
        this.isLoadMore = isLoadMore;
    }

    public LoadListDataModel(T data, boolean isPullRefresh, boolean isLoadMore) {
        super(data);
        this.isPullRefresh = isPullRefresh;
        this.isLoadMore = isLoadMore;
    }

    public LoadListDataModel(int code, String msg, boolean isPullRefresh, boolean isLoadMore) {
        super(code, msg,false);
        this.isPullRefresh = isPullRefresh;
        this.isLoadMore = isLoadMore;
    }
}
