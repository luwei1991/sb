package com.product.sampling.ui.form;

import android.os.Bundle;
import android.text.TextUtils;

import com.product.sampling.Constants;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.db.tables.HandleFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.download.DownLoadListener;
import com.product.sampling.net.download.DownLoadUtils;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.form.bean.FormHandleBean;
import com.product.sampling.ui.form.utils.FormSubmitUtil;
import com.product.sampling.utils.RxSchedulersHelper;

import io.reactivex.disposables.Disposable;


/**
 * Created by 陆伟 on 2020/2/13.
 * Copyright (c) 2020 . All rights reserved.
 */


public class HandleFormController extends BaseUIController<HandleFormActivity> {
    private static final String TAG = "HandleFormController";
    private HandleFormActivity handleFormActivity;
    private String taskId;//任务id
    private String tasktypecount;//产品名称
    private String companyName;//受检单位名称
    private String taskSampleId;//样品id

    @Override
    public void setUI(HandleFormActivity sc) {
        handleFormActivity = sc;
        getIntent();
    }

    private void getIntent(){
        Bundle bundle = handleFormActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        taskId = bundle.getString("taskId");//
        tasktypecount = bundle.getString("tasktypecount");
        companyName =  bundle.getString("companyname");
        taskSampleId = bundle.getString("id");
    }


    public HandleForm getHandleFormToDB(String sampleId){
        if(!TextUtils.isEmpty(sampleId)){
            HandleForm handleForm =  DbController.getInstance(handleFormActivity).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(sampleId)).build().unique();
            return handleForm;
        }
        return null;
    }

    public String getTaskId(){
        return taskId;
    }
    public String getTaskSampleId() {
        return taskSampleId;
    }



    public void requestSubmit(FormHandleBean handleBean){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .handleForm(FormSubmitUtil.getMultBody(handleBean).build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        DownLoadUtils.download( "base/tBaFile/downFile?id="+ s,Constants.PDF_PATH + s+".pdf" ,new DownLoadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onProgress(int progress) {

                            }

                            @Override
                            public void onFinish(String path) {
                                if(handleFormActivity.pdfDialog != null && handleFormActivity.pdfDialog.isShowing()){
                                    handleFormActivity.pdfDialog.dismiss();
                                }
                                handleFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleFormActivity.showPDFSuccess();
                                    }
                                });
                                HandleForm handleForm = DBManagerFactory.getInstance().getHandleManager().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(getTaskSampleId())).build().unique();
                                handleForm.setPdfPath(path);
                                DbController.getInstance(handleFormActivity).getDaoSession().getHandleFormDao().update(handleForm);
                                handleFormActivity.shareBySystem(path);
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                if(handleFormActivity.pdfDialog != null && handleFormActivity.pdfDialog.isShowing()){
                                    handleFormActivity.pdfDialog.dismiss();
                                }

                                handleFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleFormActivity.showPDFFail();
                                    }
                                });

                            }
                        });

                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        if(handleFormActivity.pdfDialog != null && handleFormActivity.pdfDialog.isShowing()){
                            handleFormActivity.pdfDialog.dismiss();
                        }
                        handleFormActivity.showPDFFail();
                    }
                });

    }

}
