//package com.product.sampling.ui.masterplate;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//
//import com.product.sampling.Constants;
//import com.product.sampling.bean.Sampling;
//import com.product.sampling.bean.TaskCompany;
//import com.product.sampling.db.DBManagerFactory;
//import com.product.sampling.db.DbController;
//import com.product.sampling.db.tables.NotCheckForm;
//import com.product.sampling.db.tables.NotCheckFormDao;
//import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
//import com.product.sampling.net.ZBaseObserver;
//import com.product.sampling.net.download.DownLoadListener;
//import com.product.sampling.net.download.DownLoadUtils;
//import com.product.sampling.net.request.Request;
//import com.product.sampling.ui.base.BaseUIController;
//import com.product.sampling.ui.form.NotCheckFormActivity;
//import com.product.sampling.ui.form.bean.NotCheckedFormBean;
//import com.product.sampling.ui.form.utils.FormSubmitUtil;
//import com.product.sampling.utils.RxSchedulersHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.disposables.Disposable;
//
///**
// * Created by 陆伟 on 2020/2/16.
// * Copyright (c) 2020 . All rights reserved.
// */
//
//
//public class NotCheckMasterController extends BaseUIController<NotCheckMasterActivity> {
//    private NotCheckMasterActivity notCheckMasterActivity;
//
//    public static final String TYPE_TASK_FROM = "0";
//    public static final String TYPE_TASK_CALSS = "1";
//    private List<TaskCompany> taskCompanies = new ArrayList<>();
//
//    private String moudleType;
//    private String moudleId;
//
//    @Override
//    public void setUI(NotCheckMasterActivity sc) {
//        notCheckMasterActivity = sc;
//        getIntent();
//    }
//
//    private void getIntent(){
//        Bundle bundle = notCheckMasterActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
//        moudleType = bundle.getString(MasterplterMainActivity.REPORT_TYPE);
//        moudleId = bundle.getString(MasterplterListActivity.MOUDLE_ID);
//    }
//    /**
//     * 请求任务来源和任务类别和抽样单位名称
//     * @param type
//     */
//    public void requestTaskCompanyInfo(String type){
//        RetrofitServiceManager.getInstance().createApiService(Request.class)
//                .getTypeContet(type)
//                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
//                .subscribe(new ZBaseObserver<List<TaskCompany>>() {
//                    @Override
//                    public void onSuccess(List<TaskCompany> companies) {
//                        taskCompanies = companies;
//                        switch (type){
//                            case TYPE_TASK_FROM:
//                                notCheckMasterActivity.showPopView(notCheckMasterActivity.getTaskFromView(),taskCompanies);
//                                break;
//                            case TYPE_TASK_CALSS:
//                                notCheckMasterActivity.showPopView(notCheckMasterActivity.getTaskClassView(),taskCompanies);
//                                break;
//
//                        }
//                    }
//                });
//
//    }
//
//
//    /**
//     * 请求抽查单
//     * @param userid
//     * @param id
//     * @param type 模版类型
//     */
//    public void requestTasksample(String userid, String id,String type) {
//        if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(id)) return;
//        RetrofitServiceManager.getInstance().createApiService(Request.class)
//                .getMouldInfo(userid,id,type)
//                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
//                .subscribe(new ZBaseObserver<Sampling>() {
//                    @Override
//                    public void onSuccess(Sampling sample) {
////                        notCheckMasterActivity.setData(sample);
//                    }
//                });
//    }
//
//    public String getMoudleType(){
//        return moudleType;
//    }
//
//    public String getMoudleId(){
//        return moudleId;
//    }
//
//}
