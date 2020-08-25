package com.product.sampling.ui.form;

import android.os.Bundle;
import android.text.TextUtils;

import com.product.sampling.Constants;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.NotCheckForm;
import com.product.sampling.db.tables.NotCheckFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.download.DownLoadListener;
import com.product.sampling.net.download.DownLoadUtils;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.form.bean.NotCheckedFormBean;
import com.product.sampling.ui.form.utils.FormSubmitUtil;
import com.product.sampling.utils.RxSchedulersHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by 陆伟 on 2020/2/16.
 * Copyright (c) 2020 . All rights reserved.
 */


public class NotCheckFormController extends BaseUIController<NotCheckFormActivity> {
    private NotCheckFormActivity notCheckFormActivity;
    private String taskId;//任务id
    private String tasktypecount;//产品名称
    private String companyName;//受检单位名称
    public static final String TYPE_TASK_FROM = "0";
    public static final String TYPE_TASK_CALSS = "1";
    private List<TaskCompany> taskCompanies = new ArrayList<>();

    @Override
    public void setUI(NotCheckFormActivity sc) {
        notCheckFormActivity = sc;
        getIntent();
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
    /**
     * 请求任务来源和任务类别和抽样单位名称
     * @param type
     */
    public void requestTaskCompanyInfo(String type){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getTypeContet(type)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskCompany>>() {
                    @Override
                    public void onSuccess(List<TaskCompany> companies) {
                        taskCompanies = companies;
                        switch (type){
                            case TYPE_TASK_FROM:
                                notCheckFormActivity.showPopView(notCheckFormActivity.getTaskFromView(),taskCompanies);
                                break;
                            case TYPE_TASK_CALSS:
                                notCheckFormActivity.showPopView(notCheckFormActivity.getTaskClassView(),taskCompanies);
                                break;

                        }
                    }
                });

    }

    public NotCheckForm getHandleFormToDB(String taskId){
        if(!TextUtils.isEmpty(taskId)){
            NotCheckForm notCheckForm =  DbController.getInstance(notCheckFormActivity).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(taskId)).build().unique();
            return notCheckForm;
        }
        return null;
    }

    public void requestSubmit(NotCheckedFormBean notCheckedFormBean){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .notCheckForm(FormSubmitUtil.getMultBody(notCheckedFormBean).build())
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
                                NotCheckForm notCheckForm = DBManagerFactory.getInstance().getNotCheckFormManager().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(taskId)).build().unique();
                                notCheckForm.setPdfPath(path);
                                DbController.getInstance(notCheckFormActivity).getDaoSession().getNotCheckFormDao().update(notCheckForm);
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
