//package com.product.sampling.ui.masterplate;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PaintFlagsDrawFilter;
//import android.graphics.Rect;
//import android.graphics.pdf.PdfDocument;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.print.PrintAttributes;
//import android.text.Editable;
//import android.text.InputType;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bigkoo.pickerview.builder.TimePickerBuilder;
//import com.bigkoo.pickerview.listener.OnTimeSelectListener;
//import com.bigkoo.pickerview.view.TimePickerView;
//import com.product.sampling.Constants;
//import com.product.sampling.R;
//import com.product.sampling.bean.TaskCompany;
//import com.product.sampling.manager.AccountManager;
//import com.product.sampling.manager.GlideManager;
//import com.product.sampling.ui.base.BaseActivity;
//import com.product.sampling.ui.form.bean.PopListBean;
//import com.product.sampling.ui.widget.LWTextView;
//import com.product.sampling.utils.DateUtils;
//import com.product.sampling.utils.RxSchedulersHelper;
//import com.qmuiteam.qmui.util.QMUIDisplayHelper;
//import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
//import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
//import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
//import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
//import com.qmuiteam.qmui.widget.popup.QMUIPopup;
//import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.TimeUnit;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import io.reactivex.Flowable;
//import io.reactivex.Observable;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * Created by 陆伟 on 2020/2/16.
// * Copyright (c) 2020 . All rights reserved.
// */
//
//
//public class NotCheckMasterActivity extends BaseActivity<NotCheckMasterController> implements View.OnClickListener {
//    private static final String TAG = "NotCheckFormActivity";
//    @BindView(R.id.lwtv_rwly_in)
//    LWTextView lwTeRWLYIN;//任务来源
//    @BindView(R.id.lwtv_rwlb_in)
//    LWTextView lwTeRWLYLBIN;//任务类别
//    @BindView(R.id.lwtv_cplb_in)
//    LWTextView lwTeCPLBIN;//产品类别
//    @BindView(R.id.lwtv_xkzh_in)
//    LWTextView lwTeXKZHIN;//许可证号
//    @BindView(R.id.lwtv_qymc_in)
//    LWTextView lwTeQYMCIN;//企业名称
//    @BindView(R.id.lwtv_qydz_in)
//    LWTextView lwTeQYDZIN;//企业地址
//    @BindView(R.id.lwtv_qylxr_in)
//    LWTextView lwTeQYLXRIN;//企业联系人
//    @BindView(R.id.lwtv_lxdh_in)
//    LWTextView lwTeLXDHIN;//联系电话
//    @BindView(R.id.lwtv_cplx_in)
//    LWTextView lwTeCPLXIN;//产品流向
//    @BindView(R.id.ll_back)
//    LinearLayout llBack;//返回
//    @BindView(R.id.btn_more)
//    QMUIRoundButton btnMore;//生成pdf
//    @BindView(R.id.lwtv_sjdw_nyr)
//    LWTextView lwTeSJDWNYR;//受检单位年月日
//    @BindView(R.id.lwtv_dd_nyr)
//    LWTextView lwTeDDDWNYR;//当地监管年月日
//    @BindView(R.id.lwtv_cyr_nyr)
//    LWTextView lwTeCYRNYR;//抽样人你年月日
//    @BindView(R.id.et_gcjyms_in)
//    EditText etGCMS;//过程描述
//    @BindView(R.id.et_ddscjdglbmyj)
//    EditText etDDYJ;//当地意见
//    @BindView(R.id.iv_cyr_qz_1)
//    ImageView cryQZ_1;
//    @BindView(R.id.iv_cyr_qz_2)
//    ImageView cryQZ_2;
//    @BindView(R.id.ll_cyr_qz)
//    LinearLayout llQyrQz;
//    @BindView(R.id.iv_sjdw_qz)
//    ImageView sjdwQZ;
//    @BindView(R.id.rb_yes)
//    CheckBox cbYes;
//    @BindView(R.id.rb_no)
//    CheckBox cbNo;
//    @BindView(R.id.rb_db)
//    CheckBox cbDb;//倒闭
//    @BindView(R.id.rb_zx)
//    CheckBox cbZx;//注销
//    @BindView(R.id.rb_tc)
//    CheckBox cbTc;//停产
//    @BindView(R.id.rb_zc)
//    CheckBox cbZc;//转产
//    @BindView(R.id.rb_qbyyck)
//    CheckBox cbQbyyck;//全部用于出口
//    @BindView(R.id.rb_fbhxzqhnqy)
//    CheckBox cbFbhxzqhnqy;//非本行政区划内企业
//    @BindView(R.id.rb_tgqqhcwzf)
//    CheckBox cbTgqqhcwzf;//通过前期核查，未走访
//    @BindView(R.id.rb_cpbzbcccfwn)
//    CheckBox cbCpbzbcccfwn;//产品不在本地抽查范围内
//    @BindView(R.id.rb_kcjsbz)
//    CheckBox cbKcjsbz;//库存基数不足
//    @BindView(R.id.rb_qyzgz)
//    CheckBox cbQyzgz;//企业整改中
//    @BindView(R.id.rb_fscqy)
//    CheckBox cbFscqy;//非生产企业
//    @BindView(R.id.rb_qydzbz)
//    CheckBox cbQydzbz;//企业地址不准
//    @BindView(R.id.rb_qt)
//    CheckBox cbQt;//其他
//    /**抽样人日期*/
//    public static String CYR_YEAR = "";
//    public static String CYR_MONTH = "";
//    public static String CYR_DAY = "";
//    /**当地单位日期*/
//    private static String DDDW_YEAR = "";
//    private static String DDDW_MONTH = "";
//    private static String DDDW_DAY = "";
//    /**受检单位日期*/
//    private static String SJDW_YEAR = "";
//    private static String SJDW_MONTH = "";
//    private static String SJDW_DAY = "";
//
//    public static final int IMAGE_REQUEST_CODE = 0x06;
//    /**最近一年是否销售*/
//    private static final String ZJ_SF_XX_YES = "0";
//    private static final String ZJ_SF_XX_NO = "1";
//    /**未抽到原因*/
//    private static final String NOT_R_DB = "0";
//    private static final String NOT_R_ZX = "1";
//    private static final String NOT_R_TC = "2";
//    private static final String NOT_R_ZC = "3";
//    private static final String NOT_R_QBYYCK = "4";
//    private static final String NOT_R_FBXZQHNQY = "5";
//    private static final String NOT_R_TGQQHC = "6";
//    private static final String NOT_R_CPBZBCCCFWN = "7";
//    private static final String NOT_R_KCJSBZ = "8";
//    private static final String NOT_R_QYZGZ = "9";
//    private static final String NOT_R_FSCQY = "10";
//    private static final String NOT_R_QYDZBZ = "11";
//    private static final String NOT_R_QT = "12";
//
//
//
//
//
//    private NotCheckMasterController notCheckMasterController;
//    private TimePickerView timePicker;
//    public QMUITipDialog pdfDialog;
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static final int EDIT_GCMS_OK = 0x0928;
//    private static final int EDIT_DDYJ_OK = 0x0929;
//
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (EDIT_GCMS_OK == msg.what) {
//                upDataBase(etGCMS.getId(),etGCMS.getText().toString());
//
//            }else if(EDIT_DDYJ_OK == msg.what){
//                upDataBase(etDDYJ.getId(),etDDYJ.getText().toString());
//
//            }
//
//        }
//    };
//
//    private Runnable mRunnableGC = new Runnable() {
//        @Override
//        public void run() {
//
//            mHandler.sendEmptyMessage(EDIT_GCMS_OK);
//        }
//    };
//
//    private Runnable mRunnableDD = new Runnable() {
//        @Override
//        public void run() {
//
//            mHandler.sendEmptyMessage(EDIT_DDYJ_OK);
//        }
//    };
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_not_check_form);
//        ButterKnife.bind(this);
//        initController();
//        initView();
//        initData();
//
//    }
//    private void initController(){
//        notCheckMasterController = new NotCheckMasterController();
//        setUIController(notCheckMasterController);
//        notCheckMasterController.setUI(this);
//    }
//    @Override
//    public void setUIController(NotCheckMasterController sc) {
//
//        notCheckMasterController = sc;
//    }
//
//    private void initView(){
//         lwTeRWLYIN.setOnClickListener(this);
//         lwTeRWLYLBIN.setOnClickListener(this);
//         lwTeCPLBIN.setOnClickListener(this);
//         lwTeXKZHIN.setOnClickListener(this);
//         lwTeQYMCIN.setOnClickListener(this);
//         lwTeQYDZIN.setOnClickListener(this);
//         lwTeQYLXRIN.setOnClickListener(this);
//         lwTeLXDHIN.setOnClickListener(this);
//         lwTeCPLXIN.setOnClickListener(this);
//         llBack.setOnClickListener(this);
//         btnMore.setOnClickListener(this);
//         lwTeSJDWNYR.setOnClickListener(this);
//         lwTeDDDWNYR.setOnClickListener(this);
//         lwTeCYRNYR.setOnClickListener(this);
//            etGCMS.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            //输入时的调用
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                mHandler.removeCallbacks(mRunnableGC);
//                //800毫秒没有输入认为输入完毕
//                mHandler.postDelayed(mRunnableGC, 800);
//            }
//
//
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//
//        etDDYJ.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            //输入时的调用
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                mHandler.removeCallbacks(mRunnableDD);
//                //800毫秒没有输入认为输入完毕
//                mHandler.postDelayed(mRunnableDD, 800);
//            }
//
//
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//        llQyrQz.setOnClickListener(this);
//        sjdwQZ.setOnClickListener(this);
//
//        cbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_yes,"");
//                    cbNo.setChecked(false);
//                }
//            }
//        });
//
//        cbNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_no,"");
//                    cbYes.setChecked(false);
//                }
//            }
//        });
//
//        cbDb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_db,"");
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//
//                }
//            }
//        });
//        cbZx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_zx,"");
//                    cbDb.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbTc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_tc,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//        cbZc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_zc,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbQbyyck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_qbyyck,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbFbhxzqhnqy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_fbhxzqhnqy,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbTgqqhcwzf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_tgqqhcwzf,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbCpbzbcccfwn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_cpbzbcccfwn,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbKcjsbz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_kcjsbz,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbQyzgz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_qyzgz,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//        cbFscqy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_fscqy,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbQydzbz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_qydzbz,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQt.setChecked(false);
//                }
//            }
//        });
//
//        cbQt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    upDataBase(R.id.rb_qt,"");
//                    cbDb.setChecked(false);
//                    cbZx.setChecked(false);
//                    cbTc.setChecked(false);
//                    cbZc.setChecked(false);
//                    cbQbyyck.setChecked(false);
//                    cbFbhxzqhnqy.setChecked(false);
//                    cbTgqqhcwzf.setChecked(false);
//                    cbCpbzbcccfwn.setChecked(false);
//                    cbKcjsbz.setChecked(false);
//                    cbQyzgz.setChecked(false);
//                    cbFscqy.setChecked(false);
//                    cbQydzbz.setChecked(false);
//                }
//            }
//        });
//
//
//    }
//
//    private void initData(){
//        String userid = AccountManager.getInstance().getUserId();
//        String moudleType = notCheckMasterController.getMoudleType();
//        String moudleId = notCheckMasterController.getMoudleId();
//        notCheckMasterController.requestTasksample(userid,moudleId,moudleType);
//    }
//
//    private void setData(NotCheckForm notCheckForm){
//        lwTeRWLYIN.setText(checkNullString(notCheckForm.getTaskLY()));
//        lwTeRWLYLBIN.setText(checkNullString(notCheckForm.getTaskLB()));
//        lwTeCPLBIN.setText(checkNullString(notCheckForm.getCplb()));
//        lwTeXKZHIN.setText(checkNullString(notCheckForm.getXkzh()));
//        lwTeQYMCIN.setText(checkNullString(notCheckForm.getQymc()));
//        lwTeQYDZIN.setText(checkNullString(notCheckForm.getQydz()));
//        lwTeQYLXRIN.setText(checkNullString(notCheckForm.getQylxr()));
//        lwTeLXDHIN.setText(checkNullString(notCheckForm.getLxdh()));
//        lwTeCPLXIN.setText(checkNullString(notCheckForm.getCplx()));
//        etGCMS.setText(checkNullString(notCheckForm.getGcjyms()));
//        etDDYJ.setText(checkNullString(notCheckForm.getDdjgyj()));
//        lwTeCYRNYR.setText(checkNullString(notCheckForm.getCyDwRQ()));
//
//        if(!TextUtils.isEmpty(notCheckForm.getDdDwRQ())){
//            lwTeDDDWNYR.setText(notCheckForm.getDdDwRQ());
//        }
//
//        if(!TextUtils.isEmpty(notCheckForm.getSjDwRQ())){
//            lwTeSJDWNYR.setText(notCheckForm.getSjDwRQ());
//        }
//
//
//        //最近一年是否销售记录
//        String typeZ = notCheckForm.getZjyn();
//        if(!TextUtils.isEmpty(typeZ)){
//            if(typeZ.equals(ZJ_SF_XX_YES)){
//                cbYes.setChecked(true);
//            }else if(typeZ.equals(ZJ_SF_XX_NO)){
//                cbNo.setChecked(true);
//            }
//        }
//        //未抽到样品原因
//        String typeR = notCheckForm.getWcdyyy();
//        if(!TextUtils.isEmpty(typeR)){
//            switch (typeR){
//                case  NOT_R_DB:
//                    cbDb.setChecked(true);
//                    break;
//                case NOT_R_ZX:
//                    cbZx.setChecked(true);
//                    break;
//                case NOT_R_TC:
//                    cbTc.setChecked(true);
//                    break;
//                case NOT_R_ZC:
//                    cbZc.setChecked(true);
//                    break;
//                case NOT_R_QBYYCK:
//                    cbQbyyck.setChecked(true);
//                    break;
//                case NOT_R_FBXZQHNQY:
//                    cbFbhxzqhnqy.setChecked(true);
//                    break;
//                case NOT_R_TGQQHC:
//                    cbTgqqhcwzf.setChecked(true);
//                    break;
//                case NOT_R_CPBZBCCCFWN:
//                    cbCpbzbcccfwn.setChecked(true);
//                    break;
//                case NOT_R_KCJSBZ:
//                    cbKcjsbz.setChecked(true);
//                    break;
//                case NOT_R_QYZGZ:
//                    cbQyzgz.setChecked(true);
//                    break;
//                case NOT_R_FSCQY:
//                    cbFscqy.setChecked(true);
//                    break;
//                case NOT_R_QYDZBZ:
//                    cbQydzbz.setChecked(true);
//                    break;
//                case NOT_R_QT:
//                    cbQt.setChecked(true);
//                    break;
//
//            }
//
//
//
//        }
//
//        String imagePathCYR_1 = notCheckForm.getCyrQZ_1();
//        if(!TextUtils.isEmpty(imagePathCYR_1)){
//            GlideManager.getInstance().ImageLoad(this,imagePathCYR_1,cryQZ_1,true);
//        }
//
//        String imagePathCYR_2 = notCheckForm.getCyrQZ_2();
//        if(!TextUtils.isEmpty(imagePathCYR_2)){
//            GlideManager.getInstance().ImageLoad(this,imagePathCYR_2,cryQZ_2,true);
//        }
//
//        String imagePathSJDW = notCheckForm.getQyfzrQZ();
//        if(!TextUtils.isEmpty(imagePathSJDW)){
//            GlideManager.getInstance().ImageLoad(this,imagePathSJDW,sjdwQZ,true);
//        }
//
//    }
//
//    public void verifyStoragePermissions(Activity activity) {
//        try {
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                //请求权限
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 编辑
//     */
//    private void showEditTextDialog(TextView textView, String string){
//        String deafultInputText = textView.getText().toString();
//        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
//        builder.setTitle("信息录入")
//                .setPlaceholder(string)
//                .setDefaultText(deafultInputText)
//                .setInputType(InputType.TYPE_CLASS_TEXT)
//                .addAction("取消", new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        dialog.dismiss();
//                    }
//                })
//                .addAction("确定", new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        CharSequence text = builder.getEditText().getText();
//                        if (text != null && text.length() > 0) {
//                            textView.setText(text);
//                            upDataBase(textView.getId(),text.toString());
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(NotCheckMasterActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .show();
//    }
//
//    /**
//     * 生产pdf
//     */
//    private void formToPDF(){
//        //创建pdf存储位置
//        Date currentTime = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//        String dateString = formatter.format(currentTime);
//        String path = Constants.PDF_PATH;
//        File file = new File(path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        Flowable.timer(600, TimeUnit.MILLISECONDS)
//                .compose(RxSchedulersHelper.f_io_main())
//                .subscribe(c -> saveToPDF(dateString), e -> {
//                });
//    }
//
//    /**
//     * 将页面截图并转换成PDF文件，并创建xxxx-xx-xx-tasksampleid文件为名
//     */
//    private void saveToPDF(String dateString){
//        Observable.just(dateString)
//                .observeOn(Schedulers.computation())
//                .map(new Function<String, File>() {
//                    @Override
//                    public File apply(String s) throws Exception {
//
//                        //创建截图
//                        int height = QMUIDisplayHelper.getScreenHeight(NotCheckMasterActivity.this);
//                        int statusBarHeight = QMUIDisplayHelper.getStatusBarHeight(NotCheckMasterActivity.this);
//                        int width = QMUIDisplayHelper.getScreenWidth(NotCheckMasterActivity.this);
//                        View view = getWindow().getDecorView();
//                        view.setDrawingCacheEnabled(true);
//                        view.buildDrawingCache();
//                        Bitmap activityBitmap = view.getDrawingCache();
//                        Rect rect = new Rect();
//                        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//                        Bitmap nowBitmap = Bitmap.createBitmap(activityBitmap,0,statusBarHeight,width,height-statusBarHeight*2-10);
////        try {
////            FileOutputStream fout = new FileOutputStream(Constants.PDF_PATH +"/" + dateString +"-" + checkOrRecheckFormController.getTaskSampleId()+"xxxx.png");
////            nowBitmap.compress(Bitmap.CompressFormat.PNG,100,fout);
////            fout.flush();
////            fout.close();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
//                        view.setDrawingCacheEnabled(false);
//                        PdfDocument doc = new PdfDocument();
//                        int pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils() * 72 / 1000;
////        int pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils();
////        int pageHeight = PrintAttributes.MediaSize.ISO_A4.getHeightMils();
//                        Paint paint = new Paint();
//                        paint.setAntiAlias(true);
//                        Matrix matrix = new Matrix();
//                        float scale = (float) pageWidth / (float) nowBitmap.getWidth();
//                        int pageHeight = (int) (nowBitmap.getHeight() * scale);
//
//                        matrix.postScale(scale, scale);
//                        PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,1).create();
//                        PdfDocument.Page page = doc.startPage(newPage);
//                        Canvas canvas = page.getCanvas();
//
//                        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
//                        canvas.drawBitmap(nowBitmap,matrix,paint);
//                        doc.finishPage(page);
//                        NotCheckForm notCheckForm =  DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//                        String name = dateString +"-" + "WCDYD-"+ notCheckMasterController.getTaskId()+".pdf";
//                        //将地址插入数据库
//                        notCheckForm.setPdfPath(Constants.PDF_PATH + "/" + name);
//                        File pdfFile = new File(Constants.PDF_PATH,name);
//                        FileOutputStream outputStream = null;
//                        try {
//                            outputStream = new FileOutputStream(pdfFile);
//                            doc.writeTo(outputStream);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } finally {
//                            doc.close();
//                            try {
//                                if (outputStream != null) {
//                                    outputStream.close();
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().update(notCheckForm);
//                        return pdfFile;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<File>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(File file) {
//
//                    }
//
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        if(pdfDialog != null && pdfDialog.isShowing()){
//                            pdfDialog.dismiss();
//                        }
//                        showPDFSuccess();
//                        NotCheckForm notCheckForm =  DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//                        shareBySystem(notCheckForm.getPdfPath());
//                        setResult(CheckOrRecheckFormActivity.RESULT_CODE_PDF_SUCCESS);
//
//
//                    }
//                });
//
//    }
//
//    public void showPDFSuccess(){
//        Dialog sucDialog = new QMUITipDialog.Builder(this)
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
//                .setTipWord("PDF生成成功！")
//                .create();
//        sucDialog.show();
//
//
//        btnMore.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sucDialog.dismiss();
//            }
//        },1500);
//
//    }
//    public void showPDFFail(){
//        Dialog failDialog = new QMUITipDialog.Builder(this)
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
//                .setTipWord("PDF生成失败，请稍候再试！")
//                .create();
//        failDialog.show();
//
//
//        btnMore.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                failDialog.dismiss();
//            }
//        },1500);
//
//    }
//    public void shareBySystem(String path) {
//        File doc = new File(path);
//        Intent share = new Intent();
//        share.setAction(Intent.ACTION_SEND);
//        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));
//        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        Uri contentUri = FileProvider.getUriForFile(this, "com.product.sampling.fileprovider", doc);
//        share.setDataAndType(contentUri, "application/pdf");
//        startActivity(Intent.createChooser(share, "分享文件"));
//    }
//
//    private void showTimePicker(int viewId){
//        Calendar selectedDate = Calendar.getInstance();
//        Calendar startDate = Calendar.getInstance();
//        Calendar endDate = Calendar.getInstance();
//        startDate.set(2015,0,1);
//        endDate.set(2029,11,31);
//        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date, View v) {
//                if(DateUtils.isCurTime(date)){
//                    setViewDate(viewId,DateUtils.getSelTime(date));
//                }else{
//                    new QMUIDialog.MessageDialogBuilder(NotCheckMasterActivity.this)
//                            .setTitle("日期提醒")
//                            .setMessage("您选择的日期不是今天！")
//                            .addAction("取消", new QMUIDialogAction.ActionListener() {
//                                @Override
//                                public void onClick(QMUIDialog dialog, int index) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
//                                @Override
//                                public void onClick(QMUIDialog dialog, int index) {
//                                    dialog.dismiss();
//                                    setViewDate(viewId,DateUtils.getSelTime(date));
//                                }
//                            })
//                            .create().show();
//
//                }
//
//            }
//        }).setType(new boolean[]{true, true, true, true, true, true})// 默认全部显示
//                .setCancelText("取消")//取消按钮文字
//                .setSubmitText("确定")//确认按钮文字
//                .setTitleSize(20)//标题文字大小
//                .setTitleText("日期选择")//标题文字
//                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
//                .isCyclic(true)//是否循环滚动
//                .setTitleColor(Color.BLACK)//标题文字颜色
//                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
//                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
//                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
//                .setRangDate(startDate,endDate)//起始终止年月日设定
//                .setLabel("年","月","日",null,null,null)//默认设置为年月日时分秒
//                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .isDialog(true)//是否显示为对话框样式
//                .build();
//
//        timePicker.show();
//    }
//
//    private void setViewDate(int viewId,String dateString){
//        NotCheckForm notCheckForm =  DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//
//        String dates[] = dateString.split("-");
//        String timeStr =  dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
//        switch (viewId){
//            case R.id.lwtv_dd_nyr:
//                lwTeDDDWNYR.setText(timeStr);
//                notCheckForm.setDdDwRQ(timeStr);
//                DDDW_YEAR = dates[0];
//                DDDW_MONTH = dates[1];
//                DDDW_DAY = dates[2];
//                break;
//            case R.id.lwtv_cyr_nyr:
//                lwTeCYRNYR.setText(timeStr);
//                notCheckForm.setCyDwRQ(timeStr);
//                CYR_YEAR = dates[0];
//                CYR_MONTH = dates[1];
//                CYR_DAY = dates[2];
//                break;
//            case R.id.lwtv_sjdw_nyr:
//                lwTeSJDWNYR.setText(timeStr);
//                notCheckForm.setSjDwRQ(timeStr);
//                SJDW_YEAR = dates[0];
//                SJDW_MONTH = dates[1];
//                SJDW_DAY = dates[2];
//                break;
//        }
//
//        DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().update(notCheckForm);
//
//    }
//
//    public void showPopView(View v, List<TaskCompany> list){
//        initListPopupIfNeed(v,list);
//    }
//    private void initListPopupIfNeed(View v,List<TaskCompany> list) {
//        List<PopListBean> data = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++){
//            PopListBean popListBean = new PopListBean();
//            popListBean.setId(list.get(i).getValue());
//            popListBean.setName(list.get(i).getItem());
//            data.add(popListBean);
//            if(list.get(i).getChild()!= null && list.get(i).getChild().size() > 0){
//                List<TaskCompany> childList = list.get(i).getChild();
//                for(TaskCompany taskCompany:childList){
//                    PopListBean popBean = new PopListBean();
//                    popBean.setId(taskCompany.getValue());
//                    popBean.setName("------"+taskCompany.getItem());
//                    data.add(popBean);
//                }
//            }
//
//        }
//        ArrayList<String> arrayList = new ArrayList<>();
//
//        for(int i = 0; i < data.size(); i++){
//            arrayList.add(data.get(i).getName());
//        }
//
//        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, arrayList);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 300), QMUIDisplayHelper.dp2px(this, 900), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                NotCheckForm notCheckForm =  DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//                switch (v.getId()){
//                    case R.id.lwtv_rwly_in:
//                        notCheckForm.setTaskLY(data.get(i).getName());
//                        notCheckForm.setTaskLYID(data.get(i).getId());
//                        lwTeRWLYIN.setText(data.get(i).getName());
//                        break;
//                    case R.id.lwtv_rwlb_in:
//                        notCheckForm.setTaskLB(data.get(i).getName());
//                        lwTeRWLYLBIN.setText(data.get(i).getName());
//                        break;
//
//
//                }
//                DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().update(notCheckForm);
//
//                mListPopup.dismiss();
//            }
//        });
//        mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//
//            }
//        });
//        mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
//        mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
//        mListPopup.show(v);
//    }
//    private String checkNullString(String s){
//        if(TextUtils.isEmpty(s)) {
//            return "";
//        }else {
//            return s;
//        }
//    }
//    public View getTaskFromView(){
//        return lwTeRWLYIN;
//    }
//
//    public View getTaskClassView(){
//        return lwTeRWLYLBIN;
//    }
//
//    private void upDataBase(int viewId,String text){
//        NotCheckForm notCheckForm =  DbController.getInstance(NotCheckMasterActivity.this).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//        if(notCheckForm == null){return;}
//        switch (viewId){
//            case R.id.lwtv_rwly_in:
//                notCheckForm.setTaskLY(text);
//                break;
//            case R.id.lwtv_rwlb_in:
//                notCheckForm.setTaskLB(text);
//                break;
//            case R.id.lwtv_cplb_in:
//                notCheckForm.setCplb(text);
//
//                break;
//            case R.id.lwtv_xkzh_in:
//                notCheckForm.setXkzh(text);
//                break;
//            case R.id.lwtv_qymc_in:
//                notCheckForm.setQymc(text);
//
//                break;
//            case R.id.lwtv_qydz_in:
//                notCheckForm.setQydz(text);
//
//                break;
//            case R.id.lwtv_qylxr_in:
//                notCheckForm.setQylxr(text);
//                break;
//
//            case R.id.lwtv_lxdh_in:
//                notCheckForm.setLxdh(text);
//                break;
//
//            case R.id.lwtv_cplx_in:
//                notCheckForm.setCplx(text);
//                break;
//            case R.id.ll_back:
//                onBackPressed();
//                break;
//            case R.id.btn_more:
//                pdfDialog = new QMUITipDialog.Builder(NotCheckMasterActivity.this)
//                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                        .setTipWord("PDF生成中...")
//                        .create(false);
//                pdfDialog.show();
////                formToPDF();
//                formToService();
//                break;
//
//            case R.id.et_gcjyms_in:
//                notCheckForm.setGcjyms(text);
//                break;
//            case R.id.et_ddscjdglbmyj:
//                notCheckForm.setDdjgyj(text);
//                break;
//
//            case R.id.rb_yes:
//                notCheckForm.setZjyn(ZJ_SF_XX_YES);
//                break;
//            case R.id.rb_no:
//                notCheckForm.setZjyn(ZJ_SF_XX_NO);
//                break;
//            case R.id.rb_db:
//                notCheckForm.setWcdyyy(NOT_R_DB);
//                break;
//
//            case R.id.rb_zx:
//                notCheckForm.setWcdyyy(NOT_R_ZX);
//                break;
//            case R.id.rb_tc:
//                notCheckForm.setWcdyyy(NOT_R_TC);
//                break;
//            case R.id.rb_zc:
//                notCheckForm.setWcdyyy(NOT_R_ZC);
//                break;
//            case R.id.rb_qbyyck:
//                notCheckForm.setWcdyyy(NOT_R_QBYYCK);
//                break;
//            case R.id.rb_fbhxzqhnqy:
//                notCheckForm.setWcdyyy(NOT_R_FBXZQHNQY);
//                break;
//            case R.id.rb_tgqqhcwzf:
//                notCheckForm.setWcdyyy(NOT_R_TGQQHC);
//                break;
//            case R.id.rb_cpbzbcccfwn:
//                notCheckForm.setWcdyyy(NOT_R_CPBZBCCCFWN);
//                break;
//            case R.id.rb_kcjsbz:
//                notCheckForm.setWcdyyy(NOT_R_KCJSBZ);
//                break;
//            case R.id.rb_qyzgz:
//                notCheckForm.setWcdyyy(NOT_R_QYZGZ);
//                break;
//            case R.id.rb_fscqy:
//                notCheckForm.setWcdyyy(NOT_R_FSCQY);
//                break;
//            case R.id.rb_qydzbz:
//                notCheckForm.setWcdyyy(NOT_R_QYDZBZ);
//                break;
//            case R.id.rb_qt:
//                notCheckForm.setWcdyyy(NOT_R_QT);
//                break;
//
//        }
//        DbController.getInstance(this).getDaoSession().getNotCheckFormDao().update(notCheckForm);
//    }
//
//    private void formToService(){
//        NotCheckForm notCheckForm = DBManagerFactory.getInstance().getNotCheckFormManager().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//        NotCheckedFormBean notCheckedFormBean = new NotCheckedFormBean();
//        notCheckedFormBean.setTaskFrom(notCheckForm.getTaskLY());
//        notCheckedFormBean.setTaskClass(notCheckForm.getTaskLB());
//        notCheckedFormBean.setProClass(notCheckForm.getCplb());
//        notCheckedFormBean.setCerId(notCheckForm.getXkzh());
//        notCheckedFormBean.setComName(notCheckForm.getQymc());
//        notCheckedFormBean.setComAddress(notCheckForm.getQydz());
//        notCheckedFormBean.setComConName(notCheckForm.getQylxr());
//        notCheckedFormBean.setConTel(notCheckForm.getLxdh());
//        notCheckedFormBean.setSaleType(notCheckForm.getZjyn());
//        notCheckedFormBean.setProTo(notCheckForm.getCplx());
//        notCheckedFormBean.setNotCheckReason(notCheckForm.getWcdyyy());
//        notCheckedFormBean.setProgressDes(notCheckForm.getGcjyms());
//        notCheckedFormBean.setLocalAdvice(notCheckForm.getDdjgyj());
//        List<String> imgList = new ArrayList<>();
//        imgList.add(notCheckForm.getCyrQZ_1());
//        imgList.add(notCheckForm.getCyrQZ_2());
//        notCheckedFormBean.setCheckPersonImage(imgList);
//        notCheckedFormBean.setCheckPerYear(CYR_YEAR);
//        notCheckedFormBean.setCheckPerMonth(CYR_MONTH);
//        notCheckedFormBean.setCheckPerDay(CYR_DAY);
//        //当地部门日期
//        notCheckedFormBean.setLocalYear(DDDW_YEAR);
//        notCheckedFormBean.setLocalMonth(DDDW_MONTH);
//        notCheckedFormBean.setLocalDay(DDDW_DAY);
//        //受检单位日期
//        notCheckedFormBean.setAcceptYear(SJDW_YEAR);
//        notCheckedFormBean.setAcceptMonth(SJDW_MONTH);
//        notCheckedFormBean.setAcceptDay(SJDW_DAY);
//        notCheckedFormBean.setAcceptImage(notCheckForm.getQyfzrQZ());
//        notCheckMasterController.requestSubmit(notCheckedFormBean);
//
//
//    }
//
//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == IMAGE_REQUEST_CODE && resultCode == SignActivityController.IMAGE_SUCCESS_RESULT_CODE){
//            Bundle bundle = data.getExtras(); //读取intent的数据给bundle对象
//            String imagePath = bundle.getString(SignActivityController.RESULT_IMAGE_PATH);
//            String imageType = bundle.getString(SignActivityController.RESULT_IMAGE_TYPE);
//            if(imageType.equals(SignActivityController.IMAGE_TYPE_NOT_CHECK_CYR)){
//                NotCheckForm notCheckForm = DBManagerFactory.getInstance().getNotCheckFormManager().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(notCheckMasterController.getTaskId())).build().unique();
//                String imagePathCYR_1 = notCheckForm.getCyrQZ_1();
//                if(!TextUtils.isEmpty(imagePathCYR_1)){
//                    GlideManager.getInstance().ImageLoad(this,imagePathCYR_1,cryQZ_1,true);
//                }
//
//                String imagePathCYR_2 = notCheckForm.getCyrQZ_2();
//                if(!TextUtils.isEmpty(imagePathCYR_2)){
//                    GlideManager.getInstance().ImageLoad(this,imagePathCYR_2,cryQZ_2,true);
//                }
//            }else if(imageType.equals(SignActivityController.IMAGE_TYPE_NOT_CHECK_SJDW)){
//                GlideManager.getInstance().ImageLoad(this,imagePath,sjdwQZ,true);
//            }
//
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.lwtv_rwly_in:
//                notCheckMasterController.requestTaskCompanyInfo(CheckOrRecheckFormController.TYPE_TASK_FROM);
//                break;
//            case R.id.lwtv_rwlb_in:
//                notCheckMasterController.requestTaskCompanyInfo(CheckOrRecheckFormController.TYPE_TASK_CALSS);
//                break;
//            case R.id.lwtv_cplb_in:
//                showEditTextDialog(lwTeCPLBIN,"产品类别");
//                break;
//            case R.id.lwtv_xkzh_in:
//                showEditTextDialog(lwTeXKZHIN,"许可证号");
//                break;
//            case R.id.lwtv_qymc_in:
//                showEditTextDialog(lwTeQYMCIN,"企业名称");
//
//                break;
//            case R.id.lwtv_qydz_in:
//                showEditTextDialog(lwTeQYDZIN,"企业地址");
//
//                break;
//            case R.id.lwtv_qylxr_in:
//                showEditTextDialog(lwTeQYLXRIN,"企业联系人");
//                break;
//
//            case R.id.lwtv_lxdh_in:
//                showEditTextDialog(lwTeLXDHIN,"联系电话");
//
//                break;
//
//            case R.id.lwtv_cplx_in:
//                showEditTextDialog(lwTeCPLXIN,"产品流向");
//                break;
//            case R.id.ll_back:
//                onBackPressed();
//                break;
//            case R.id.btn_more:
//                pdfDialog = new QMUITipDialog.Builder(NotCheckMasterActivity.this)
//                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                        .setTipWord("PDF生成中...")
//                        .create(false);
//                pdfDialog.show();
////                formToPDF();
//                formToService();
//                break;
//            case R.id.lwtv_sjdw_nyr:
//                showTimePicker(R.id.lwtv_sjdw_nyr);
//                break;
//            case R.id.lwtv_dd_nyr:
//                showTimePicker(R.id.lwtv_dd_nyr);
//                break;
//            case R.id.lwtv_cyr_nyr:
//                showTimePicker(R.id.lwtv_cyr_nyr);
//                break;
//
//            case R.id.iv_sjdw_qz:
//                SignBean signBeanSJ = new SignBean();
//                signBeanSJ.setId(notCheckMasterController.getTaskId());
//                signBeanSJ.setImgType(SignActivityController.IMAGE_TYPE_NOT_CHECK_SJDW);
//                toSign(signBeanSJ);
//                break;
//
//            case R.id.ll_cyr_qz:
//                SignBean signBeanCY = new SignBean();
//                signBeanCY.setId(notCheckMasterController.getTaskId());
//                signBeanCY.setImgType(SignActivityController.IMAGE_TYPE_NOT_CHECK_CYR);
//                toSign(signBeanCY);
//                break;
//        }
//
//    }
//
//    /**
//     * 去手写签名
//     */
//    private void toSign(SignBean signBean){
//        Intent intent = new Intent(this, SignActivity.class);
//        intent.putExtra(SignActivityController.SIGN_TAG,signBean);
//        startActivityForResult(intent,IMAGE_REQUEST_CODE);
//    }
//}
