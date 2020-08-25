package com.product.sampling.ui.form;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.product.sampling.Constants;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.download.DownLoadListener;
import com.product.sampling.net.download.DownLoadUtils;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.form.bean.CodeCompany;
import com.product.sampling.ui.form.bean.FormCheckBean;
import com.product.sampling.ui.form.utils.FormSubmitUtil;
import com.product.sampling.ui.masterplate.bean.MasterpleListBean;
import com.product.sampling.ui.widget.EditAndRecycleView;
import com.product.sampling.utils.BASE64Utils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by 陆伟 on 2019/11/7.
 * Copyright (c) 2019 . All rights reserved.
 */


public class CheckOrRecheckFormController extends BaseUIController<CheckOrRecheckFormActivity> {
    private static final String TAG = "CheckOrRecheckFormController";
    private CheckOrRecheckFormActivity checkOrRecheckFormActivity;
    private List<TaskSample> taskSamples = new ArrayList<>();
    /**
     * 单个样单ID
     */
    private String taskSampleId;
    private String taskId;
    private String plantype;
    private String planId;
    public static final String TYPE_TASK_FROM = "0";
    public static final String TYPE_TASK_CALSS = "1";
    public static final String TYPE_CYDY_NAME = "2";
    private List<TaskCompany> taskCompanies = new ArrayList<>();


    @Override
    public void setUI(CheckOrRecheckFormActivity sc) {
        checkOrRecheckFormActivity = sc;
        getIntent();
    }

    private void getIntent() {
        Bundle bundle = checkOrRecheckFormActivity.getIntent().getExtras(); //读取intent的数据给bundle对象
        taskSampleId = bundle.getString("id");
        taskId = bundle.getString("taskId");
        // 2:表示为 流通领域类型
        plantype = bundle.getString("plantype");
        planId = bundle.getString("planid");
    }


    public void  getCheckFormToDB(String taskSampleId, Handler mHandler) {
        DBManagerFactory.getInstance().getCheckFormManager().queryBySampleId(taskSampleId,mHandler);
//        if (!TextUtils.isEmpty(taskSampleId)) {
//            CheckForm checkForm = DbController.getInstance(checkOrRecheckFormActivity).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(taskSampleId)).build().unique();
//            return checkForm;
//        }
//        return null;
    }

    /**
     * 获取处置单表，用于将抽样单数据填写到处置单中
     */
    public void getHandleFormToDB(String taskSampleId, Handler mHandler){
        DBManagerFactory.getInstance().getHandleManager().queryBySampleId(taskSampleId,mHandler);
    }

    /**
     * 请求任务来源和任务类别和抽样单位名称
     *
     * @param type
     */
    public void requestTaskCompanyInfo(String type) {
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getTypeContet(type)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskCompany>>() {
                    @Override
                    public void onSuccess(List<TaskCompany> companies) {
                        taskCompanies.clear();
                        switch (type) {
                            case TYPE_TASK_FROM:
                                //                                checkOrRecheckFormActivity.showPopView(checkOrRecheckFormActivity.getTaskFromView(), taskCompanies);
                                EditAndRecycleView fromView = new EditAndRecycleView(checkOrRecheckFormActivity,TYPE_TASK_FROM);
                                fromView.create().show();
                                //将得到的集合
                                for(TaskCompany taskCompany :companies){
                                    taskCompanies.add(taskCompany);
                                    if(taskCompany.getChild() != null && taskCompany.getChild().size() > 0){
                                        for(TaskCompany taskCompanyChild:taskCompany.getChild()){
                                            taskCompanyChild.setItem("------"+taskCompanyChild.getItem());
                                            taskCompanies.add(taskCompanyChild);
                                        }
                                    }

                                }

                                fromView.setWholeList(taskCompanies);
                                fromView.refreshRecycleUI(taskCompanies);
                                break;
                            case TYPE_TASK_CALSS:
                                //                                checkOrRecheckFormActivity.showPopView(checkOrRecheckFormActivity.getTaskFromView(), taskCompanies);
                                EditAndRecycleView classView = new EditAndRecycleView(checkOrRecheckFormActivity,TYPE_TASK_CALSS);
                                classView.create().show();
                                //将得到的集合
                                for(TaskCompany taskCompany :companies){
                                    taskCompanies.add(taskCompany);
                                    if(taskCompany.getChild() != null && taskCompany.getChild().size() > 0){
                                        for(TaskCompany taskCompanyChild:taskCompany.getChild()){
                                            taskCompanyChild.setItem("------"+taskCompanyChild.getItem());
                                            taskCompanies.add(taskCompanyChild);
                                        }
                                    }

                                }

                                classView.setWholeList(taskCompanies);
                                classView.refreshRecycleUI(taskCompanies);
                                break;
                            //                                checkOrRecheckFormActivity.showPopView(checkOrRecheckFormActivity.getTaskClassView(), taskCompanies);
                            case TYPE_CYDY_NAME:
                                //将得到的集合
                                for(TaskCompany taskCompany :companies){
                                    taskCompanies.add(taskCompany);
                                    if(taskCompany.getChild() != null && taskCompany.getChild().size() > 0){
                                        for(TaskCompany taskCompanyChild:taskCompany.getChild()){
                                            taskCompanyChild.setItem("------"+taskCompanyChild.getItem());
                                            taskCompanies.add(taskCompanyChild);
                                        }
                                    }

                                }

                                checkOrRecheckFormActivity.showPopView(checkOrRecheckFormActivity.getCydwNameView(), taskCompanies);
                                break;

                        }
                    }
                });

    }

    /**
     * 请求抽查单
     *
     * @param userid
     * @param taskId
     */
    public void requestTasksamples(String userid, String taskId) {
        if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(taskId)) return;
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .tasksamples(userid, taskId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskSample>>() {
                    @Override
                    public void onSuccess(List<TaskSample> samples) {
                        taskSamples = samples;
                        checkOrRecheckFormActivity.setupUIData();
                    }
                });
    }


    /**
     * 请求抽查单模版数据
     *
     * @param userid
     * @param id
     * @param type   模版类型
     */
    public void requestTasksample(String userid, String id, String type) {
        if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(id)) return;
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getMouldInfo(userid, id, type)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<Sampling>() {
                    @Override
                    public void onSuccess(Sampling sample) {
                        checkOrRecheckFormActivity.setupUIData(sample);
                    }
                });
    }


    /**
     * 获取模版列表
     */
    public void requestMasterpleList(String reporterType) {
        Dialog tipDialog = new QMUITipDialog.Builder(checkOrRecheckFormActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中......")
                .create(false);
        tipDialog.show();

        String userid = AccountManager.getInstance().getUserId();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getMouldList(userid, reporterType)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<MasterpleListBean>>() {
                    @Override
                    public void onSuccess(List<MasterpleListBean> masterpleListBeanList) {
                        if (masterpleListBeanList != null && masterpleListBeanList.size() > 0) {
                            tipDialog.dismiss();
                            checkOrRecheckFormActivity.showMenuDialog(masterpleListBeanList);
                        } else {
                            tipDialog.dismiss();
                            checkOrRecheckFormActivity.showMasterInfoFail("暂无模版数据，请先去模版添加");
//                            Toast.makeText(checkOrRecheckFormActivity,"暂无模版数据，请先去模版添加",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        tipDialog.dismiss();
                    }
                });

    }


    /**
     * 根据抽样单id获取单个抽样单
     *
     * @param taskSampleId
     * @return
     */
    public Sampling getSampling(String taskSampleId) {
        if (taskSamples == null || taskSamples.size() == 0) return null;
        TaskSample curTaskSample = null;
        for (TaskSample taskSample : taskSamples) {
            if (taskSample.getId().equals(taskSampleId)) {
                curTaskSample = taskSample;
            }
        }
        return curTaskSample.getSampling();
    }

    /**
     * 根据机构统一信用代码获取公司信息
     */
    public void requestCompanyInfoByCode(String code) {
        Dialog tipDialog = new QMUITipDialog.Builder(checkOrRecheckFormActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中......")
                .create(false);
        tipDialog.show();
        String userid = AccountManager.getInstance().getUserId();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getCompanyIfonByComID(userid, code)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<CodeCompany>>() {
                    @Override
                    public void onSuccess(List<CodeCompany> codeCompanyList) {
                        tipDialog.dismiss();
                        if(codeCompanyList != null && codeCompanyList.size() > 0){
                            checkOrRecheckFormActivity.showMultiChoiceDialog(codeCompanyList);
                        }else {
                            checkOrRecheckFormActivity.showMasterInfoFail("查无此代码公司！");
                        }

                }

                    @Override
                    public void onFailure(int code, String message) {
                        tipDialog.dismiss();
                        checkOrRecheckFormActivity.showMasterInfoFail("查无此代码公司！");

                    }
                });
    }

    public String getTaskSampleId() {
        return taskSampleId;
    }

    public String getTaskId() {
        return taskId;
    }

    /**
     * 电子签章接口
     *
     * @param taskSampleId
     * @param taskId
     */
    public void requestPDFSign(String taskSampleId, String taskId, String reporttype) {
        CheckForm checkForm = DbController.getInstance(checkOrRecheckFormActivity).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(taskSampleId)).build().unique();
        String pdfPath = checkForm.getPdfPath();
        String userid = AccountManager.getInstance().getUserId();
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            return;
        }
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, pdfFile);//把文件与类型放入请求体
        multipartBodyBuilder.addFormDataPart("pdfbase64", pdfFile.getName(), requestFile);//抽样单
        multipartBodyBuilder.addFormDataPart("taskid", taskId);
        multipartBodyBuilder.addFormDataPart("sampleid", taskSampleId);
        multipartBodyBuilder.addFormDataPart("userid", userid);
        multipartBodyBuilder.addFormDataPart("reporttype", reporttype);
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .pdfSignElc(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        //如果成功将base64转换成PDF文件
                        try {
                            if (pdfFile.exists()) {
                                pdfFile.delete();
                            }
                            BASE64Utils.decoderBase64ToFile(s, pdfPath);
                        } catch (IOException e) {
                            checkOrRecheckFormActivity.showPDFFail();
                            e.printStackTrace();
                        }

                        if (checkOrRecheckFormActivity.pdfDialog != null && checkOrRecheckFormActivity.pdfDialog.isShowing()) {
                            checkOrRecheckFormActivity.pdfDialog.dismiss();
                        }
                        checkOrRecheckFormActivity.showPDFSuccess();

                        checkOrRecheckFormActivity.shareBySystem(checkForm.getPdfPath());
                        checkOrRecheckFormActivity.setResult(CheckOrRecheckFormActivity.RESULT_CODE_PDF_SUCCESS);


                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        checkOrRecheckFormActivity.showPDFFail();
                    }
                });

    }

    /**
     * 将表单数据提交后台
     *
     * @param formCheckBean
     */
    public void requestSubmit(FormCheckBean formCheckBean) {
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .checForm(FormSubmitUtil.getMultBody(formCheckBean).build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        DownLoadUtils.download("base/tBaFile/downFile?id=" + s, Constants.PDF_PATH + s + ".pdf", new DownLoadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onProgress(int progress) {

                            }

                            @Override
                            public void onFinish(String path) {
                                if (checkOrRecheckFormActivity.pdfDialog != null && checkOrRecheckFormActivity.pdfDialog.isShowing()) {
                                    checkOrRecheckFormActivity.pdfDialog.dismiss();
                                }
                                checkOrRecheckFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkOrRecheckFormActivity.showPDFSuccess();
                                    }
                                });
                                CheckForm checkForm = DBManagerFactory.getInstance().getCheckFormManager().queryBuilder().where(CheckFormDao.Properties.Id.eq(getTaskSampleId())).build().unique();
                                checkForm.setPdfPath(path);
                                DbController.getInstance(checkOrRecheckFormActivity).getDaoSession().startAsyncSession().runInTx(new Runnable() {
                                    @Override
                                    public void run() {
                                        DbController.getInstance(checkOrRecheckFormActivity).getDaoSession().getCheckFormDao().update(checkForm);
                                    }
                                });


                                checkOrRecheckFormActivity.shareBySystem(path);
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                if (checkOrRecheckFormActivity.pdfDialog != null && checkOrRecheckFormActivity.pdfDialog.isShowing()) {
                                    checkOrRecheckFormActivity.pdfDialog.dismiss();

                                }
                                checkOrRecheckFormActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkOrRecheckFormActivity.showPDFFail();
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
                        if (checkOrRecheckFormActivity.pdfDialog != null && checkOrRecheckFormActivity.pdfDialog.isShowing()) {
                            checkOrRecheckFormActivity.pdfDialog.dismiss();
                        }
                        checkOrRecheckFormActivity.showPDFFail();
                    }
                });
    }

    /**
     * 获取生产单位下的所有产品名称
     * @param productName  生产单位名称
     */
    public void requestProductsName(String planId,String productName){
        Dialog tipDialog = new QMUITipDialog.Builder(checkOrRecheckFormActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中......")
                .create(false);
        tipDialog.show();
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .checkProduceName(planId,productName)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String string) {
                        tipDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        tipDialog.dismiss();
                        if(code == 201){
                            checkOrRecheckFormActivity.showLongMessageDialog(message);
                        }
                    }
                });

    }

    public String getPlanId() {
        return planId;
    }

    public String getPlantype() {
        return plantype;
    }

    public static void main(String[] args) {
        String test = "------";
        System.out.println(test.length());
    }
}
