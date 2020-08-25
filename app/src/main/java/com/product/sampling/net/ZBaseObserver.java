package com.product.sampling.net;


import android.text.TextUtils;

import com.product.sampling.MainApplication;
import com.product.sampling.bean.UpdateEntity;
import com.product.sampling.httpmoudle.error.ApiException;
import com.product.sampling.httpmoudle.error.ExecptionEngin;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.ui.ActivityManager;
import com.product.sampling.ui.update.UpdateDialog;

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
        ApiException exception = ExecptionEngin.handleException(t);
        onFailure(exception.getCode(), exception.getDisplayMessage());
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
                case 207://版本需要更新
                    UpdateDialog dialog = new UpdateDialog(ActivityManager.getInstance().currentActivity());
                    String [] msg = message.split("#");
                    UpdateEntity updateEntity = new UpdateEntity();
                    updateEntity.setAppfileid(msg[0]);
                    if(!TextUtils.isEmpty(msg[1])){
                        updateEntity.setRemark(msg[1]);
                    }
                    dialog.show();
                    dialog.downLoadApk(updateEntity);
                    break;



            }
            if (isNeedShowtoast && code != 207) {
                ToastUtil.show(MainApplication.INSTANCE.getApplicationContext(), message);
            }
        }


    }


    public abstract void onSuccess(T t);

    public Disposable getDisposable() {
        return disposable;
    }

}
