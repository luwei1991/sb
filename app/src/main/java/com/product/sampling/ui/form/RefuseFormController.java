package com.product.sampling.ui.form;

import android.os.Bundle;
import android.text.TextUtils;

import com.product.sampling.Constants;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.RefuseForm;
import com.product.sampling.db.tables.RefuseFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.download.DownLoadListener;
import com.product.sampling.net.download.DownLoadUtils;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.form.bean.RefuseBean;
import com.product.sampling.ui.form.utils.FormSubmitUtil;
import com.product.sampling.utils.RxSchedulersHelper;

import io.reactivex.disposables.Disposable;

/**
 * Created by 陆伟 on 2020/3/6.
 * Copyright (c) 2020 . All rights reserved.
 */


public class RefuseFormController extends BaseUIController<RefuseFormActivity> {
    private static final String TAG = "RefuseFormController";
    private RefuseFormActivity notCheckFormActivity;
    private String taskId;//任务id
    private String tasktypecount;//产品名称
    private String companyName;//受检单位名称


    @Override
    public void setUI(RefuseFormActivity sc) {
        notCheckFormActivity = sc;
        getIntent();
    }

    public RefuseForm getRefuseFormDB(String taskId){
        if(!TextUtils.isEmpty(taskId)){
            RefuseForm refuseForm =  DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(taskId)).build().unique();
            return refuseForm;
        }
        return null;
    }
    private void getIntent(){
        Bundle bundle = notCheckFormActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        taskId = bundle.getString("taskId");//
        tasktypecount = bundle.getString("tasktypecount");
        companyName =  bundle.getString("companyname");
    }
    public String getTaskId(){
        return taskId;
    }

    public void requestSubmit(RefuseBean refuseBean){

        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .refusekForm(FormSubmitUtil.getMultBody(refuseBean) .build())
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
                                if(notCheckFormActivity.pdfDialog != null && notCheckFormActivity.pdfDialog.isShowing()){
                                    notCheckFormActivity.pdfDialog.dismiss();
                                }

                                notCheckFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notCheckFormActivity.showPDFSuccess();
                                    }
                                });
                                RefuseForm refuseForm = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(taskId)).build().unique();
                                refuseForm.setRefusePdf(path);
                                DbController.getInstance(notCheckFormActivity).getDaoSession().getRefuseFormDao().update(refuseForm);
                                notCheckFormActivity.shareBySystem(path);
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                if(notCheckFormActivity.pdfDialog != null && notCheckFormActivity.pdfDialog.isShowing()){
                                    notCheckFormActivity.pdfDialog.dismiss();
                                }
                                notCheckFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notCheckFormActivity.showPDFFail();
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
                        if(notCheckFormActivity.pdfDialog != null && notCheckFormActivity.pdfDialog.isShowing()){
                            notCheckFormActivity.pdfDialog.dismiss();
                        }
                        notCheckFormActivity.showPDFFail();
                    }
                });

    }


}
