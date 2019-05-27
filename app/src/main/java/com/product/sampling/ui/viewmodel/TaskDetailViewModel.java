package com.product.sampling.ui.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.TaskMenu;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.ui.viewmodel.AutoDisposViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class TaskDetailViewModel extends AutoDisposViewModel {
    public TaskEntity taskEntity = new TaskEntity();
    public List<TaskSample> taskList = new ArrayList<>();
    public ArrayList<TaskImageEntity> imageList = new ArrayList<>();
    public ArrayList<LocalMedia> videoList = new ArrayList<>();

    public MutableLiveData<LoadDataModel<String>> orderLoadLiveData = new MutableLiveData<>();

    public void requestOrderList(String symbol, String orderType, int page, boolean isRefrash) {

        //未登录判断

        orderLoadLiveData.setValue(new LoadDataModel());
        NetWorkManager.getRequest().getArea(null, null)
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(new ZBaseObserver<TaskMenu>() {
                    @Override
                    public void onSuccess(TaskMenu balanceEntity) {
                        orderLoadLiveData.postValue(new LoadDataModel(balanceEntity));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        addDispos(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        orderLoadLiveData.postValue(new LoadDataModel<>(code, message));
                    }
                });
    }

}
