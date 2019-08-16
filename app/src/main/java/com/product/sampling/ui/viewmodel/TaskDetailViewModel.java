package com.product.sampling.ui.viewmodel;

import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.product.sampling.bean.FastMail;
import com.product.sampling.bean.TaskBean;
import com.product.sampling.bean.UpdateEntity;
import com.product.sampling.bean.TaskCity;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskProvince;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.LoadListDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.update.UpdateDialogFragment;
import com.product.sampling.utils.AppUtils;
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
    public MutableLiveData<LoadDataModel<FastMail>> fastMailLiveData = new MutableLiveData<>();
    public MutableLiveData<LoadDataModel<TaskBean>> dataListLiveData = new MutableLiveData<>();


    public void requestCityList(boolean isNeedAddAll) {

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
                        if (isNeedAddAll) {
                            TaskProvince taskProvince = new TaskProvince();
                            taskProvince.name = "全部";
                            taskProvince.shicitys = new ArrayList<>();
                            TaskCity taskCity = new TaskCity();
                            taskCity.name = "全部";
                            taskProvince.shicitys.add(taskCity);
                            taskProvinces.add(0, taskProvince);
                        }
                        cityListLiveData.postValue(new LoadDataModel(taskProvinces));
                    }
                });
    }

    public void requestDetailList(String userid, String id) {
        if (TextUtils.isEmpty(userid)) {
            return;
        }
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
        if (TextUtils.isEmpty(userid)) {
            return;
        }
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
        if (TextUtils.isEmpty(userid)) {
            return;
        }
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

    public void checkVersion(Context context, FragmentManager fragmentManager) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }

        RetrofitService.createApiService(Request.class)
                .getAppVersion(userid, AppUtils.getVersionName(context))
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<UpdateEntity>() {
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }

                    @Override
                    public void onSuccess(UpdateEntity result) {
                        if (null != result && "1".equals(result.getIsnew())) {
                            UpdateDialogFragment.newInstance(result).show(fragmentManager, "update");
                        }
                    }
                });
    }

    public void getFastMail(String taskId, String sampleId) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        fastMailLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .getFastMail(userid, sampleId, taskId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<FastMail>() {
                    @Override
                    public void onFailure(int code, String message) {
//                        super.onFailure(code, message);
                        fastMailLiveData.postValue(new LoadDataModel<>(code, message));
                    }

                    @Override
                    public void onSuccess(FastMail result) {
                        fastMailLiveData.setValue(new LoadDataModel(result));
                    }
                });
    }

    public void sendReportRecord(String taskId, String sampleId, String reporttype) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        fastMailLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .sendReportRecord(userid, sampleId, taskId, reporttype)
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {
                    @Override
                    public void onFailure(int code, String message) {
//                        super.onFailure(code, message);
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {

                    }
                });
    }

    public void getDataByPage(Context context, int pageNo, String status, String ordertype, String cityid) {
        dataListLiveData.setValue(new LoadDataModel());
        RetrofitService.createApiService(Request.class)
                .taskList(AccountManager.getInstance().getUserId(), status, ordertype, cityid, pageNo)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<TaskBean>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        dataListLiveData.setValue(new LoadDataModel(code, message));
                    }

                    @Override
                    public void onSuccess(TaskBean taskBean) {
                        dataListLiveData.setValue(new LoadDataModel(taskBean));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });

    }

    public void uploadtaskinfo(String id) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        RetrofitService.createApiService(Request.class)
                .uploadtaskinfo(userid, id)
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {
                    @Override
                    public void onFailure(int code, String message) {
//                        super.onFailure(code, message);
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {

                    }
                });
    }
}
