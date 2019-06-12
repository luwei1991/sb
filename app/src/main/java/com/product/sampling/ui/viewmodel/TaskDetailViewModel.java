package com.product.sampling.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.product.sampling.bean.LocalMediaInfo;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.TaskMenu;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.utils.RxSchedulersHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 任务数据模型 生命周期和activity绑定
 */
public class TaskDetailViewModel extends AutoDisposViewModel {

    public TaskEntity taskEntity = new TaskEntity();

    public MutableLiveData<LoadDataModel<String>> orderLoadLiveData = new MutableLiveData<>();
    public MutableLiveData<LoadDataModel<TaskEntity>> orderDetailLiveData = new MutableLiveData<>();
    public MutableLiveData<LoadDataModel<List<TaskSample>>> sampleDetailLiveData = new MutableLiveData<>();


    public void requestOrderList(String symbol, String orderType, int page, boolean isRefrash) {

        orderLoadLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .getArea(null, null)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskMenu>>() {

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

                    @Override
                    public void onSuccess(List<TaskMenu> taskMenus) {
                        orderLoadLiveData.postValue(new LoadDataModel(taskMenus));
                    }
                });
    }

    public void requestOrderList(String userid, String id) {

        orderDetailLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .taskdetail(userid, id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<TaskEntity>() {
                    @Override
                    public void onSuccess(TaskEntity entity) {
                        orderDetailLiveData.setValue(new LoadDataModel(entity));
                    }
                });
    }

    public void requestTasksamples(String userid, String id) {

        sampleDetailLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .tasksamples(userid, id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskSample>>() {
                    @Override
                    public void onSuccess(List<TaskSample> entity) {
                        sampleDetailLiveData.setValue(new LoadDataModel(entity));
                    }
                });
    }

}
