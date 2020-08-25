package com.product.sampling.ui.form;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.product.sampling.R;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.tables.RefuseForm;
import com.product.sampling.db.tables.RefuseFormDao;
import com.product.sampling.manager.GlideManager;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.form.bean.RefuseBean;
import com.product.sampling.ui.form.bean.SignBean;
import com.product.sampling.ui.sign.SignActivity;
import com.product.sampling.ui.sign.SignActivityController;
import com.product.sampling.utils.DateUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 陆伟 on 2020/3/6.
 * Copyright (c) 2020 . All rights reserved.
 */


public class RefuseFormActivity extends BaseActivity<RefuseFormController> {
    private static final String TAG = "RefuseFormActivity";
    private RefuseFormController refuseFormController;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int IMAGE_REQUEST_CODE = 0x06;
    public static String SSRD_YRAR = "";
    public static String SSRD_MONTH = "";
    public static String SSRD_DAY = "";
    /**当地部门日期*/
    public static String DD_YRAR = "";
    public static String DD_MONTH = "";
    public static String DD_DAY = "";
    public QMUITipDialog pdfDialog;
    private TimePickerView timePicker;


    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.et_cpmc_in)
    EditText etCpmc;//产品名称
    @BindView(R.id.et_qymc_in)
    EditText etQymc;//企业名称
    @BindView(R.id.et_frdb_in)
    EditText etFrdb;//企业法人
    @BindView(R.id.et_qydz_in)
    EditText etQydz;//企业地址
    @BindView(R.id.et_lxr_in)
    EditText etSjdwLxr; //受检单位联系人
    @BindView(R.id.et_lxdh_in)
    EditText etSjdwLxrdh;//受检单位联系电话
    @BindView(R.id.et_cy_lxdh_in)
    EditText etCyLx;//抽样单位联系电话
    @BindView(R.id.et_taskfrom_year)
    EditText etTaskFromYear;//任务来源年
    @BindView(R.id.et_taskfrom_pro)
    EditText etTaskFromPro;//产品质量
    @BindView(R.id.et_taskfrom_jd)
    EditText etTaskFromJd; //监督管理
    @BindView(R.id.et_ssrd)
    EditText etSsrd;//事实认定
    @BindView(R.id.ll_cyr_qz)
    LinearLayout ivCyr;//抽样人签字
    @BindView(R.id.iv_cyr_1)
    ImageView ivCyr_1;//抽样人签字
    @BindView(R.id.iv_cyr_2)
    ImageView ivCyr_2;//抽样人签字
    @BindView(R.id.iv_xgry)
    ImageView ivXgry;//相关人员签字
    @BindView(R.id.tv_ssrd_date)
    TextView tvSsrdDate;//事实认定日期
    @BindView(R.id.et_qyszd_advice)
    EditText etQyAdvice;//企业所在地意见
    @BindView(R.id.tv_qyszdzl_date)
    TextView tvQyDate;//企业所在地质量技术监督部门日期
    @BindView(R.id.button)
    QMUIRoundButton pdfButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_refuse_form);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_refuse_form, null);
        ButterKnife.bind(this,root);
        setContentView(root);
        initController();
        initData();
    }

    private void initController(){
        refuseFormController = new RefuseFormController();
        setUIController(refuseFormController);
        refuseFormController.setUI(this);
    }

    @Override
    public void setUIController(RefuseFormController sc) {
        refuseFormController = sc;
    }

    private void initData(){
        verifyStoragePermissions(RefuseFormActivity.this);
        //加载数据   先从本地加载数据-------
        RefuseForm refuseForm = refuseFormController.getRefuseFormDB(refuseFormController.getTaskId());
        if(refuseForm != null){
            setData(refuseForm);
        }
    }

    private void setData(RefuseForm refuseForm){
        etCpmc.setText(checkNullString(refuseForm.getRefuseCpmc()));
        etQymc.setText(checkNullString(refuseForm.getRefuseQymc()));
        etFrdb.setText(checkNullString(refuseForm.getRefuseFrdb()));
        etQydz.setText(checkNullString(refuseForm.getRefuseQydz()));
        etSjdwLxr.setText(checkNullString(refuseForm.getRefuseSjqyLxr()));
        etSjdwLxrdh.setText(checkNullString(refuseForm.getRefuseSjqyLxdh()));
        etCyLx.setText(checkNullString(refuseForm.getRefuseCydwLxdh()));
        etTaskFromYear.setText(checkNullString(refuseForm.getRefuseCydwRwlyYear()));
        etTaskFromPro.setText(checkNullString(refuseForm.getRefuseCydwRwlyPro()));
        etTaskFromJd.setText(checkNullString(refuseForm.getRefuseCydwRwlyJD()));
        etSsrd.setText(checkNullString(refuseForm.getRefuseSsrd()));
        //抽样人签字
        String cyrPath_1 = refuseForm.getRefuseCyrImg_1();
        if(!TextUtils.isEmpty(cyrPath_1)){
            GlideManager.getInstance().ImageLoad(this,cyrPath_1,ivCyr_1,true);

        }

        String cyrPath_2 = refuseForm.getRefuseCyrImg_2();
        if(!TextUtils.isEmpty(cyrPath_2)){
            GlideManager.getInstance().ImageLoad(this,cyrPath_2,ivCyr_2,true);

        }
        //相关人员签字
        String xgryPath = refuseForm.getRefuseXgryImg();
        if(!TextUtils.isEmpty(xgryPath)){
            GlideManager.getInstance().ImageLoad(this,xgryPath,ivXgry,true);

        }
        tvSsrdDate.setText(checkNullString(refuseForm.getRefuseSsrdDate()));
        etQyAdvice.setText(checkNullString(refuseForm.getRefuseDDAdvice()));
        tvQyDate.setText(checkNullString(refuseForm.getRefuseDDDate()));
    }

    @OnClick(R.id.ll_back)
    public void toBack(){
        tipBack();
    }

    @Override
    protected void doOnBackPressed() {
//        super.doOnBackPressed();
        tipBack();
    }

    private void tipBack(){
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("退出")
                .setMessage("需要保存当前页面数据吗？")
                .addAction("不需要", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();

                    }
                })
                .addAction(0, "需要", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        saveToLocalDB();
                        dialog.dismiss();
                        finish();

                    }
                })
                .create().show();

    }

    @OnClick(R.id.button)
    public void toPdf(){
        pdfDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("PDF生成中...")
                .create(false);
        pdfDialog.show();
        saveToLocalDB();
        RefuseForm refuseForm = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(refuseFormController.getTaskId())).build().unique();
        RefuseBean refuseBean = new RefuseBean();
        refuseBean.setProducename(refuseForm.getRefuseCpmc());
        refuseBean.setCompanyname(refuseForm.getRefuseQymc());
        refuseBean.setRepresentative(refuseForm.getRefuseFrdb());
        refuseBean.setCompanyaddress(refuseForm.getRefuseQydz());
        refuseBean.setTelandman(refuseForm.getRefuseSjqyLxr());
        refuseBean.setTel(refuseForm.getRefuseSjqyLxdh());
        refuseBean.setTaskfromyear(refuseForm.getRefuseCydwRwlyYear());
        refuseBean.setTaskfrompro(refuseForm.getRefuseCydwRwlyJD());
        refuseBean.setTaskfromjd(refuseForm.getRefuseCydwRwlyPro());
        refuseBean.setSamplingtel(refuseForm.getRefuseCydwLxdh());
        refuseBean.setProgrss(refuseForm.getRefuseSsrd());
        refuseBean.setFillInDateSYear(SSRD_YRAR);
        refuseBean.setFillInDateSMonth(SSRD_MONTH);
        refuseBean.setFillInDateSDay(SSRD_DAY);
        refuseBean.setCompanymanbase64(refuseForm.getRefuseXgryImg());
        List<String> imgList = new ArrayList<>();
        imgList.add(refuseForm.getRefuseCyrImg_1());
        imgList.add(refuseForm.getRefuseCyrImg_2());
        refuseBean.setSamplemanbase64(imgList);
        refuseBean.setFillInDateDYear(DD_YRAR);
        refuseBean.setFillInDateDMonth(DD_MONTH);
        refuseBean.setFillInDateDDay(DD_DAY);
        refuseBean.setAdvice(refuseForm.getRefuseDDAdvice());
        refuseFormController.requestSubmit(refuseBean);

    }

    @OnClick(R.id.ll_cyr_qz)
    public void toSignCyr(){
        SignBean signBeanCY = new SignBean();
        signBeanCY.setId(refuseFormController.getTaskId());
        signBeanCY.setImgType(SignActivityController.IMAGE_TYPE_REFUSE_CYR);
        toSign(signBeanCY);
    }

    @OnClick(R.id.iv_xgry)
    public void toSignXgry(){
        SignBean signBeanXG = new SignBean();
        signBeanXG.setId(refuseFormController.getTaskId());
        signBeanXG.setImgType(SignActivityController.IMAGE_TYPE_REFUSE_SJDW);
        toSign(signBeanXG);
    }
    /**
     * 去手写签名
     */
    private void toSign(SignBean signBean){
        Intent intent = new Intent(this, SignActivity.class);
        intent.putExtra(SignActivityController.SIGN_TAG,signBean);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @OnClick(R.id.tv_ssrd_date)
    public void showSSDate(){
        showTimePicker(R.id.tv_ssrd_date);
    }

    @OnClick(R.id.tv_qyszdzl_date)
    public void showDDDate(){
        showTimePicker(R.id.tv_qyszdzl_date);
    }


    /**
     * 将数据保存到本地数据库
     */
    private void saveToLocalDB(){
        RefuseForm refuseForm = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(refuseFormController.getTaskId())).build().unique();
        refuseForm.setRefuseCpmc(etCpmc.getText().toString().trim());
        refuseForm.setRefuseQymc(etQymc.getText().toString().trim());
        refuseForm.setRefuseFrdb(etFrdb.getText().toString().trim());
        refuseForm.setRefuseQydz(etQydz.getText().toString().trim());
        refuseForm.setRefuseSjqyLxr(etSjdwLxr.getText().toString().trim());
        refuseForm.setRefuseSjqyLxdh(etSjdwLxrdh.getText().toString().trim());
        refuseForm.setRefuseCydwRwlyYear(etTaskFromYear.getText().toString().trim());
        refuseForm.setRefuseCydwRwlyPro(etTaskFromPro.getText().toString().trim());
        refuseForm.setRefuseCydwRwlyJD(etTaskFromJd.getText().toString().trim());
        refuseForm.setRefuseCydwLxdh(etCyLx.getText().toString().trim());
        refuseForm.setRefuseSsrd(etSsrd.getText().toString().trim());
        refuseForm.setRefuseDDAdvice(etQyAdvice.getText().toString().trim());
    }

    /**
     * 时间选择器
     */

    private void showTimePicker(int viewId){
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2015,0,1);
        endDate.set(2029,11,31);
        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if(DateUtils.isCurTime(date)){
                    setViewDate(viewId,DateUtils.getSelTime(date));
                }else{
                    new QMUIDialog.MessageDialogBuilder(RefuseFormActivity.this)
                            .setTitle("日期提醒")
                            .setMessage("您选择的日期不是今天！")
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    setViewDate(viewId,DateUtils.getSelTime(date));
                                }
                            })
                            .create().show();

                }


            }
        }).setType(new boolean[]{true, true, true, true, true, true})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText("日期选择")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年","月","日",null,null,null)//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();

        timePicker.show();
    }

    private void setViewDate(int viewId,String dateString){
        RefuseForm refuseForm = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(refuseFormController.getTaskId())).build().unique();
        String dates[] = dateString.split("-");
        switch (viewId){
            case R.id.tv_qyszdzl_date:
                String sjTime = dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
                DD_YRAR = dates[0];
                DD_MONTH = dates[1];
                DD_DAY = dates[2];
                tvQyDate.setText(sjTime);
                refuseForm.setRefuseDDDate(sjTime);
                break;
            case R.id.tv_ssrd_date:
                String scTime = dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
                SSRD_YRAR = dates[0];
                SSRD_MONTH = dates[1];
                SSRD_DAY = dates[2];
                tvSsrdDate.setText(scTime);
                refuseForm.setRefuseSsrdDate(scTime);
                break;

        }
        DBManagerFactory.getInstance().getRefuseFormManager().update(refuseForm);
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showPDFSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("PDF生成成功！")
                .create();
        sucDialog.show();


        pdfButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                sucDialog.dismiss();
            }
        },1500);

    }
    public void showPDFFail(){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("PDF生成失败，请稍候再试！")
                .create();
        failDialog.show();


        pdfButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }
    private String checkNullString(String s){
        if(TextUtils.isEmpty(s)) {
            return "";
        }else {
            return s;
        }
    }
    public void shareBySystem(String path) {
        File doc = new File(path);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(this, "com.product.sampling.fileprovider", doc);
        share.setDataAndType(contentUri, "application/pdf");
        startActivity(Intent.createChooser(share, "分享文件"));
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == SignActivityController.IMAGE_SUCCESS_RESULT_CODE){
            Bundle bundle = data.getExtras(); //读取intent的数据给bundle对象
            String imagePath = bundle.getString(SignActivityController.RESULT_IMAGE_PATH);
            String imageType = bundle.getString(SignActivityController.RESULT_IMAGE_TYPE);
            switch (imageType){
                case SignActivityController.IMAGE_TYPE_REFUSE_CYR:
                    //抽样人签字
                    RefuseForm refuseForm = refuseFormController.getRefuseFormDB(refuseFormController.getTaskId());
                    String cyrPath_1 = refuseForm.getRefuseCyrImg_1();
                    if(!TextUtils.isEmpty(cyrPath_1)){
                        GlideManager.getInstance().ImageLoad(this,cyrPath_1,ivCyr_1,true);

                    }

                    String cyrPath_2 = refuseForm.getRefuseCyrImg_2();
                    if(!TextUtils.isEmpty(cyrPath_2)){
                        GlideManager.getInstance().ImageLoad(this,cyrPath_2,ivCyr_2,true);

                    }
                    break;
                case SignActivityController.IMAGE_TYPE_REFUSE_SJDW:
                    GlideManager.getInstance().ImageLoad(this,imagePath,ivXgry,true);

                    break;

            }

        }
    }
}
