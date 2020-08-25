package com.product.sampling.ui.masterplate;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;


/**
 * Created by 陆伟 on 2019/11/22.
 * Copyright (c) 2019 . All rights reserved.
 */


public class MasterplterEditController extends BaseUIController<MasterplterEditActivity> {
    private MasterplterEditActivity masterplterEditActivity;
    private String moudleType;
    private String moudleId;

    public static final String TYPE_TASK_FROM = "0";
    public static final String TYPE_TASK_CALSS = "1";
    public static final String TYPE_CYDY_NAME = "2";
    private List<TaskCompany> taskCompanies = new ArrayList<>();




    @Override
    public void setUI(MasterplterEditActivity sc) {
        masterplterEditActivity = sc;
        getIntent();
    }

    private void getIntent(){
        Bundle bundle = masterplterEditActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        moudleType = bundle.getString(MasterplterMainActivity.REPORT_TYPE);
        moudleId = bundle.getString(MasterplterListActivity.MOUDLE_ID);
    }

    public String getMoudleType(){
        return moudleType;
    }

    public String getMoudleId(){
        return moudleId;
    }

    /**
     * 请求抽查单
     * @param userid
     * @param id
     * @param type 模版类型
     */
    public void requestTasksample(String userid, String id,String type) {
        if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(id)) return;
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getMouldInfo(userid,id,type)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<Sampling>() {
                    @Override
                    public void onSuccess(Sampling sample) {
                        masterplterEditActivity.setData(sample);
                    }
                });
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
                                masterplterEditActivity.showPopView(masterplterEditActivity.getTaskFromView(),taskCompanies);
                                break;
                            case TYPE_TASK_CALSS:
                                masterplterEditActivity.showPopView(masterplterEditActivity.getTaskClassView(),taskCompanies);
                                break;
                            case TYPE_CYDY_NAME:
                                masterplterEditActivity.showPopView(masterplterEditActivity.getCydwNameView(),taskCompanies);
                                break;

                        }
                    }
                });

    }

    public void postSampling(CheckForm masterpleForm){
        Dialog tipDialog = new QMUITipDialog.Builder(masterplterEditActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("提交中......")
                .create(false);
        tipDialog.show();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("id", masterpleForm.getId());
        multipartBodyBuilder.addFormDataPart("reporttype",MasterplterMainActivity.REPORT_TYPE_SAMPLIG);
        appendSampleToBody("taskfrom",multipartBodyBuilder,checkNullString(masterpleForm.getTaskFromId()));
        appendSampleToBody("tasktype",multipartBodyBuilder,checkNullString(masterpleForm.getTaskClassId()));
        appendSampleToBody("taskfrommean",multipartBodyBuilder,checkNullString(masterpleForm.getTaskFrom()));
        appendSampleToBody("tasktypemean",multipartBodyBuilder,checkNullString(masterpleForm.getTaskClass()));
        appendSampleToBody("inspectedname",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptCompanyName()));
        appendSampleToBody("inspectedaddress",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptCompanyAddress()));
        appendSampleToBody("inspectedman",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptCompanyConnectNanme()));
        appendSampleToBody("inspectedtel",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptCompanyConnecTel()));
        appendSampleToBody("representative",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptCompanyFaren()));
        appendSampleToBody("producename",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyName()));
        appendSampleToBody("produceaddress",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyAdress()));
        appendSampleToBody("producezipcode",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyEMS()));
        appendSampleToBody("prepresentative",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyFaren()));

        appendSampleToBody("produceconman",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyConnectName()));
        appendSampleToBody("producetel",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyConnectTEL()));
        appendSampleToBody("producelicense",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyLicense()));
        appendSampleToBody("producecode",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyID()));
        appendSampleToBody("produceoutput",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyDimensionsYYSR()));
        appendSampleToBody("producepcount",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanyDimensionsCYRYS()));
//        appendSampleToBody("acceptProType",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProType()));
        appendSampleToBody("producesamlltype",multipartBodyBuilder,checkNullString(masterpleForm.getProductCompanySmallType()));
        appendSampleToBody("productnumtype",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceLicenseType()));
        appendSampleToBody("productnum",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceLicenseId()));
        appendSampleToBody("productname",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceCPMC()));
        appendSampleToBody("productmodel",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceGGCH()));
        appendSampleToBody("productbnum",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceSCRQ()));
        appendSampleToBody("productmark",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceSB()));
        appendSampleToBody("samplingcount",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceCYSL()));
        appendSampleToBody("productlevel",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceCPDJ()));
//        appendSampleToBody("productpl",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProducePL()));
        appendSampleToBody("samplingbaseandbatch",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProducePL()));

        appendSampleToBody("dostandard",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceBZZXBZ()));
        appendSampleToBody("dotime",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceCYRQ()));
        appendSampleToBody("encapsulationstate",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceFYZT()));
        appendSampleToBody("rvandfc",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceBYLFCDD()));
        appendSampleToBody("sendaddress",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceJSYDD()));
        appendSampleToBody("endtime",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceJSYJZRQ()));
        appendSampleToBody("samplingname",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyNameId()));
        appendSampleToBody("samplingnamemean",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyName()));
        appendSampleToBody("samplingman",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyConnectName()));
        appendSampleToBody("samplingaddress",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyAddress()));
        appendSampleToBody("samplingemail",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyEmail()));
        appendSampleToBody("samplingcode",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyEMS()));
        appendSampleToBody("samplingtel",multipartBodyBuilder,checkNullString(masterpleForm.getCheckCompanyConnectTel()));
        appendSampleToBody("remark",multipartBodyBuilder,checkNullString(masterpleForm.getBzInfo()));
        appendSampleToBody("unitprice",multipartBodyBuilder,checkNullString(masterpleForm.getAcceptProduceDJ()));
        appendSampleToBody("recordnumber",multipartBodyBuilder,checkNullString(masterpleForm.getBzXMBAH()));
        appendSampleToBody("samplecost",multipartBodyBuilder,checkNullString(masterpleForm.getBzGYF()));
        appendSampleToBody("taskcode",multipartBodyBuilder,checkNullString(masterpleForm.getTaskCode()));
        appendSampleToBody("reportcode",multipartBodyBuilder,checkNullString(masterpleForm.getReportId()));
        appendSampleToBody("fillInDate",multipartBodyBuilder,checkNullString(masterpleForm.getCyDwRQ()));

        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .savesMoudleInfonByBody(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        tipDialog.dismiss();
                        masterplterEditActivity.showSuccess();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        masterplterEditActivity.showFail();
                    }
                });

    }
    /**
     * 将数据拼接成服务端格式
     */
    private void  appendSampleToBody(String string, MultipartBody.Builder builder,String value){
        builder.addFormDataPart("sampling." + string,value);
    }
    private String checkNullString(String s){
        if(TextUtils.isEmpty(s)) {
            return "";
        }else {
            return s;
        }
    }

}
