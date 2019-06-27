package com.product.sampling.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.product.sampling.bean.TaskCity;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskProvince;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.utils.RxSchedulersHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 任务数据模型 生命周期和activity绑定
 */
public class TaskDetailViewModel extends AutoDisposViewModel {

    public TaskEntity taskEntity = new TaskEntity();

    public MutableLiveData<LoadDataModel<ArrayList<TaskProvince>>> cityListLiveData = new MutableLiveData<>();
    public MutableLiveData<LoadDataModel<TaskEntity>> orderDetailLiveData = new MutableLiveData<>();
    public MutableLiveData<LoadDataModel<List<TaskSample>>> sampleDetailLiveData = new MutableLiveData<>();


    public void requestCityList() {

        cityListLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .getArea()
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskProvince>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        addDispos(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        cityListLiveData.postValue(new LoadDataModel<>(code, message));
                    }

                    @Override
                    public void onSuccess(List<TaskProvince> taskProvinces) {
                        TaskProvince taskProvince = new TaskProvince();
                        taskProvince.name = "全部";
                        taskProvince.shicitys = new ArrayList<>();
                        TaskCity taskCity = new TaskCity();
                        taskCity.name = "全部";
                        taskProvince.shicitys.add(taskCity);
                        taskProvinces.add(0, taskProvince);
                        cityListLiveData.postValue(new LoadDataModel(taskProvinces));
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

    public void uploadaddress(String userid, double latitude, double longitude) {
        RetrofitService.createApiService(Request.class)
                .uploadaddress(userid, longitude + "", latitude + "")
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {

                    }
                });
    }

}
