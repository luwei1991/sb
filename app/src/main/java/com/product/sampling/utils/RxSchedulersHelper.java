package com.product.sampling.utils;


import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.error.ServerException;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gdy on 2018/11/8
 * Describe:
 */


public class RxSchedulersHelper {


    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> io() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public static <T> FlowableTransformer<T, T> f_io() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }


    public static <T> FlowableTransformer<T, T> f_io_main() {
        return (followable) -> followable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<BaseHttpResult<T>, T> f_handResult() {
        return (upstream) -> upstream.flatMap(result -> {
            if (result.isSuccess()) {
                return Flowable.just(result.data);
            } else {
                return Flowable.error(new ServerException(result.code, result.message));
            }
        });
    }


    public static <T> ObservableTransformer<BaseHttpResult<T>, T> ObsHandHttpResult() {
        return (upstream) -> upstream.flatMap(result -> {
            if (result.isSuccess()) {
                return Observable.just(result.data);
            } else {
                return Observable.error(new ServerException(result.code, result.message));
            }
        });
    }

}
