package com.product.sampling.ui.viewmodel;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.product.sampling.bean.FastMail;
import com.product.sampling.bean.TaskBean;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.utils.RxSchedulersHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;

//import com.product.sampling.bean.TaskCity;

/**
 * 任务数据模型 生命周期和activity绑定
 */
public class TaskDetailViewModel extends ViewModel {

    public TaskEntity taskEntity = new TaskEntity();
//    public MutableLiveData<LoadDataModel<ArrayList<TaskProvince>>> cityListLiveData = new MutableLiveData<>();
    public UnPeekLiveData<LoadDataModel<TaskEntity>> orderDetailLiveData = new UnPeekLiveData<>();
    public UnPeekLiveData<LoadDataModel<List<TaskSample>>> sampleDetailLiveData = new UnPeekLiveData<>();
    public UnPeekLiveData<LoadDataModel<FastMail>> fastMailLiveData = new UnPeekLiveData<>();
    public UnPeekLiveData<LoadDataModel<TaskBean>> dataListLiveData = new UnPeekLiveData<>();
    public UnPeekLiveData<TaskEntity> taskEntityLiveData = new UnPeekLiveData<>();
    //用于保存当前选择的T已上传和未上传，代办的TAG
    public final UnPeekLiveData<String> taskListTag = new UnPeekLiveData<>();
    //用于保存TaskDetail的tag
    public final UnPeekLiveData<String> taskDetailTag = new UnPeekLiveData<>();



    public void requestDetailList(String userid, String id) {
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        orderDetailLiveData.setValue(new LoadDataModel());
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .taskdetail(userid, id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<TaskEntity>() {
                    @Override
                    public void onSuccess(TaskEntity entity) {
                        orderDetailLiveData.setValue(new LoadDataModel(entity,false));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);

                    }
                });
    }

    public void requestTasksamples(String userid, String id) {
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        sampleDetailLiveData.setValue(new LoadDataModel());
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .tasksamples(userid, id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskSample>>() {
                    @Override
                    public void onSuccess(List<TaskSample> entity) {
                        sampleDetailLiveData.setValue(new LoadDataModel(entity,false));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
    }


    public void sendReportRecord(String taskId, String sampleId, String reporttype) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        fastMailLiveData.setValue(new LoadDataModel());
        RetrofitServiceManager.getInstance().createApiService(Request.class)
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

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);

                    }
                });
    }

    public void getDataByPage(Context context, int pageNo, String status, String ordertype, String cityid,String tasktypecount,String taskCode,String companyname,boolean isSearch) {
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .taskList(AccountManager.getInstance().getUserId(), status, ordertype, cityid, pageNo,tasktypecount,taskCode,companyname)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<TaskBean>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        dataListLiveData.setValue(new LoadDataModel(code, message,isSearch));
                    }

                    @Override
                    public void onSuccess(TaskBean taskBean) {
                        dataListLiveData.setValue(new LoadDataModel(taskBean,isSearch));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);

                    }
                });

     }


}
