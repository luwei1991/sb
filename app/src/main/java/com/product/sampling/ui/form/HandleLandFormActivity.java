package com.product.sampling.ui.form;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.product.sampling.R;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.manager.GlideManager;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.form.bean.FormHandleBean;
import com.product.sampling.ui.form.bean.SignBean;
import com.product.sampling.ui.sign.SignActivity;
import com.product.sampling.ui.sign.SignActivityController;
import com.product.sampling.utils.DateUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.product.sampling.db.manager.HandleFormManager.HANDLE_FORM_QUERY_ID;

/**
 * 处置单（横版）
 * Created by 陆伟 on 2020/5/18.
 * Copyright (c) 2020 . All rights reserved.
 */


public class HandleLandFormActivity extends BaseActivity <HandleLandFormController> implements View.OnClickListener {
    private static final String TAG = "HandleLandFormActivity";
    private HandleLandFormController handleLandFormController;
    private TimePickerView timePicker;
    public static final int IMAGE_REQUEST_CODE = 0x06;
    private static String year;
    private static String month;
    private static String day;
    public QMUITipDialog pdfDialog;
    @BindView(R.id.et_cydbh)
    EditText etCydbh;
    @BindView(R.id.et_dwmc)
    EditText etDwmc;
    @BindView(R.id.et_wtdw)
    EditText etWtdw;
    @BindView(R.id.et_time)
    EditText etTime;
    @BindView(R.id.et_sjdw_time)
    EditText etSjdwTime;
    @BindView(R.id.et_cyr_time)
    EditText etCyrTime;
    @BindView(R.id.rg_type)
    RadioGroup rgType;
    @BindView(R.id.rb_sale)
    RadioButton rbSale;//销售
    @BindView(R.id.rb_product)
    RadioButton rbProduct;//生产
    @BindView(R.id.et_cpmc)
    EditText etCpmc;
    @BindView(R.id.et_ggxh)
    EditText etGgxh;
    @BindView(R.id.rg_sample_type)
    RadioGroup rgSampleType;
    @BindView(R.id.rb_by)
    RadioButton rbByyp;
    @BindView(R.id.rb_jb)
    RadioButton rbJb;
    @BindView(R.id.iv_sjdw)
    ImageView ivSjdw;
    @BindView(R.id.iv_cry_1)
    ImageView ivCry1;
    @BindView(R.id.iv_cry_2)
    ImageView ivCry2;
    @BindView(R.id.pdf)
    Button btnPdf;
    @BindView(R.id.tv_hn)
    TextView tvHn;
    @BindView(R.id.tv_gj)
    TextView tvGj;
    @BindView(R.id.iv_calendar)
    ImageView ivCalendar;
    @BindView(R.id.iv_cyr_calendar)
    ImageView ivCyrCalendar;
    @BindView(R.id.iv_sjdw_calendar)
    ImageView ivSjdwCalendar;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.fl_cyr)
    FrameLayout flCyr;
    @BindView(R.id.fl_sjdw)
    FrameLayout flSjdw;
    @BindView(R.id.iv_sub)
    ImageView ivSub;
    @BindView(R.id.iv_sup)
    ImageView ivSup;

    private static HandleForm handleForm;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLE_FORM_QUERY_ID:
                    initData((HandleForm) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_handle_land_form, null);
        setContentView(root);
        ButterKnife.bind(this);
        handleLandFormController = new HandleLandFormController();
        setUIController(handleLandFormController);
        initView();
    }

    private void initView(){
        tvHn.setOnClickListener(this);
        tvGj.setOnClickListener(this);
        ivCalendar.setOnClickListener(this);
        ivCyrCalendar.setOnClickListener(this);
        ivSjdwCalendar.setOnClickListener(this);
        llBack.setOnClickListener(this);
        flCyr.setOnClickListener(this);
        flSjdw.setOnClickListener(this);
        btnPdf.setOnClickListener(this);
        ivSub.setOnClickListener(this);
        ivSup.setOnClickListener(this);
        handleLandFormController.getHandleFormToDB(handler);
    }

    private void initData(HandleForm handleForm){
        this.handleForm = handleForm;
        if(handleForm != null){
            if(!TextUtils.isEmpty(handleForm.getCydCode())){
                etCydbh.setText(handleForm.getCydCode());
            }
            if(!TextUtils.isEmpty(handleForm.getSjdwmc())){
                etDwmc.setText(handleForm.getSjdwmc());
            }

            if(!TextUtils.isEmpty(handleForm.getCpmc())){
                etCpmc.setText(handleForm.getCpmc());
            }
            if(!TextUtils.isEmpty(handleForm.getGgxh())){
                etGgxh.setText(handleForm.getGgxh());
            }

            if(!TextUtils.isEmpty(handleForm.getCydwmc())){
                etWtdw.setText(handleForm.getCydwmc());
            }
            if(!TextUtils.isEmpty(handleForm.getYear())){
                String dateString = handleForm.getYear()+"-"+ handleForm.getMonth()+"-" + handleForm.getDay();
                etTime.setText(dateString);
                etSjdwTime.setText(dateString);
                etCyrTime.setText(dateString);
                year = handleForm.getYear();
                month = handleForm.getMonth();
                day = handleForm.getDay();
            }

            if(!TextUtils.isEmpty(handleForm.getSaleOrProductType())){
                rgType.clearCheck();
                if (handleForm.getSaleOrProductType().equals("0")) {//0 销售，1生产
                    rbSale.setChecked(true);
                } else {
                    rbProduct.setChecked(true);
                }
            }

            if(!TextUtils.isEmpty(handleForm.getCpmc())){
                etCpmc.setText(handleForm.getCpmc());
            }

            if(!TextUtils.isEmpty(handleForm.getGgxh())){
                etGgxh.setText(handleForm.getGgxh());
            }


            if(!TextUtils.isEmpty(handleForm.getProState())){
                rgSampleType.clearCheck();
                if (handleForm.getProState().equals("0")) {
                    rbByyp.setChecked(true);
                } else {
                    rbJb.setChecked(true);
                }
            }

            if(!TextUtils.isEmpty(handleForm.getSjdwImg())){
                GlideManager.getInstance().ImageLoad(HandleLandFormActivity.this,handleForm.getSjdwImg(),ivSjdw,true);

            }
            if(!TextUtils.isEmpty(handleForm.getCyrImg_1())){
                GlideManager.getInstance().ImageLoad(HandleLandFormActivity.this,handleForm.getCyrImg_1(),ivCry1,true);
            }
            if(!TextUtils.isEmpty(handleForm.getCyrImg_2())){
                GlideManager.getInstance().ImageLoad(HandleLandFormActivity.this,handleForm.getCyrImg_2(),ivCry2,true);
            }
        }

    }


    @Override
    protected void doOnBackPressed() {
        saveInfo();
        super.doOnBackPressed();
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
                    new QMUIDialog.MessageDialogBuilder(HandleLandFormActivity.this)
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
    /**
     * 去手写签名
     */
    private void toSign(SignBean signBean){
        Intent intent = new Intent(this, SignActivity.class);
        intent.putExtra(SignActivityController.SIGN_TAG,signBean);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }


    private void setViewDate(int viewId,String dateString){
        String dates[] = dateString.split("-");
        switch (viewId){
            case R.id.et_time:
                year = dates[0];
                month = dates[1];
                day = dates[2];

                etTime.setText(dateString);
                etSjdwTime.setText(dateString);
                etCyrTime.setText(dateString);

                break;
            case R.id.et_sjdw_time:
                year = dates[0];
                month = dates[1];
                day = dates[2];

                etTime.setText(dateString);
                etSjdwTime.setText(dateString);
                etCyrTime.setText(dateString);
                break;
            case R.id.et_cyr_time:
                year = dates[0];
                month = dates[1];
                day = dates[2];

                etTime.setText(dateString);
                etSjdwTime.setText(dateString);
                etCyrTime.setText(dateString);
                break;
        }

    }

    /**
     * 保存
     */
    private void saveInfo(){
        handleForm.setSampleId(handleLandFormController.getTaskSampleId());
        handleForm.setCydCode(etCydbh.getText().toString().trim());
        handleForm.setSjdwmc(etDwmc.getText().toString().trim());
        handleForm.setCydwmc(etWtdw.getText().toString().trim());
        handleForm.setYear(year);
        handleForm.setMonth(month);
        handleForm.setDay(day);
        if(rbSale.isChecked()){//1是生产，0是销售
            handleForm.setSaleOrProductType("0");
        }
        if(rbProduct.isChecked()){
            handleForm.setSaleOrProductType("1");
        }

        if(rbByyp.isChecked()){//1是检毕，0是备用
            handleForm.setProState("0");
        }

        if(rbJb.isChecked()){
            handleForm.setProState("1");
        }
        handleForm.setCpmc(etCpmc.getText().toString().trim());
        handleForm.setGgxh(etGgxh.getText().toString().trim());
        DBManagerFactory.getInstance().getHandleManager().updateByHandleForm(handleForm);
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

    public void showPDFSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("PDF生成成功！")
                .create();
        sucDialog.show();


        btnPdf.postDelayed(new Runnable() {
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


        btnPdf.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }

    private void formToService( ){
        FormHandleBean handleBean = new FormHandleBean();
        handleBean.setCheckFormID(handleForm.getCydCode());
        handleBean.setAcceptComName(handleForm.getSjdwmc());
        handleBean.setSupComName(handleForm.getCydwmc());
        handleBean.setYear(year);
        handleBean.setMonth(month);
        handleBean.setDay(day);
        handleBean.setSaleOrProductType(handleForm.getSaleOrProductType());//1是生产，0是销售
        handleBean.setCheckPersonYear(year);
        handleBean.setCheckPersonMonth(month);
        handleBean.setCheckPersonDay(day);
        List<String> imgList = new ArrayList<>();
        imgList.add(handleForm.getCyrImg_1());
        imgList.add(handleForm.getCyrImg_2());
        handleBean.setCheckPersonImage(imgList);
        handleBean.setProName(handleForm.getCpmc());
        handleBean.setProModel(handleForm.getGgxh());
        handleBean.setProState(handleForm.getProState());
        handleBean.setAcceptMonth(month);
        handleBean.setAcceptYear(year);
        handleBean.setAcceptDay(day);
        handleBean.setAcceptImage(handleForm.getSjdwImg());
        handleLandFormController.requestSubmit(handleBean);
    }

    @Override
    public void setUIController(HandleLandFormController sc) {
        sc.setUI(this);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == SignActivityController.IMAGE_SUCCESS_RESULT_CODE){
            Bundle bundle = data.getExtras(); //读取intent的数据给bundle对象
            String imagePath = bundle.getString(SignActivityController.RESULT_IMAGE_PATH);
            String imageType = bundle.getString(SignActivityController.RESULT_IMAGE_TYPE);
            switch (imageType){
                case SignActivityController.IMAGE_TYPE_HANDLE_CYR:
                    if(handleLandFormController != null){
                        if(handleForm != null){
                            String imagePath_1 = handleForm.getCyrImg_1();
                            if(!TextUtils.isEmpty(imagePath_1)){
                                handleForm.setCyrImg_1(imagePath_1);
                                GlideManager.getInstance().ImageLoad(this,imagePath_1,ivCry1,true);
                            }

                            String imagePath_2 = handleForm.getCyrImg_2();
                            if(!TextUtils.isEmpty(imagePath_2)){
                                handleForm.setCyrImg_2(imagePath_2);
                                GlideManager.getInstance().ImageLoad(this,imagePath_2,ivCry2,true);
                            }
                        }
//                        DBManagerFactory.getInstance().getHandleManager().updateByHandleForm(handleForm);
                    }
                    break;

                case  SignActivityController.IMAGE_TYPE_HANDLE_SJDW:
                    handleForm.setSjdwImg(imagePath);
                    GlideManager.getInstance().ImageLoad(this,imagePath,ivSjdw,true);
//                    DBManagerFactory.getInstance().getHandleManager().updateByHandleForm(handleForm);
                    break;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_hn:
                etWtdw.setText("湖南省市场监督管理局");
                break;
            case R.id.tv_gj:
                etWtdw.setText("国家市场监督管理");
                break;
            case R.id.iv_calendar:
                showTimePicker(R.id.et_time);
                break;
            case R.id.iv_cyr_calendar:
                showTimePicker(R.id.et_cyr_time);
                break;
            case R.id.iv_sjdw_calendar:
                showTimePicker(R.id.et_sjdw_time);
                break;
            case R.id.ll_back:
                saveInfo();
                onBackPressed();
                break;
            case R.id.fl_cyr:
                SignBean signBeanCY = new SignBean();
                signBeanCY.setImgType(SignActivityController.IMAGE_TYPE_HANDLE_CYR);
                signBeanCY.setId(handleLandFormController.getTaskSampleId());
                toSign(signBeanCY);
                break;

            case R.id.fl_sjdw:
                SignBean signBeanSJ = new SignBean();
                signBeanSJ.setImgType(SignActivityController.IMAGE_TYPE_HANDLE_SJDW);
                signBeanSJ.setId(handleLandFormController.getTaskSampleId());
                toSign(signBeanSJ);
                break;

            case R.id.pdf:
                pdfDialog = new QMUITipDialog.Builder(HandleLandFormActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("PDF生成中...")
                        .create();
                pdfDialog.show();
                saveInfo();
                formToService();
                break;
            case R.id.iv_sup:
                etGgxh.setText(etGgxh.getText().toString().trim()+ "<sup></sup>");
                etGgxh.requestFocus();
                etGgxh.setSelection(etGgxh.getText().toString().trim().indexOf(">")+1);
                break;
            case R.id.iv_sub:
                etGgxh.setText(etGgxh.getText().toString().trim() + "<sub></sub>");
               etGgxh.requestFocus();
               etGgxh.setSelection(etGgxh.getText().toString().trim().indexOf(">")+1);
                break;
        }
    }

}
