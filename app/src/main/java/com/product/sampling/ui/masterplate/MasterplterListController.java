package com.product.sampling.ui.masterplate;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.masterplate.bean.MasterpleListBean;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.List;


/**
 * Created by 陆伟 on 2019/11/21.
 * Copyright (c) 2019 . All rights reserved.
 */


public class MasterplterListController extends BaseUIController<MasterplterListActivity> {
    private static final String TAG = "MasterplterListController";
    MasterplterListActivity masterplterListActivity;
    private String reporterType;


    @Override
    public void setUI(MasterplterListActivity sc) {
            masterplterListActivity = sc;
            getIntent();
    }

    /**
     * 获取模版类型
     * @return
     */
    private void getIntent(){
        masterplterListActivity.emptyView.show(true);
        Bundle bundle = masterplterListActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        reporterType = bundle.getString(MasterplterMainActivity.REPORT_TYPE);
        switch (reporterType){
            case MasterplterMainActivity.REPORT_TYPE_SAMPLIG:
                masterplterListActivity.tvTitle.setText("监督抽查/复查抽样单模版");
                break;
            case MasterplterMainActivity.REPORT_TYPE_UNFIND:
                masterplterListActivity.tvTitle.setText("未抽到样模版");
                break;
            case MasterplterMainActivity.REPORT_TYPE_REFUSE:
                masterplterListActivity.tvTitle.setText("拒检单模版");
                break;
        }


        requestData(reporterType);

    }

    /**
     * 获取模版列表
     */
    private void requestData(String reporterType){
        Dialog tipDialog = new QMUITipDialog.Builder(masterplterListActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中......")
                .create(false);
        tipDialog.show();

        String userid = AccountManager.getInstance().getUserId();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getMouldList(userid,reporterType)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<MasterpleListBean>>() {
                    @Override
                    public void onSuccess(List<MasterpleListBean> masterpleListBeanList) {
                        if(masterpleListBeanList != null && masterpleListBeanList.size() > 0){
                            tipDialog.dismiss();
                            masterplterListActivity.initListView(masterpleListBeanList);
                        }else{
                            tipDialog.dismiss();
                            Toast.makeText(masterplterListActivity,"暂无模版数据",Toast.LENGTH_SHORT).show();
                            masterplterListActivity.initListView(masterpleListBeanList);
                        }

                    }

                    @Override
                    public void onFailure(int code, String message) {
                        tipDialog.dismiss();
                        Toast.makeText(masterplterListActivity,message,Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 增加模版
     */
    public void addMasterplate(String reporterType,String maTitle){
        Dialog tipDialog = new QMUITipDialog.Builder(masterplterListActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("增加中......")
                .create(false);
        tipDialog.show();
        String userid = AccountManager.getInstance().getUserId();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .addMould(userid,reporterType,maTitle)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<MasterpleListBean>() {
                    @Override
                    public void onSuccess(MasterpleListBean masterpleListBean) {
                        tipDialog.dismiss();
                        if(masterpleListBean != null){
                            masterplterListActivity.addItemToListView(masterpleListBean);
                        }

                    }

                    @Override
                    public void onFailure(int code, String message) {
                        masterplterListActivity.showAddFail();
                    }
                });

    }

    /**
     * 更新模版
     */
    public void updateMasterplate(String reporterType,String maTitle,String id){
        Dialog tipDialog = new QMUITipDialog.Builder(masterplterListActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("修改中......")
                .create(false);
        tipDialog.show();
        String userid = AccountManager.getInstance().getUserId();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .updateMould(userid,reporterType,maTitle,id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<MasterpleListBean>() {
                    @Override
                    public void onSuccess(MasterpleListBean masterpleListBean) {
                        tipDialog.dismiss();
                        if(masterpleListBean != null){
                            masterplterListActivity.updateItemToListView(masterpleListBean);
                        }

                    }

                    @Override
                    public void onFailure(int code, String message) {
                        masterplterListActivity.showUpdateFail();
                    }
                });

    }

    /**
     * 删除模版
     */
    public void deleteMasterplate(String id,int pos){
        Dialog tipDialog = new QMUITipDialog.Builder(masterplterListActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("删除中......")
                .create(false);
        tipDialog.show();
        String userid = AccountManager.getInstance().getUserId();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .deleteMould(userid,id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<MasterpleListBean>() {
                    @Override
                    public void onSuccess(MasterpleListBean masterpleListBean) {
                        tipDialog.dismiss();
                        masterplterListActivity.removeItemFromListView(pos);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        masterplterListActivity.showDeleteFail();
                    }
                });

    }


    /**
     * 返回type
     * @return
     */
    public String getType(){
        return reporterType;
    }
}
