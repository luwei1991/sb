package com.product.sampling.ui.form;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.product.sampling.Constants;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
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
 * Created by 陆伟 on 2020/5/18.
 * Copyright (c) 2020 . All rights reserved.
 */


public class HandleLandFormController extends BaseUIController<HandleLandFormActivity> {
    private static final String TAG = "HandleLandFormController";
    private HandleLandFormActivity handleLandFormActivity;
    private String taskId;//任务id
    private String tasktypecount;//产品名称
    private String companyName;//受检单位名称
    private String taskSampleId;//样品id


    @Override
    public void setUI(HandleLandFormActivity sc) {
        handleLandFormActivity = sc;
        getIntent();
    }

    private void getIntent(){
        Bundle bundle = handleLandFormActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        taskId = bundle.getString("taskId");//
        tasktypecount = bundle.getString("tasktypecount");
        companyName =  bundle.getString("companyname");
        taskSampleId = bundle.getString("id");
    }

    /**
     * 获取处置单信息
     * @return
     */
    public void getHandleFormToDB(Handler mHandler){
        if(!TextUtils.isEmpty(taskSampleId)){
//            HandleForm handleForm =  DbController.getInstance(handleLandFormActivity).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(taskSampleId)).build().unique();
            DBManagerFactory.getInstance().getHandleManager().queryBySampleId(taskSampleId,mHandler);
        }

    }

    /**
     * 获取抽样单信息
     * @return
     */
    public CheckForm getCheckFormToDB() {
        if (!TextUtils.isEmpty(taskSampleId)) {
            CheckForm checkForm = DbController.getInstance(handleLandFormActivity).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(taskSampleId)).build().unique();
            return checkForm;
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
                        DownLoadUtils.download( "base/tBaFile/downFile?id="+ s, Constants.PDF_PATH + s+".pdf" ,new DownLoadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onProgress(int progress) {

                            }

                            @Override
                            public void onFinish(String path) {
                                if(handleLandFormActivity.pdfDialog != null && handleLandFormActivity.pdfDialog.isShowing()){
                                    handleLandFormActivity.pdfDialog.dismiss();
                                }
                                handleLandFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleLandFormActivity.showPDFSuccess();
                                    }
                                });
                                HandleForm handleForm = DBManagerFactory.getInstance().getHandleManager().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(getTaskSampleId())).build().unique();
                                handleForm.setPdfPath(path);
                                DbController.getInstance(handleLandFormActivity).getDaoSession().getHandleFormDao().update(handleForm);
                                handleLandFormActivity.shareBySystem(path);
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                if(handleLandFormActivity.pdfDialog != null && handleLandFormActivity.pdfDialog.isShowing()){
                                    handleLandFormActivity.pdfDialog.dismiss();
                                }

                                handleLandFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleLandFormActivity.showPDFFail();
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
                        if(handleLandFormActivity.pdfDialog != null && handleLandFormActivity.pdfDialog.isShowing()){
                            handleLandFormActivity.pdfDialog.dismiss();
                        }
                        handleLandFormActivity.showPDFFail();
                    }
                });

    }
}
