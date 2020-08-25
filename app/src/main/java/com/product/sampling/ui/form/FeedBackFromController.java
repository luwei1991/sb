package com.product.sampling.ui.form;

import android.os.Bundle;

import com.product.sampling.Constants;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.download.DownLoadListener;
import com.product.sampling.net.download.DownLoadUtils;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.form.bean.FeedBackBean;
import com.product.sampling.ui.form.utils.FormSubmitUtil;
import com.product.sampling.utils.RxSchedulersHelper;

import io.reactivex.disposables.Disposable;

/**
 * Created by 陆伟 on 2020/4/9.
 * Copyright (c) 2020 . All rights reserved.
 */


public class FeedBackFromController extends BaseUIController<FeedBackFormActivity> {
    private FeedBackFormActivity feedBackFormActivity;
    private String taskId;//任务id
    private String tasktypecount;//产品名称
    private String companyName;//受检单位名称
    @Override
    public void setUI(FeedBackFormActivity sc) {
        feedBackFormActivity = sc;
        getIntent();
    }
    private void getIntent(){
        Bundle bundle = feedBackFormActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        taskId = bundle.getString("taskId");//
        tasktypecount = bundle.getString("tasktypecount");
        companyName =  bundle.getString("companyname");
    }

    public String getCompanyName(){
        return companyName;
    }

    public void requestSubmit(FeedBackBean feedBackBean){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .feedBackkForm(FormSubmitUtil.getMultBody(feedBackBean).build())
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
                                if(feedBackFormActivity.pdfDialog != null && feedBackFormActivity.pdfDialog.isShowing()){
                                    feedBackFormActivity.pdfDialog.dismiss();
                                }
                                feedBackFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        feedBackFormActivity.showPDFSuccess();
                                    }
                                });
                                feedBackFormActivity.shareBySystem(path);
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                if(feedBackFormActivity.pdfDialog != null && feedBackFormActivity.pdfDialog.isShowing()){
                                    feedBackFormActivity.pdfDialog.dismiss();
                                }

                                feedBackFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        feedBackFormActivity.showPDFFail();
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
                        if(feedBackFormActivity.pdfDialog != null && feedBackFormActivity.pdfDialog.isShowing()){
                            feedBackFormActivity.pdfDialog.dismiss();
                        }
                        feedBackFormActivity.showPDFFail();
                    }
                });

    }


}
