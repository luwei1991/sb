package com.product.sampling.ui.form;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.product.sampling.R;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.form.bean.FeedBackBean;
import com.product.sampling.utils.DateUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 陆伟 on 2020/4/9.
 * Copyright (c) 2020 . All rights reserved.
 */


public class FeedBackFormActivity extends BaseActivity<FeedBackFromController> {
    private static final String TAG = "FeedBackFormActivity";
    private FeedBackFromController feedBackFromController;
    private static final String YES = "0";
    private static final String NO = "1";
    private String twoman ="";
    private String showcertificates="";
    private String standardwork = "";
    private String  principlework = "";
    private String makedifficulties = "";
    private String collectfees ="";
    private String playfees ="";
    private String reimbursement = "";
    private String abuse ="";
    private String evaluate;		// 满意程度
    private String explain;
    private TimePickerView timePicker;


    @BindView(R.id.et_qymc_in)
    EditText etQymc;
    @BindView(R.id.et_cyzry_in)
    EditText etCyzry;
    @BindView(R.id.rg_01)
    RadioGroup rg01;
    @BindView(R.id.rg_02)
    RadioGroup rg02;
    @BindView(R.id.rg_03)
    RadioGroup rg03;
    @BindView(R.id.rg_04)
    RadioGroup rg04;
    @BindView(R.id.rg_05)
    RadioGroup rg05;
    @BindView(R.id.rg_06)
    RadioGroup rg06;
    @BindView(R.id.rg_07)
    RadioGroup rg07;
    @BindView(R.id.rg_08)
    RadioGroup rg08;
    @BindView(R.id.rg_09)
    RadioGroup rg09;
    @BindView(R.id.rg_10)
    RadioGroup rg10;

    @BindView(R.id.button)
    Button toPdfButton;
    @BindView(R.id.tv_ssrd_date)
    TextView tvDate;
    public QMUITipDialog pdfDialog;
    @BindView(R.id.et_explain)
    EditText etExplain;
    @BindView(R.id.ll_back)
    LinearLayout llBack;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_form);
        ButterKnife.bind(this);
        feedBackFromController = new FeedBackFromController();
        feedBackFromController.setUI(this);
        initView();

    }
    private void initView(){
        rg01.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_01_yes:
                        twoman = YES;
                        break;
                    case R.id.rb_01_no:
                        twoman = NO;
                        break;
                }

            }
        });
        rg02.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_02_yes:
                        showcertificates = YES;
                        break;
                    case R.id.rb_02_no:
                        showcertificates = NO;
                        break;
                }

            }
        });
        rg03.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_03_yes:
                        standardwork = YES;
                        break;
                    case R.id.rb_03_no:
                        standardwork = NO;
                        break;
                }

            }
        });
        rg04.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_04_yes:
                        principlework = YES;
                        break;
                    case R.id.rb_04_no:
                        principlework = NO;
                        break;
                }

            }
        });
        rg05.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_05_yes:
                        makedifficulties = YES;
                        break;
                    case R.id.rb_05_no:
                        makedifficulties = NO;
                        break;
                }

            }
        });
        rg06.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_06_yes:
                        collectfees	 = YES;
                        break;
                    case R.id.rb_06_no:
                        collectfees = NO;
                        break;
                }

            }
        });
        rg07.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_07_yes:
                        playfees = YES;
                        break;
                    case R.id.rb_07_no:
                        playfees = NO;
                        break;
                }

            }
        });
        rg08.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_08_yes:
                        reimbursement = YES;
                        break;
                    case R.id.rb_08_no:
                        reimbursement = NO;
                        break;
                }

            }
        });
        rg09.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_09_yes:
                        abuse = YES;
                        break;
                    case R.id.rb_09_no:
                        abuse = NO;
                        break;
                }

            }
        });
        rg10.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_10_yes:
                        evaluate = YES;
                        break;
                    case R.id.rb_10_no:
                        evaluate = NO;
                        break;
                    case R.id.rb_10_no_02:
                        evaluate = "2";
                        break;

                }

            }
        });


        etQymc.setText(feedBackFromController.getCompanyName());
        tvDate.setText(DateUtils.getCurTime());
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(tvDate.getId());
            }
        });
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfDialog = new QMUITipDialog.Builder(FeedBackFormActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("PDF生成中...")
                        .create(false);
                pdfDialog.show();
                FeedBackBean feedBackBean = new FeedBackBean();
                feedBackBean.setCompanyname(etQymc.getText().toString().trim());
                feedBackBean.setCheckman(etCyzry.getText().toString().trim());
                feedBackBean.setTwoman(twoman);
                feedBackBean.setShowcertificates(showcertificates);
                feedBackBean.setStandardwork(standardwork);
                feedBackBean.setPrinciplework(principlework);
                feedBackBean.setMakedifficulties(makedifficulties);
                feedBackBean.setCollectfees(collectfees);
                feedBackBean.setPlayfees(playfees);
                feedBackBean.setReimbursement(reimbursement);
                feedBackBean.setAbuse(abuse);
                feedBackBean.setEvaluate(evaluate);
                feedBackBean.setExplain(etExplain.getText().toString().trim());
                feedBackBean.setDateFille(tvDate.getText().toString().trim());
                feedBackFromController.requestSubmit(feedBackBean);
            }
        });



    }
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
                    new QMUIDialog.MessageDialogBuilder(FeedBackFormActivity.this)
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
        tvDate.setText(dateString);
    }
    @Override
    public void setUIController(FeedBackFromController sc) {
    }
    public void showPDFSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("PDF生成成功！")
                .create();
        sucDialog.show();


        toPdfButton.postDelayed(new Runnable() {
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


        toPdfButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

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
}
