package com.product.sampling.net;


import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.Exception.ServerException;
import com.product.sampling.utils.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by gongdongyang on 2018/7/19.
 */

public abstract class ZBaseObserver<T> implements Observer<T> {

    private final String TAG = ZBaseObserver.class.getSimpleName();


    public final int RESPONSE_CODE_FAILED = -1; //返回数据失败

    private Disposable disposable;


    private boolean isNeedShowtoast = true;

    public ZBaseObserver(boolean isNeedShowtoast) {
        this.isNeedShowtoast = isNeedShowtoast;
    }

    public ZBaseObserver() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }


    @Override
    public void onError(Throwable t) {
        ServerException exception = (ServerException) t;
        onFailure(exception.getCode(), exception.getMsg());
    }


    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onComplete() {

    }

    /**
     * 网络请求失败
     *
     * @param code
     * @param message
     */
    public void onFailure(int code, String message) {


        if (code == RESPONSE_CODE_FAILED) {
            //业务没有正常返回数据
        } else {
            //网络异常 其他 错误信息
            switch (code) {

                case 123:
                    break;
                case 2010:
                    // 当业务接口或者http 接口报 登陆信息过期则启动到登陆页面

                    //发送登录信息过期事件

                    break;

            }
            if (isNeedShowtoast) {
                ToastUtils.showToast(message);
            }
        }


    }


    public abstract void onSuccess(T t);

    public Disposable getDisposable() {
        return disposable;
    }

}
