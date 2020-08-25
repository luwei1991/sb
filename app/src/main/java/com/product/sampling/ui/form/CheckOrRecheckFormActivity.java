package com.product.sampling.ui.form;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.print.PrintAttributes;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.manager.GlideManager;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.eventmessage.SelectedTaskCompanyMessage;
import com.product.sampling.ui.form.bean.CodeCompany;
import com.product.sampling.ui.form.bean.FormCheckBean;
import com.product.sampling.ui.form.bean.PopListBean;
import com.product.sampling.ui.form.bean.SignBean;
import com.product.sampling.ui.masterplate.MasterplterMainActivity;
import com.product.sampling.ui.masterplate.bean.MasterpleListBean;
import com.product.sampling.ui.sign.SignActivity;
import com.product.sampling.ui.sign.SignActivityController;
import com.product.sampling.ui.widget.LWTextView;
import com.product.sampling.utils.DateUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.view.SingleClick;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.product.sampling.db.manager.CheckFormManager.CHECK_FORM_QUERY_ID;
import static com.product.sampling.db.manager.HandleFormManager.HANDLE_FORM_QUERY_ID;


/**
 * 抽查复查抽样单
 * Created by 陆伟 on 2019/11/5.
 * Copyright (c) 2019 . All rights reserved.
 */


public class CheckOrRecheckFormActivity extends BaseActivity<CheckOrRecheckFormController> implements View.OnClickListener {
    private static final String TAG = "CheckOrRecheckFormActivity";
    public static final int IMAGE_REQUEST_CODE = 0x06;
    private LWTextView title;
    /**受检单位信息*/
    private LWTextView lwTvCheckID;//编号
    private LWTextView lwTvReportID;//报告编号
    private LWTextView lwTvTaskFrom;//任务来源
    private LWTextView lwTvTaskClass;//任务类别
    private LWTextView lwTvAcceptComName;//受检单位
    private LWTextView lwTvAcceptComAddres;//受检
    private LWTextView lwTvFaRen;//受检单位法人
    private LWTextView lwTvConnectName;//联系人
    private LWTextView lwTvConnectTel;
    private LWTextView lwTvReport;
    /**======================生产单位信息==========================*/
    private LWTextView lwTvProductName;
    private LWTextView lwTvProductAddress;
    private LWTextView lwTvProductEMSID;
    private LWTextView lwTvProdictFaRen;
    private LWTextView lwTvProductTelName;
    private LWTextView lwTvProductTleNum;
    private LWTextView lwTvProductBusinessLicense;
    private LWTextView lwTevProductID;
    private LWTextView lwTevProductCYRYS;
    private LWTextView lwTevProductYYSR;
    private LWTextView lwTevProductYYSRDW;
    private LWTextView lwTevProductCYRY;//从业人员单位


    private RadioButton rb_GY//国有
            ,rb_SY
            ,rb_JT
            ,rb_YXZRGS
            ,rb_LY
            ,rb_GFYXGS
            ,rb_GFHZ
            ,rb_QTQY
            ,rb_HZJY
            ,rb_HZOJY
            ,rb_GATDZJY
            ,rb_GATTZGFYXGS
            ,rb_ZWHZ
            ,rb_ZWHZO
            ,rb_WZQY
            ,rb_WSTZGFYXGS;

    private static final String PRODUCT_COMPANY_SMALL_TYPE_GY = "1";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_SY = "2";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_JT = "3";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_YXZRGS = "4";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_LY = "5";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_GFYXGS = "6";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_GFHZ = "7";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_QTQY = "8";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_HZJY = "9";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_HZOJY = "10";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_DZJY = "11";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS = "12";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_ZWHZ = "13";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_ZWHZO = "14";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_WZQY = "15";
    private static final String PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS = "16";





    /**===================受检产品信息===========================*/
    /**工业产品生产许可证*/
    private static final String LINSENCE_TYPE_GYCPSCXKZ = "6";
    /**SC许可证*/
    private static final String LINSENCE_TYPE_SC = "2";
    /**CCC许可证*/
    private static final String LINSENCE_TYPE_CCC = "4";
    /**其他*/
    private static final String LINSENCE_TYPE_OTHERS = "5";
    /**无*/
    private static final String LINSENCE_TYPE_NONE = "7";


    private CheckBox rbGYCPSCXKZ,rbSC,rbCCC,rbOthers;
    private LWTextView lwTvAcceptProduceInfoZSID;
    private LWTextView lwTvAcceptProduceCPMCJB;
    private EditText lwTvAcceptProduceCPMC;
    private EditText lwTvAcceptProduceGGXH;
    private TextView lwTvAcceptProduceGGXHJB;
    private LWTextView lwTvAcceptProduceSCRI;
    private LWTextView lwTvAcceptProduceSB;
    private LWTextView lwTvAcceptProduceCYSL;
    private LWTextView lwTvAcceptProduceCPDJ;
    private LWTextView lwTvAcceptProduceBZZXBZ;
    private LWTextView lwTvAcceptProduceCYRQ;
    private LWTextView lwTvAcceptProduceFYZT;
    private LWTextView lwTvAcceptProduceJSYDD;
    private LWTextView lwTvAcceptProduceJSYJZRQ;

    private LWTextView lwTvAcceptProducePL;
//    private LinearLayout lwTvAcceptProducePLDW;
    private LWTextView lwTvAcceptProduceDJ;
    private LWTextView lwTvAcceptProduceBYLJFCDD;

    private LWTextView lwTvAcceptProduceCYSLDW;//抽样数量单位
    private LWTextView lwTvAcceptProduceDJDW;//抽样数量单位
    //受检单位0批量  1基数
//    private CheckBox checkBoxPL;//批量
//    private CheckBox checkBoxJS;//基数
//    private static final String ACCEPT_PL = "0";
//    private static final String ACCEPT_JS = "1";



    /**=================================抽样单位信息=====================*/
    private LWTextView lwTvSampleCompanyName;
    private LWTextView lwTvSampleCompanyAddress;
    private LWTextView lwTvSampleCompanyEMSID;
    private LWTextView lwTvSampleCompanyConnectName;
    private LWTextView lwTvSampleCompanyConnectTel;
    private LWTextView lwTvSampleCompanyConnectEmail;
    /**===================================备注信息======================*/
    private EditText lwTvBz;
    private TextView lwTvBzJB;

    private ImageView ivSJDW,ivSCDW,cyrImg_1,cyrImg_2;
    private LinearLayout llCYR;

    private LWTextView lwTvSCDW_NYR,lwTvSJDW_NYR,lwTvCYR_NYR;
    private CheckOrRecheckFormController checkOrRecheckFormController;
    private TimePickerView timePicker;

    private LWTextView bzXHBAH;
    private LWTextView bzGYF;
    private LWTextView bzGYFDW;



    private ImageView ivQIANZHANG;//电子签章
    /**================================更多===================================*/
    private QMUIRoundButton btnMore;
//    private static final int SCAN = 0;
    private static final int PDF = 0;
    private static final int MOBAN = 1;
    private static final int COMPANYID = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int RESULT_CODE_PDF_SUCCESS = 0x02;
    private LinearLayout llBack;
    public QMUITipDialog pdfDialog;
    private static final int EDIT_OK = 0x0929;
    private static final int EDIT_GGXH_OK = 0x0928;
    private static final int EDIT_CPMC_OK = 0x0927;
    /**抽样人日期===用于生成PDF*/
    public static String CYR_DATE_YEAR = "";
    public static String CYR_DATE_MONTH = "";
    public static String CYR_DATE_DAY = "";
    /**受检单位日期===用于生成PDF*/
    public static String SJDW_DATE_YEAR = "";
    public static String SJDW_DATE_MONTH = "";
    public static String SJDW_DATE_DAY = "";
    /**生产单位日期===用于生成PDF*/
    public static String SCDW_DATE_YEAR = "";
    public static String SCDW_DATE_MONTH = "";
    public static String SCDW_DATE_DAY = "";
    /**
     * 多选
     */
    private QMUIPopup mNormalPopup;
    private static CheckForm checkForm;
    private static HandleForm handleForm;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EDIT_OK:
                    upDataBase(lwTvBz.getId(),lwTvBz.getText().toString());
                    break;
                case EDIT_GGXH_OK:
                    upDataBase(lwTvAcceptProduceGGXH.getId(),lwTvAcceptProduceGGXH.getText().toString());
                    break;
                case EDIT_CPMC_OK:
                    upDataBase(lwTvAcceptProduceCPMC.getId(),lwTvAcceptProduceCPMC.getText().toString());
                    break;
                case CHECK_FORM_QUERY_ID:
                    checkForm = (CheckForm) msg.obj;
                    if(checkForm != null){
                         setData(checkForm);
                        checkOrRecheckFormController.getHandleFormToDB(checkOrRecheckFormController.getTaskSampleId(),mHandler);
                     }else{
                        checkOrRecheckFormController.requestTasksamples(AccountManager.getInstance().getUserId(),checkOrRecheckFormController.getTaskId());
                     }
                    break;
                case HANDLE_FORM_QUERY_ID:
                    handleForm = (HandleForm) msg.obj;
                    break;
            }

        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

    private Runnable mRunnableGC = new Runnable() {
        @Override
        public void run() {

            mHandler.sendEmptyMessage(EDIT_GGXH_OK);
        }
    };
    private Runnable mRunnableCPMC = new Runnable() {
        @Override
        public void run() {

            mHandler.sendEmptyMessage(EDIT_CPMC_OK);
        }
    };


    @Override
    public void setUIController(CheckOrRecheckFormController sc) {
        checkOrRecheckFormController = sc;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activit_check_form_default_new, null);
        setContentView(root);
        initController();
        initView();
        initData();
        EventBus.getDefault().register(this);
    }


    private void initController(){
        checkOrRecheckFormController = new CheckOrRecheckFormController();
        setUIController(checkOrRecheckFormController);
        checkOrRecheckFormController.setUI(this);
    }

    private void initView(){
        title = findViewById(R.id.lwtv_titile);
        title.getPaint().setFakeBoldText(true);//加粗

        lwTvCheckID = findViewById(R.id.lwtv_id);
        lwTvReportID = findViewById(R.id.lwtv_report_id);
        lwTvReportID.setOnClickListener(this);

        lwTvReport = findViewById(R.id.lwtv_descrable02);
        lwTvReport.setOnClickListener(this);
        lwTvTaskFrom = findViewById(R.id.lwtv_task_from);
        lwTvTaskFrom.setOnClickListener(this);
        lwTvTaskClass = findViewById(R.id.lwtv_task_class);
        lwTvTaskClass.setOnClickListener(this);
        lwTvAcceptComName = findViewById(R.id.lwtv_accept_com_desc_name_in);
        lwTvAcceptComName.setOnClickListener(this);
        lwTvFaRen = findViewById(R.id.lwtv_descrable_faren_in);
        lwTvFaRen.setOnClickListener(this);
        lwTvConnectName = findViewById(R.id.lwtv_connect_name_in);
        lwTvConnectName.setOnClickListener(this);
        lwTvConnectTel = findViewById(R.id.lwtv_connect_tel_in);
        lwTvConnectTel.setOnClickListener(this);
        lwTvAcceptComAddres = findViewById(R.id.lwtv_accept_com_desc_address_in);
        lwTvAcceptComAddres.setOnClickListener(this);

        //生产单位信息
        lwTvProductName = findViewById(R.id.lwtv_product_com_name_in);
        lwTvProductName.setOnClickListener(this);
        lwTvProductAddress = findViewById(R.id.lwtv_product_com_address_in);
        lwTvProductAddress.setOnClickListener(this);
        lwTvProductEMSID = findViewById(R.id.lwtv_product_com_ems_id_in);
        lwTvProductEMSID.setOnClickListener(this);
        lwTvProdictFaRen = findViewById(R.id.lwtv_product_com_faren_in);
        lwTvProdictFaRen.setOnClickListener(this);
        lwTvProductTelName = findViewById(R.id.lwtv_product_com_tel_name_in);
        lwTvProductTelName.setOnClickListener(this);
        lwTvProductTleNum = findViewById(R.id.lwtv_product_com_tel_num_in);
        lwTvProductTleNum.setOnClickListener(this);
        lwTvProductBusinessLicense = findViewById(R.id.lwtv_product_com_business_license_in);
        lwTvProductBusinessLicense.setOnClickListener(this::onClick);
        lwTevProductID = findViewById(R.id.lwtv_product_com_id_in);
        lwTevProductID.setOnClickListener(this);

        lwTevProductCYRY = findViewById(R.id.lwtv_product_com_cyrys);
        lwTevProductCYRY.setOnClickListener(this);

        lwTevProductYYSR = findViewById(R.id.lwtv_product_com_yysr_in);
        lwTevProductYYSR.setOnClickListener(this);
        lwTevProductYYSRDW = findViewById(R.id.lwtv_product_com_yysr);
        lwTevProductYYSRDW.setOnClickListener(this);

        lwTevProductCYRYS = findViewById(R.id.lwtv_product_com_cyrys_in);
        lwTevProductCYRYS.setOnClickListener(this);
        lwTevProductYYSR = findViewById(R.id.lwtv_product_com_yysr_in);
        lwTevProductYYSR.setOnClickListener(this);
        //经济类型
//        rg_NZ_GY_OR_SY = findViewById(R.id.rg_gv_or_person);//国有或者私营

        rb_GY = findViewById(R.id.rb_gv);//国有button
        rb_GY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(true);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_gv,PRODUCT_COMPANY_SMALL_TYPE_GY);
                }
            }
        });
        rb_SY = findViewById(R.id.rb_person);//私营button
        rb_SY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(true);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_person,PRODUCT_COMPANY_SMALL_TYPE_SY);
                }
            }
        });

//        rg_NZ_JT_OR_YXZRGS = findViewById(R.id.rg_group_or_youxian);//集体有限
        rb_JT = findViewById(R.id.rb_group);//集体
        rb_JT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(true);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_group,PRODUCT_COMPANY_SMALL_TYPE_JT);
                }
            }
        });
        rb_YXZRGS = findViewById(R.id.rb_youxian);//有限责任公司
        rb_YXZRGS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(true);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_youxian,PRODUCT_COMPANY_SMALL_TYPE_YXZRGS);
                }
            }
        });
//        rg_NZ_LY_OR_GFYXGS = findViewById(R.id.rg_lianying_or_gufen);//联营股份
        rb_LY = findViewById(R.id.rb_lianying);
        rb_LY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(true);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_lianying,PRODUCT_COMPANY_SMALL_TYPE_LY);
                }
            }
        });
        rb_GFYXGS = findViewById(R.id.rb_gufen);
        rb_GFYXGS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(true);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_gufen,PRODUCT_COMPANY_SMALL_TYPE_GFYXGS);                }
            }
        });

//        rg_NZ_GFHZ_OR_QTQY = findViewById(R.id.rg_gufen_or_others);//股份其他
        rb_GFHZ = findViewById(R.id.rb_gufenhz);
        rb_GFHZ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(true);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_gufenhz,PRODUCT_COMPANY_SMALL_TYPE_GFHZ);                }
            }
        });
        rb_QTQY = findViewById(R.id.rb_others);
        rb_QTQY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(true);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_others,PRODUCT_COMPANY_SMALL_TYPE_QTQY);
                }
            }
        });

//        rg_GAT_HZJY_OR_HZJY = findViewById(R.id.rg_hejy_or_hzjy);//合资合作

        rb_HZJY = findViewById(R.id.rb_hzjy);
        rb_HZJY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(true);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_hzjy,PRODUCT_COMPANY_SMALL_TYPE_HZJY);                }
            }
        });
        rb_HZOJY = findViewById(R.id.rb_h_zuo_jy);
        rb_HZOJY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(true);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_h_zuo_jy,PRODUCT_COMPANY_SMALL_TYPE_HZOJY);

                }
            }
        });
//        rg_GAT_GATDZJY_OR_GATTZGFYXGS = findViewById(R.id.rg_gatdzjy_or_gattzgfyxgs);//港澳台独资、投资

        rb_GATDZJY = findViewById(R.id.rb_gatdzjy);
        rb_GATDZJY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(true);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_gatdzjy,PRODUCT_COMPANY_SMALL_TYPE_DZJY);
                }
            }
        });
        rb_GATTZGFYXGS = findViewById(R.id.rb_gattzgfyxgs);
        rb_GATTZGFYXGS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(true);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_gattzgfyxgs,PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS);
                }
            }
        });


//        rg_WZ_ZWHZ_OR_ZWHZ = findViewById(R.id.rg_zwhz_or_zwhz);//中外合资、合作

        rb_ZWHZ = findViewById(R.id.rb_zwhz);
        rb_ZWHZ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(true);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_zwhz,PRODUCT_COMPANY_SMALL_TYPE_ZWHZ);
                }
            }
        });
        rb_ZWHZO = findViewById(R.id.rb_zwhzhuo);
        rb_ZWHZO.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(true);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_zwhzhuo,PRODUCT_COMPANY_SMALL_TYPE_ZWHZO);
                }
            }
        });

//        rg_WZ_WZQY_OR_WSTZGFYXGS = findViewById(R.id.rg_wzqy_or_wztsgfyxgs);

        rb_WZQY = findViewById(R.id.rb_wzqy);
        rb_WZQY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(true);
                    rb_WSTZGFYXGS.setChecked(false);
                    upDataBase(R.id.rb_zwhzhuo,PRODUCT_COMPANY_SMALL_TYPE_WZQY);                }
            }
        });

        rb_WSTZGFYXGS = findViewById(R.id.rb_wstzgfyxgs);
        rb_WSTZGFYXGS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_GY.setChecked(false);
                    rb_SY.setChecked(false);
                    rb_JT.setChecked(false);
                    rb_YXZRGS.setChecked(false);
                    rb_LY.setChecked(false);
                    rb_GFYXGS.setChecked(false);
                    rb_GFHZ.setChecked(false);
                    rb_QTQY.setChecked(false);
                    rb_HZJY.setChecked(false);
                    rb_HZOJY.setChecked(false);
                    rb_GATDZJY.setChecked(false);
                    rb_GATTZGFYXGS.setChecked(false);
                    rb_ZWHZ.setChecked(false);
                    rb_ZWHZO.setChecked(false);
                    rb_WZQY.setChecked(false);
                    rb_WSTZGFYXGS.setChecked(true);
                    upDataBase(R.id.rb_zwhzhuo,PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS);                }
            }
        });



        rbGYCPSCXKZ = findViewById(R.id.rb_gycpscxkz);
        rbSC = findViewById(R.id.rb_product_info_sc);
        rbCCC = findViewById(R.id.rb_product_info_ccc);
        rbOthers = findViewById(R.id.rb_product_info_others);
        rbGYCPSCXKZ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbSC.setChecked(false);
                    rbCCC.setChecked(false);
                    rbOthers.setChecked(false);
                    upDataBase(R.id.rb_gycpscxkz,LINSENCE_TYPE_GYCPSCXKZ);
                }else{
                    upDataBase(R.id.rb_gycpscxkz,"-1");
                }
            }
        });

        rbSC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbGYCPSCXKZ.setChecked(false);
                    rbCCC.setChecked(false);
                    rbOthers.setChecked(false);
                    upDataBase(R.id.rb_product_info_sc,LINSENCE_TYPE_SC);
                }else{
                    upDataBase(R.id.rb_product_info_sc,"-1");
                }
            }
        });

        rbCCC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbGYCPSCXKZ.setChecked(false);
                    rbSC.setChecked(false);
                    rbOthers.setChecked(false);
                    upDataBase(R.id.rb_product_info_ccc,LINSENCE_TYPE_CCC);
                }else{
                    upDataBase(R.id.rb_product_info_ccc,"-1");
                }
            }
        });

        rbOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbGYCPSCXKZ.setChecked(false);
                    rbSC.setChecked(false);
                    rbCCC.setChecked(false);
                    upDataBase(R.id.rb_product_info_others,LINSENCE_TYPE_OTHERS);
                }else{
                    upDataBase(R.id.rb_product_info_others,"-1");
                }
            }
        });





        lwTvAcceptProduceInfoZSID = findViewById(R.id.lwtv_accept_produce_info_zs_id_in);
        lwTvAcceptProduceInfoZSID.setOnClickListener(this);
        lwTvAcceptProduceCPMC = findViewById(R.id.lwtv_accept_produce_info_cpmc_in);
        lwTvAcceptProduceCPMCJB = findViewById(R.id.lwtv_accept_produce_info_cpmc);
        lwTvAcceptProduceCPMCJB.setOnClickListener(this);
        lwTvAcceptProduceCPMC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //输入时的调用
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mHandler.removeCallbacks(mRunnableCPMC);
                //800毫秒没有输入认为输入完毕
                mHandler.postDelayed(mRunnableCPMC, 800);
            }



            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
//        lwTvAcceptProduceCPMC.setOnClickListener(this);
        lwTvAcceptProduceGGXH = findViewById(R.id.lwtv_accept_produce_info_ggxh_in);
        lwTvAcceptProduceGGXHJB = findViewById(R.id.lwtv_accept_produce_info_ggxh);
        lwTvAcceptProduceGGXHJB.setOnClickListener(this);
        lwTvAcceptProduceGGXH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //输入时的调用
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mHandler.removeCallbacks(mRunnableGC);
                //800毫秒没有输入认为输入完毕
                mHandler.postDelayed(mRunnableGC, 800);
            }



            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        lwTvAcceptProduceSCRI = findViewById(R.id.lwtv_accept_produce_info_scrqph_in);
        lwTvAcceptProduceSCRI.setOnClickListener(this);
        lwTvAcceptProduceSB = findViewById(R.id.lwtv_accept_produce_info_sb_in);
        lwTvAcceptProduceSB.setOnClickListener(this);
        lwTvAcceptProduceCYSL = findViewById(R.id.lwtv_accept_produce_info_cysl_in);
        lwTvAcceptProduceCYSL.setOnClickListener(this);
        lwTvAcceptProduceCYSLDW = findViewById(R.id.lwtv_accept_produce_info_cysl);
        lwTvAcceptProduceCYSLDW.setOnClickListener(this);
        lwTvAcceptProduceCPDJ = findViewById(R.id.lwtv_accept_produce_info_cpdj_in);
        lwTvAcceptProduceCPDJ.setOnClickListener(this);

        lwTvAcceptProduceBYLJFCDD = findViewById(R.id.lwtv_accept_produce_info_byljfcdd_in);
        lwTvAcceptProduceBYLJFCDD.setOnClickListener(this);


        lwTvAcceptProduceBZZXBZ = findViewById(R.id.lwtv_accept_produce_info_bjzxbzjswj_in);
        lwTvAcceptProduceBZZXBZ.setOnClickListener(this);
        lwTvAcceptProduceDJDW = findViewById(R.id.lwtv_accept_produce_info_dj);
        lwTvAcceptProduceDJDW.setOnClickListener(this);

        lwTvAcceptProduceCYRQ = findViewById(R.id.lwtv_accept_produce_info_cyrq_in);
        lwTvAcceptProduceCYRQ.setOnClickListener(this);
        lwTvAcceptProduceFYZT = findViewById(R.id.lwtv_accept_produce_info_fyzt_in);
        lwTvAcceptProduceFYZT.setOnClickListener(this);


        lwTvAcceptProduceJSYDD = findViewById(R.id.lwtv_accept_produce_info_jsydd_in);
        lwTvAcceptProduceJSYDD.setOnClickListener(this);
        lwTvAcceptProduceJSYJZRQ = findViewById(R.id.lwtv_accept_produce_info_jsyjzrqt_in);
        lwTvAcceptProduceJSYJZRQ.setOnClickListener(this);

        lwTvAcceptProducePL = findViewById(R.id.lwtv_accept_produce_info_pl_in);
        lwTvAcceptProducePL.setOnClickListener(this);


//        lwTvAcceptProducePLDW = findViewById(R.id.lwtv_accept_produce_info_pl);
//        lwTvAcceptProducePLDW.setOnClickListener(this);
//        checkBoxPL = findViewById(R.id.cb_pl);
//        checkBoxJS = findViewById(R.id.cb_js);
//
//        checkBoxPL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    checkBoxJS.setChecked(false);
//                    upDataBase(R.id.cb_pl,ACCEPT_PL);
//                }
//            }
//        });
//
//        checkBoxJS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    checkBoxPL.setChecked(false);
//                    upDataBase(R.id.cb_js,ACCEPT_JS);
//                }
//            }
//        });

        lwTvAcceptProduceDJ = findViewById(R.id.lwtv_accept_produce_info_dj_in);
        lwTvAcceptProduceDJ.setOnClickListener(this);
        lwTvSampleCompanyName = findViewById(R.id.lwtv_sample_company_info_name_in);
        lwTvSampleCompanyName.setOnClickListener(this);
        lwTvSampleCompanyAddress = findViewById(R.id.lwtv_sample_company_info_address_in);
        lwTvSampleCompanyAddress.setOnClickListener(this);
        lwTvSampleCompanyEMSID = findViewById(R.id.lwtv_sample_company_info_emss_in);
        lwTvSampleCompanyEMSID.setOnClickListener(this);
        lwTvSampleCompanyConnectName = findViewById(R.id.lwtv_sample_company_info_lxr_in);
        lwTvSampleCompanyConnectName.setOnClickListener(this);
        lwTvSampleCompanyConnectTel = findViewById(R.id.lwtv_sample_company_info_lx_tel_in);
        lwTvSampleCompanyConnectTel.setOnClickListener(this);
        lwTvSampleCompanyConnectEmail = findViewById(R.id.lwtv_sample_company_info_cz_email_in);
        lwTvSampleCompanyConnectEmail.setOnClickListener(this);

        lwTvBz = findViewById(R.id.lwtv_bz_in);
        lwTvBzJB = findViewById(R.id.lwtv_bz);
        lwTvBzJB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initJB(lwTvBz);
            }
        });


        lwTvBz.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //输入时的调用
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mHandler.removeCallbacks(mRunnable);
                //800毫秒没有输入认为输入完毕
                mHandler.postDelayed(mRunnable, 800);
            }



            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bzXHBAH = findViewById(R.id.lwtv_bz_xmbah_in);
        bzXHBAH.setOnClickListener(this);
        bzGYF = findViewById(R.id.lwtv_bz_gyf_in);
        bzGYF.setOnClickListener(this);
        bzGYFDW = findViewById(R.id.lwtv_bz_gyf);
        bzGYFDW.setOnClickListener(this);
        ivSJDW = findViewById(R.id.iv_sjdw_qz);
        ivSJDW.setOnClickListener(this);
        ivSCDW = findViewById(R.id.iv_scdw_qz);
        ivSCDW.setOnClickListener(this);
        llCYR = findViewById(R.id.ll_cyr_qz);
        llCYR.setOnClickListener(this);

        cyrImg_1 = findViewById(R.id.iv_cyr_qz_1);
        cyrImg_2 = findViewById(R.id.iv_cyr_qz_2);
        lwTvSJDW_NYR = findViewById(R.id.lwtv_sjdw_nyr);
        lwTvSJDW_NYR.setOnClickListener(this);
        lwTvSCDW_NYR = findViewById(R.id.lwtv_scdw_nyr);
        lwTvSCDW_NYR.setOnClickListener(this);
        lwTvCYR_NYR = findViewById(R.id.lwtv_cyr_nyr);
        lwTvCYR_NYR.setOnClickListener(this);
        btnMore = findViewById(R.id.btn_more);
        btnMore.setOnClickListener(this);
        llBack = findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        ivQIANZHANG = findViewById(R.id.yz);
    }
    private void initData(){
        verifyStoragePermissions(CheckOrRecheckFormActivity.this);
        if(AccountManager.getInstance().getIsOpenCode().equals(AccountManager.OPEN_CODE_YES)){
        ivQIANZHANG.setVisibility(View.GONE);
        }
        checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId(),mHandler);


//    //加载数据   先从本地加载数据---------->如果取不到就去服务端取(并更新到本地数据库)---------->服务端也没有就认为是空模版
//    CheckForm checkForm = checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId());
//        if(checkForm != null){
//        setData(checkForm);
//    }else{
//        checkOrRecheckFormController.requestTasksamples(AccountManager.getInstance().getUserId(),checkOrRecheckFormController.getTaskId());
//    }

}


    /**
     * 通过数据库加载数据
     * @param checkForm
     */
    private void setData(CheckForm checkForm){
        lwTvCheckID.setText(checkForm.getTaskCode());
        lwTvReportID.setText(checkNullString(checkForm.getReportId()));
        if(!TextUtils.isEmpty(checkForm.getReportId())){
            lwTvReportID.setText(checkForm.getReportId());
        }
        lwTvTaskFrom.setText(checkNullString(checkForm.getTaskFrom()));//任务来源
        lwTvTaskClass.setText(checkNullString(checkForm.getTaskClass()));//任务类别
        lwTvAcceptComName.setText(checkNullString(checkForm.getAcceptCompanyName()));//受检单位名称和地址
        lwTvAcceptComAddres.setText(checkNullString(checkForm.getAcceptCompanyAddress()));
        lwTvConnectName.setText(checkNullString(checkForm.getAcceptCompanyConnectNanme()));//受检单位联系人及电话
        lwTvConnectTel.setText(checkNullString(checkForm.getAcceptCompanyConnecTel()));
        lwTvFaRen.setText(checkNullString(checkForm.getAcceptCompanyFaren()));//受检单位法人
        lwTvProductName.setText(checkNullString(checkForm.getProductCompanyName()));//生产单位名称
        lwTvProductAddress.setText(checkNullString(checkForm.getProductCompanyAdress()));//生产单位地址
        lwTvProductEMSID.setText(checkNullString(checkForm.getProductCompanyEMS()));//生产单位邮政编码
        lwTvProdictFaRen.setText(checkNullString(checkForm.getProductCompanyFaren()));//生产单位法人
        lwTvProductTelName.setText(checkNullString(checkForm.getProductCompanyConnectName()));//生产单位联系人
        lwTvProductTleNum.setText(checkNullString(checkForm.getProductCompanyConnectTEL()));//生产单位联系人电话
        lwTvProductBusinessLicense.setText(checkNullString(checkForm.getProductCompanyLicense()));//生产单位营业执照
        lwTevProductID.setText(checkNullString(checkForm.getProductCompanyID()));//生产单位机构代码
        if(!TextUtils.isEmpty(checkForm.getAcceptProduceLicenseType())){
            switch (checkForm.getAcceptProduceLicenseType()){
                case LINSENCE_TYPE_GYCPSCXKZ:
                    rbGYCPSCXKZ.setChecked(true);
                    break;
                case LINSENCE_TYPE_SC:
                    rbSC.setChecked(true);
                    break;
                case LINSENCE_TYPE_CCC:
                    rbCCC.setChecked(true);
                    break;
                case LINSENCE_TYPE_OTHERS:
                    rbOthers.setChecked(true);
                    break;
                case LINSENCE_TYPE_NONE:
                    rbGYCPSCXKZ.setChecked(false);
                    rbSC.setChecked(false);
                    rbCCC.setChecked(false);
                    rbOthers.setChecked(false);
                    break;
            }
        }
            String ecType = checkForm.getProductCompanySmallType();
            if(!TextUtils.isEmpty(ecType)){
                switch (ecType){
                    case PRODUCT_COMPANY_SMALL_TYPE_GY :
                        rb_GY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_SY :
                        rb_SY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_JT :
                        rb_JT.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_YXZRGS :
                        rb_YXZRGS.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_LY:
                        rb_LY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_GFYXGS :
                        rb_GFYXGS.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_GFHZ :
                        rb_GFHZ.setChecked(true);
                        break;
                    case  PRODUCT_COMPANY_SMALL_TYPE_QTQY :
                        rb_QTQY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_HZJY :
                        rb_HZJY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_HZOJY :
                        rb_HZOJY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_DZJY :
                        rb_GATDZJY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS :
                        rb_GATTZGFYXGS.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_ZWHZ :
                        rb_ZWHZ.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_ZWHZO :
                        rb_ZWHZO.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_WZQY :
                        rb_WZQY.setChecked(true);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS :
                        rb_WSTZGFYXGS.setChecked(true);
                        break;
                }
            }

//            String acceptProType = checkForm.getAcceptProType();
//            if(!TextUtils.isEmpty(acceptProType)){
//                switch (acceptProType){
//                    case ACCEPT_JS:
//                        checkBoxJS.setChecked(true);
//                        checkBoxPL.setChecked(false);
//                        break;
//                    case ACCEPT_PL:
//                        checkBoxJS.setChecked(false);
//                        checkBoxPL.setChecked(true);
//                        break;
//                }
//            }

            lwTvAcceptProduceInfoZSID.setText(checkNullString(checkForm.getAcceptProduceLicenseId()));//证书编号
            lwTvAcceptProduceCPMC.setText(checkNullString(checkForm.getAcceptProduceCPMC()));//产品名称
            lwTvAcceptProduceGGXH.setText(checkNullString(checkForm.getAcceptProduceGGCH()));//规格型号
            lwTvAcceptProduceSCRI.setText(checkNullString(checkForm.getAcceptProduceSCRQ()));//生产日期/批号
            lwTvAcceptProduceCPDJ.setText(checkNullString(checkForm.getAcceptProduceCPDJ()));//产品等级
            lwTvAcceptProduceSB.setText(checkNullString(checkForm.getAcceptProduceSB()));//商标

            lwTvAcceptProduceCYSL.setText(checkNullString(checkForm.getAcceptProduceCYSL()));//抽样数量
            lwTvAcceptProduceBZZXBZ.setText(checkNullString(checkForm.getAcceptProduceBZZXBZ()));//标注执行标准，技术文件
            lwTvAcceptProduceCYRQ.setText(checkNullString(checkForm.getAcceptProduceCYRQ()));
            lwTvAcceptProduceFYZT.setText(checkNullString(checkForm.getAcceptProduceFYZT()));//封样状态

            lwTvAcceptProduceJSYDD.setText(checkNullString(checkForm.getAcceptProduceJSYDD()));//寄送地址

            lwTvAcceptProducePL.setText(checkNullString(checkForm.getAcceptProducePL()));
            lwTvAcceptProduceDJ.setText(checkNullString(checkForm.getAcceptProduceDJ()));
            lwTvAcceptProduceBYLJFCDD.setText(checkNullString(checkForm.getAcceptProduceBYLFCDD()));
            bzGYF.setText(checkNullString(checkForm.getBzGYF()));
            bzXHBAH.setText(checkNullString(checkForm.getBzXMBAH()));
            lwTvAcceptProduceJSYJZRQ.setText(checkNullString(checkForm.getAcceptProduceJSYJZRQ()));
            lwTvSampleCompanyName.setText(checkNullString(checkForm.getCheckCompanyName()));
            lwTvSampleCompanyAddress.setText(checkNullString(checkForm.getCheckCompanyAddress()));
            lwTvSampleCompanyEMSID.setText(checkNullString(checkForm.getCheckCompanyEMS()));
            lwTvSampleCompanyConnectName.setText(checkNullString(checkForm.getCheckCompanyConnectName()));
            lwTvSampleCompanyConnectTel.setText(checkNullString(checkForm.getCheckCompanyConnectTel()));
            lwTvSampleCompanyConnectEmail.setText(checkNullString(checkForm.getCheckCompanyEmail()));
            lwTevProductCYRYS.setText(checkNullString(checkForm.getProductCompanyDimensionsCYRYS()));
            lwTevProductYYSR.setText(checkNullString(checkForm.getProductCompanyDimensionsYYSR()));
            //备注
            lwTvBz.setText(checkForm.getBzInfo());
            //抽样单位日期
            String cyDwRQ = checkForm.getCyDwRQ();
            if(!TextUtils.isEmpty(cyDwRQ)){
                lwTvCYR_NYR.setText(cyDwRQ);
                //将抽样单位日期赋值
                SimpleDateFormat format = new SimpleDateFormat("yyyy 年MM 月dd 日");
                try {
                    Date cydwDate = format.parse(cyDwRQ);
                    Calendar cld = Calendar.getInstance();
                    cld.setTime(cydwDate);
                    CYR_DATE_YEAR = String.valueOf(cld.get(Calendar.YEAR));
                    CYR_DATE_MONTH = String.valueOf(cld.get(Calendar.MONTH)+1);
                    CYR_DATE_DAY = String.valueOf(cld.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //抽样日期
            String cyRQ = checkForm.getAcceptProduceCYRQ();
            if(!TextUtils.isEmpty(cyRQ)){
                lwTvAcceptProduceCYRQ.setText(cyRQ);

             }
            //受检单位日期
            String sjDwRQ = checkForm.getSjDwRQ();
            if(!TextUtils.isEmpty(sjDwRQ)){
                lwTvSJDW_NYR.setText(sjDwRQ);
                //将生产单位单位日期赋值
                SimpleDateFormat format = new SimpleDateFormat("yyyy 年MM 月dd 日");
                try {
                    Date sjdwDate = format.parse(sjDwRQ);
                    Calendar cld = Calendar.getInstance();
                    cld.setTime(sjdwDate);
                    SJDW_DATE_YEAR = String.valueOf(cld.get(Calendar.YEAR));
                    SJDW_DATE_MONTH = String.valueOf(cld.get(Calendar.MONTH)+1);
                    SJDW_DATE_DAY = String.valueOf(cld.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

             //生产单位日期
            String scDwRQ = checkForm.getScDwRQ();
            if(!TextUtils.isEmpty(scDwRQ)){
                lwTvSCDW_NYR.setText(scDwRQ);
                //将生产单位单位日期赋值
                SimpleDateFormat format = new SimpleDateFormat("yyyy 年MM 月dd 日");
                try {
                    Date scdwDate = format.parse(scDwRQ);
                    Calendar cld = Calendar.getInstance();
                    cld.setTime(scdwDate);
                    SCDW_DATE_YEAR = String.valueOf(cld.get(Calendar.YEAR));
                    SCDW_DATE_MONTH = String.valueOf(cld.get(Calendar.MONTH)+1);
                    SCDW_DATE_DAY = String.valueOf(cld.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            String signAPath = checkForm.getAcceptCompanySignImagePath();
            if(!TextUtils.isEmpty(signAPath)){
                GlideManager.getInstance().ImageLoad(this,signAPath,ivSJDW,true);
            }
        String signPPath = checkForm.getProductCompanySignImagePath();
        if(!TextUtils.isEmpty(signPPath)){
            GlideManager.getInstance().ImageLoad(this,signPPath,ivSCDW,true);
        }


        String signCPath_1 = checkForm.getCheckPeopleSignImagePath_1();
        if(!TextUtils.isEmpty(signCPath_1)){
            GlideManager.getInstance().ImageLoad(this,signCPath_1,cyrImg_1,true);

        }

        String signCPath_2 = checkForm.getCheckPeopleSignImagePath_2();
        if(!TextUtils.isEmpty(signCPath_2)){
            GlideManager.getInstance().ImageLoad(this,signCPath_2,cyrImg_2,true);

        }
    }

    public void setupUIData(){
        String taskSampleId = checkOrRecheckFormController.getTaskSampleId();
        Sampling sampling = checkOrRecheckFormController.getSampling(checkOrRecheckFormController.getTaskSampleId());
        CheckForm checkForm = new CheckForm();
        if(!TextUtils.isEmpty(taskSampleId) && sampling != null){//如果有数据
            //本地没有数据，将服务端的数据更新到本地数据库
            lwTvCheckID.setText(checkNullString(sampling.getTaskcode()));//编号
            checkForm.setId(taskSampleId);
            checkForm.setTaskCode(sampling.getTaskcode());
            if(!TextUtils.isEmpty(sampling.getReportcode())){
                lwTvReportID.setText(sampling.getReportcode());
            }

            checkForm.setReportId(sampling.getReportcode());
            lwTvTaskFrom.setText(checkNullString(sampling.getTaskfrommean()));//任务来源
            checkForm.setTaskFrom(sampling.getTaskfrommean());
            checkForm.setTaskFromId(sampling.getTaskfrom());
            lwTvTaskClass.setText(checkNullString(sampling.getTasktypemean()));//任务类别
            checkForm.setTaskClass(sampling.getTasktypemean());
            checkForm.setTaskClassId(sampling.getTaskfrom());
            lwTvAcceptComName.setText(checkNullString(sampling.getInspectedname()));//受检单位名称和地址
            checkForm.setAcceptCompanyName(checkNullString(sampling.getInspectedname()));
            lwTvAcceptComAddres.setText(checkNullString(sampling.getInspectedaddress()));
            checkForm.setAcceptCompanyAddress(checkNullString(sampling.getInspectedaddress()));
            lwTvConnectName.setText(checkNullString(sampling.getInspectedman()));//受检单位联系人及电话
            checkForm.setAcceptCompanyConnectNanme(checkNullString(sampling.getInspectedman()));
            lwTvConnectTel.setText(checkNullString(sampling.getInspectedtel()));
            checkForm.setAcceptCompanyConnecTel(checkNullString(sampling.getInspectedtel()));
            lwTvFaRen.setText(checkNullString(sampling.getRepresentative()));//受检单位法人
            checkForm.setAcceptCompanyFaren(sampling.getRepresentative());
            lwTvProductName.setText(checkNullString(sampling.getProducename()));//生产单位名称
            checkForm.setProductCompanyName(sampling.getProducename());
            lwTvProductAddress.setText(checkNullString(sampling.getProduceaddress()));//生产单位地址
            checkForm.setProductCompanyAdress(sampling.getProduceaddress());
            lwTvProductEMSID.setText(checkNullString(sampling.getProducezipcode()));//生产单位邮政编码
            checkForm.setProductCompanyEMS(sampling.getProducezipcode());
            lwTvProdictFaRen.setText(checkNullString(sampling.getPrepresentative()));//生产单位法人
            checkForm.setProductCompanyFaren(sampling.getPrepresentative());
            lwTvProductTelName.setText(checkNullString(sampling.getProduceconman()));//生产单位联系人
            checkForm.setProductCompanyConnectName(sampling.getProduceconman());
            lwTvProductTleNum.setText(checkNullString(sampling.getProducetel()));//生产单位联系人电话
            checkForm.setProductCompanyConnectTEL(sampling.getProducetel());
            lwTvProductBusinessLicense.setText(checkNullString(sampling.getProducelicense()));//生产单位营业执照
            checkForm.setProductCompanyLicense(sampling.getProducelicense());
            lwTevProductID.setText(checkNullString(sampling.getProducecode()));//生产单位机构代码
            checkForm.setProductCompanyID(sampling.getProducecode());
            checkForm.setProductCompanyDimensionsCYRYS(checkNullString(sampling.getProducepcount()));
            lwTevProductCYRYS.setText(sampling.getProducepcount());
            checkForm.setProductCompanyDimensionsYYSR(checkNullString(sampling.getProduceoutput()));
            lwTevProductYYSR.setText(sampling.getProduceoutput());

//            lwTvAcceptProducePL.setText(sampling.getProductpl());
//            checkForm.setAcceptProducePL(checkNullString(sampling.getProductpl()));
            //新基数和批量
            lwTvAcceptProducePL.setText(sampling.getSamplingbaseandbatch());
            checkForm.setAcceptProducePL(checkNullString(sampling.getSamplingbaseandbatch()));


            lwTvAcceptProduceDJ.setText(sampling.getUnitprice());
            checkForm.setAcceptProduceDJ(checkNullString(sampling.getUnitprice()));

            bzXHBAH.setText(sampling.getRecordnumber());
            checkForm.setBzXMBAH(checkNullString(sampling.getRecordnumber()));
            bzGYF.setText(sampling.getSamplecost());
            checkForm.setBzGYF(checkNullString(sampling.getSamplecost()));
            String ecType = sampling.getProducesamlltype();
            if(!TextUtils.isEmpty(ecType)){
                switch (ecType){
                    case PRODUCT_COMPANY_SMALL_TYPE_GY :
                        rb_GY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GY);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_SY :
                        rb_SY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_SY);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_JT :
                        rb_JT.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_JT);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_YXZRGS :
                        rb_YXZRGS.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_YXZRGS);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_LY:
                        rb_LY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_LY);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_GFYXGS :
                        rb_GFYXGS.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GFYXGS);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_GFHZ :
                        rb_GFHZ.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GFHZ);
                        break;
                    case  PRODUCT_COMPANY_SMALL_TYPE_QTQY :
                        rb_QTQY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_QTQY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_HZJY :
                        rb_HZJY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_HZJY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_HZOJY :
                        rb_HZOJY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_HZOJY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_DZJY :
                        rb_GATDZJY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_DZJY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS :

                        rb_GATTZGFYXGS.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_ZWHZ :
                        rb_ZWHZ.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_ZWHZ);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_ZWHZO :
                        rb_ZWHZO.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_ZWHZO);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_WZQY :
                        rb_WZQY.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_WZQY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS :
                        rb_WSTZGFYXGS.setChecked(true);
                        checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS);
                        break;
                }
            }


            String type = sampling.getProductnumtype();
            if(!TextUtils.isEmpty(type)){
                switch (type){
                    case LINSENCE_TYPE_GYCPSCXKZ:
                        rbGYCPSCXKZ.setChecked(true);
                        checkForm.setProductCompanyLicense(LINSENCE_TYPE_GYCPSCXKZ);
                        break;
                    case LINSENCE_TYPE_SC:
                        rbSC.setChecked(true);
                        checkForm.setProductCompanyLicense(LINSENCE_TYPE_SC);
                        break;
                    case LINSENCE_TYPE_CCC:
                        rbCCC.setChecked(true);
                        checkForm.setProductCompanyLicense(LINSENCE_TYPE_CCC);
                        break;
                    case LINSENCE_TYPE_OTHERS:
                        rbOthers.setChecked(true);
                        checkForm.setProductCompanyLicense(LINSENCE_TYPE_OTHERS);
                        break;
                    case LINSENCE_TYPE_NONE:
                        rbGYCPSCXKZ.setChecked(false);
                        rbSC.setChecked(false);
                        rbCCC.setChecked(false);
                        rbOthers.setChecked(false);
                        checkForm.setProductCompanyLicense(LINSENCE_TYPE_NONE);
                        break;
                }
            }

//            String plType = sampling.getAcceptProType();
//            if(!TextUtils.isEmpty(plType)){
//                switch (plType){
//                    case ACCEPT_JS:
//                        checkBoxJS.setChecked(true);
//                        checkBoxPL.setChecked(false);
//                        checkForm.setAcceptProType(ACCEPT_JS);
//                        break;
//                    case ACCEPT_PL:
//                        checkBoxJS.setChecked(false);
//                        checkBoxPL.setChecked(true);
//                        checkForm.setAcceptProType(ACCEPT_PL);
//                        break;
//                }
//            }

            checkForm.setAcceptProduceLicenseType(sampling.getProductnumtype());
            lwTvAcceptProduceInfoZSID.setText(checkNullString(sampling.getProductnum()));//证书编号
            checkForm.setAcceptProduceLicenseId(sampling.getProductnum());
            lwTvAcceptProduceCPMC.setText(checkNullString(sampling.getProductname()));//产品名称
            checkForm.setAcceptProduceCPMC(sampling.getProductname());
            lwTvAcceptProduceGGXH.setText(checkNullString(sampling.getProductmodel()));//规格型号
            checkForm.setAcceptProduceGGCH(sampling.getProductmodel());
            lwTvAcceptProduceSCRI.setText(checkNullString(sampling.getProductbnum()));//生产日期/批号
            checkForm.setAcceptProduceSCRQ(sampling.getProductbnum());
            lwTvAcceptProduceCPDJ.setText(checkNullString(sampling.getProductlevel()));//产品等级
            checkForm.setAcceptProduceCPDJ(sampling.getProductlevel());
            lwTvAcceptProduceSB.setText(checkNullString(sampling.getProductmark()));//商标
            checkForm.setAcceptProduceSB(sampling.getProductmark());
            lwTvAcceptProduceCYSL.setText(checkNullString(sampling.getProducepcount()));//抽样数量
            checkForm.setAcceptProduceCYSL(sampling.getProducepcount());

            lwTvAcceptProduceBZZXBZ.setText(checkNullString(sampling.getDostandard()));//标注执行标准，技术文件
            checkForm.setAcceptProduceBZZXBZ(sampling.getDostandard());
            lwTvAcceptProduceCYRQ.setText(checkNullString(sampling.getDotime()));
            checkForm.setAcceptProduceCYRQ(sampling.getDotime());
            lwTvAcceptProduceFYZT.setText(checkNullString(sampling.getEncapsulationstate()));//封样状态
            checkForm.setAcceptProduceFYZT(sampling.getEncapsulationstate());

            lwTvAcceptProduceJSYDD.setText(checkNullString(sampling.getSendaddress()));//寄送地址
            checkForm.setAcceptProduceJSYDD(sampling.getSendaddress());

                lwTvAcceptProduceJSYJZRQ.setText(checkNullString(sampling.getEndtime()));
                checkForm.setAcceptProduceJSYJZRQ(checkNullString(sampling.getEndtime()));
                //抽样单位如果取不到数据就显示默认数据
                if(!TextUtils.isEmpty(sampling.getSammanname())){//服务端存的的是单位的id，界面需要显示id对应的名称
                    lwTvSampleCompanyName.setText(sampling.getSamplingnamemean());
                    checkForm.setCheckCompanyName(sampling.getSamplingnamemean());
                    checkForm.setCheckCompanyNameId(sampling.getSammanname());
                    lwTvSampleCompanyAddress.setText(checkNullString(sampling.getSamplingaddress()));
                    checkForm.setCheckCompanyAddress(sampling.getSamplingaddress());
                    lwTvSampleCompanyEMSID.setText(checkNullString(sampling.getSamplingcode()));
                    checkForm.setCheckCompanyEMS(sampling.getSamplingcode());
                    lwTvSampleCompanyConnectName.setText(checkNullString(sampling.getSamplingman()));
                    checkForm.setCheckCompanyConnectName(sampling.getSamplingman());
                    lwTvSampleCompanyConnectTel.setText(checkNullString(sampling.getSamplingtel()));
                    checkForm.setCheckCompanyConnectTel(sampling.getSamplingtel());
                    lwTvSampleCompanyConnectEmail.setText(checkNullString(sampling.getSamplingemail()));
                    checkForm.setCheckCompanyEmail(sampling.getSamplingemail());

                }


                //备注
                lwTvBz.setText(sampling.getRemark());
                checkForm.setBzInfo(sampling.getRemark());




                //将日期try下
            try {
                String fillInDate = sampling.getFillInDate();
                SimpleDateFormat fillFormat = new SimpleDateFormat("yyyy 年MM 月dd 日",Locale.CHINA);
                Date filldate = fillFormat.parse(fillInDate);
                SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
                fillInDate =  nowFormat.format(filldate);
                if(!TextUtils.isEmpty(fillInDate)){
                    String date[] = fillInDate.split("-");
                    lwTvCYR_NYR.setText(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    lwTvAcceptProduceCYRQ.setText(fillInDate);
                    CYR_DATE_YEAR = date[0];
                    CYR_DATE_MONTH = date[1];
                    CYR_DATE_DAY = date[2];
                    checkForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    checkForm.setAcceptProduceCYRQ(fillInDate);
                }
            } catch (Exception e) {
                SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
                Date nowDate = new Date();
                String fillInDate =  nowFormat.format(nowDate);
                if(!TextUtils.isEmpty(fillInDate)){
                    String date[] = fillInDate.split("-");
                    lwTvCYR_NYR.setText(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    CYR_DATE_YEAR = date[0];
                    CYR_DATE_MONTH = date[1];
                    CYR_DATE_DAY = date[2];
                    lwTvAcceptProduceCYRQ.setText(fillInDate);
                    checkForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    checkForm.setAcceptProduceCYRQ(fillInDate);

                }
            }

        }
        DBManagerFactory.getInstance().getCheckFormManager().saveOrUpdate(checkForm);
//        DbController.getInstance(this).getDaoSession().getCheckFormDao().insertOrReplace(checkForm);
    }

    /**
     * 通过模版更新数据
     * @param sampling
     */
    public void setupUIData(Sampling sampling){
//        CheckForm checkForm = checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId());
        if(checkForm != null){
            String taskSampleId = checkOrRecheckFormController.getTaskSampleId();
            if(!TextUtils.isEmpty(taskSampleId) && sampling != null){//如果有数据
                lwTvCheckID.setText(checkForm.getTaskCode());//编号
                checkForm.setId(taskSampleId);
                checkForm.setTaskCode(checkForm.getTaskCode());
                if(!TextUtils.isEmpty(sampling.getReportcode())){
                    lwTvReportID.setText(sampling.getReportcode());
                }

                checkForm.setReportId(sampling.getReportcode());

                if(!TextUtils.isEmpty(sampling.getTaskfrommean())){
                    lwTvTaskFrom.setText(checkNullString(sampling.getTaskfrommean()));//任务来源
                    checkForm.setTaskFrom(sampling.getTaskfrommean());
                    checkForm.setTaskFromId(sampling.getTaskfrom());
                }

                if(!TextUtils.isEmpty(sampling.getTasktypemean())){
                    lwTvTaskClass.setText(checkNullString(sampling.getTasktypemean()));//任务类别
                    checkForm.setTaskClass(sampling.getTasktypemean());
                    checkForm.setTaskClassId(sampling.getTaskfrom());
                }

                if(!TextUtils.isEmpty(sampling.getInspectedname())){
                    lwTvAcceptComName.setText(checkNullString(sampling.getInspectedname()));//受检单位名称和地址
                    checkForm.setAcceptCompanyName(checkNullString(sampling.getInspectedname()));
                }

                if(!TextUtils.isEmpty(sampling.getInspectedaddress())){
                    lwTvAcceptComAddres.setText(checkNullString(sampling.getInspectedaddress()));
                    checkForm.setAcceptCompanyAddress(checkNullString(sampling.getInspectedaddress()));
                }

                if(!TextUtils.isEmpty(sampling.getInspectedman())){
                    lwTvConnectName.setText(checkNullString(sampling.getInspectedman()));//受检单位联系人及电话
                    checkForm.setAcceptCompanyConnectNanme(checkNullString(sampling.getInspectedman()));
                }

                if(!TextUtils.isEmpty(sampling.getInspectedtel())){
                    lwTvConnectTel.setText(checkNullString(sampling.getInspectedtel()));
                    checkForm.setAcceptCompanyConnecTel(checkNullString(sampling.getInspectedtel()));
                }

                if(!TextUtils.isEmpty(sampling.getRepresentative())){
                    lwTvFaRen.setText(checkNullString(sampling.getRepresentative()));//受检单位法人
                    checkForm.setAcceptCompanyFaren(sampling.getRepresentative());
                }

                if(!TextUtils.isEmpty(sampling.getProducename())){
                    lwTvProductName.setText(checkNullString(sampling.getProducename()));//生产单位名称
                    checkForm.setProductCompanyName(sampling.getProducename());
                }

                if(!TextUtils.isEmpty(sampling.getProduceaddress())){
                    lwTvProductAddress.setText(checkNullString(sampling.getProduceaddress()));//生产单位地址
                    checkForm.setProductCompanyAdress(sampling.getProduceaddress());
                }

                if(!TextUtils.isEmpty(sampling.getProducezipcode())){
                    lwTvProductEMSID.setText(checkNullString(sampling.getProducezipcode()));//生产单位邮政编码
                    checkForm.setProductCompanyEMS(sampling.getProducezipcode());
                }

                if(!TextUtils.isEmpty(sampling.getPrepresentative())){
                    lwTvProdictFaRen.setText(checkNullString(sampling.getPrepresentative()));//生产单位法人
                    checkForm.setProductCompanyFaren(sampling.getPrepresentative());
                }

                if(!TextUtils.isEmpty(sampling.getProduceconman())){
                    lwTvProductTelName.setText(checkNullString(sampling.getProduceconman()));//生产单位联系人
                    checkForm.setProductCompanyConnectName(sampling.getProduceconman());
                }

                if(!TextUtils.isEmpty(sampling.getProducetel())){
                    lwTvProductTleNum.setText(checkNullString(sampling.getProducetel()));//生产单位联系人电话
                    checkForm.setProductCompanyConnectTEL(sampling.getProducetel());
                }

                if(!TextUtils.isEmpty(sampling.getProducelicense())){
                    lwTvProductBusinessLicense.setText(checkNullString(sampling.getProducelicense()));//生产单位营业执照
                    checkForm.setProductCompanyLicense(sampling.getProducelicense());
                }

                if(!TextUtils.isEmpty(sampling.getProducecode())){
                    lwTevProductID.setText(checkNullString(sampling.getProducecode()));//生产单位机构代码
                    checkForm.setProductCompanyID(sampling.getProducecode());
                }

                if(!TextUtils.isEmpty(sampling.getProducepcount())){
                    lwTevProductCYRYS.setText(checkNullString(sampling.getProducepcount()));
                    checkForm.setProductCompanyDimensionsCYRYS(checkNullString(sampling.getProducepcount()));
                }

                if(!TextUtils.isEmpty(sampling.getProduceoutput())){
                    lwTevProductYYSR.setText(checkNullString(sampling.getProduceoutput()));
                    checkForm.setProductCompanyDimensionsYYSR(checkNullString(sampling.getProduceoutput()));
                }

//                if(!TextUtils.isEmpty(sampling.getProductpl())){
//                    lwTvAcceptProducePL.setText(checkNullString(sampling.getProductpl()));
//                    checkForm.setAcceptProducePL(checkNullString(sampling.getProductpl()));
//                }

                if(!TextUtils.isEmpty(sampling.getSamplingbaseandbatch())){
                    lwTvAcceptProducePL.setText(checkNullString(sampling.getSamplingbaseandbatch()));
                    checkForm.setAcceptProducePL(checkNullString(sampling.getSamplingbaseandbatch()));
                }



                if(!TextUtils.isEmpty(sampling.getUnitprice())){
                    lwTvAcceptProduceDJ.setText(checkNullString(sampling.getUnitprice()));
                    checkForm.setAcceptProduceDJ(checkNullString(sampling.getUnitprice()));
                }

                if(!TextUtils.isEmpty(sampling.getRecordnumber())){
                    bzXHBAH.setText(checkNullString(sampling.getRecordnumber()));
                    checkForm.setBzXMBAH(checkNullString(sampling.getRecordnumber()));
                }

                if(!TextUtils.isEmpty(sampling.getSamplecost())){
                    bzGYF.setText(checkNullString(sampling.getSamplecost()));
                    checkForm.setBzGYF(checkNullString(sampling.getSamplecost()));
                }


                String ecType = sampling.getProducesamlltype();
                if(!TextUtils.isEmpty(ecType)){
                    switch (ecType){
                        case PRODUCT_COMPANY_SMALL_TYPE_GY :
                            rb_GY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GY);
                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_SY :
                            rb_SY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_SY);
                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_JT :
                            rb_JT.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_JT);
                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_YXZRGS :
                            rb_YXZRGS.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_YXZRGS);
                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_LY:
                            rb_LY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_LY);
                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_GFYXGS :
                            rb_GFYXGS.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GFYXGS);
                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_GFHZ :
                            rb_GFHZ.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GFHZ);
                            break;
                        case  PRODUCT_COMPANY_SMALL_TYPE_QTQY :
                            rb_QTQY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_QTQY);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_HZJY :
                            rb_HZJY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_HZJY);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_HZOJY :
                            rb_HZOJY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_HZOJY);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_DZJY :
                            rb_GATDZJY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_DZJY);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS :

                            rb_GATTZGFYXGS.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_ZWHZ :
                            rb_ZWHZ.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_ZWHZ);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_ZWHZO :
                            rb_ZWHZO.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_ZWHZO);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_WZQY :
                            rb_WZQY.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_WZQY);

                            break;
                        case PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS :
                            rb_WSTZGFYXGS.setChecked(true);
                            checkForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS);
                            break;
                    }
                }


                String type = sampling.getProductnumtype();
                if(!TextUtils.isEmpty(type)){
                    switch (type){
                        case LINSENCE_TYPE_GYCPSCXKZ:
                            rbGYCPSCXKZ.setChecked(true);
                            checkForm.setProductCompanyLicense(LINSENCE_TYPE_GYCPSCXKZ);
                            break;
                        case LINSENCE_TYPE_SC:
                            rbSC.setChecked(true);
                            checkForm.setProductCompanyLicense(LINSENCE_TYPE_SC);
                            break;
                        case LINSENCE_TYPE_CCC:
                            rbCCC.setChecked(true);
                            checkForm.setProductCompanyLicense(LINSENCE_TYPE_CCC);
                            break;
                        case LINSENCE_TYPE_OTHERS:
                            rbOthers.setChecked(true);
                            checkForm.setProductCompanyLicense(LINSENCE_TYPE_OTHERS);
                            break;
                        case LINSENCE_TYPE_NONE:
                            rbGYCPSCXKZ.setChecked(false);
                            rbSC.setChecked(false);
                            rbCCC.setChecked(false);
                            rbOthers.setChecked(false);
                            checkForm.setProductCompanyLicense(LINSENCE_TYPE_NONE);
                            break;
                    }
                }
//                checkForm.setAcceptProduceLicenseType(sampling.getProductnumtype());
                lwTvAcceptProduceInfoZSID.setText(checkNullString(sampling.getProductnum()));//证书编号
                checkForm.setAcceptProduceLicenseId(sampling.getProductnum());
//                if(!TextUtils.isEmpty(sampling.getProductnumtype())){
//
//                }

                if(!TextUtils.isEmpty(sampling.getProductname())){
                    lwTvAcceptProduceCPMC.setText(checkNullString(sampling.getProductname()));//产品名称
                    checkForm.setAcceptProduceCPMC(sampling.getProductname());
                }

                if(!TextUtils.isEmpty(sampling.getProductmodel())){
                    lwTvAcceptProduceGGXH.setText(checkNullString(sampling.getProductmodel()));//规格型号
                    checkForm.setAcceptProduceGGCH(sampling.getProductmodel());
                }

                if(!TextUtils.isEmpty(sampling.getProductbnum())){
                    lwTvAcceptProduceSCRI.setText(checkNullString(sampling.getProductbnum()));//生产日期/批号
                    checkForm.setAcceptProduceSCRQ(sampling.getProductbnum());
                }

                if(!TextUtils.isEmpty(sampling.getProductlevel())){
                    lwTvAcceptProduceCPDJ.setText(checkNullString(sampling.getProductlevel()));//产品等级
                    checkForm.setAcceptProduceCPDJ(sampling.getProductlevel());
                }

                if(!TextUtils.isEmpty(sampling.getProductmark())){
                    lwTvAcceptProduceSB.setText(checkNullString(sampling.getProductmark()));//商标
                    checkForm.setAcceptProduceSB(sampling.getProductmark());
                }

                if(!TextUtils.isEmpty(sampling.getSamplingcount())){
                    lwTvAcceptProduceCYSL.setText(checkNullString(sampling.getSamplingcount()));//抽样数量
                    checkForm.setAcceptProduceCYSL(sampling.getSamplingcount());
                }


                if(!TextUtils.isEmpty(sampling.getDostandard())){
                    lwTvAcceptProduceBZZXBZ.setText(checkNullString(sampling.getDostandard()));//标注执行标准，技术文件
                    checkForm.setAcceptProduceBZZXBZ(sampling.getDostandard());
                }

                if(!TextUtils.isEmpty(sampling.getDotime())){
                    lwTvAcceptProduceCYRQ.setText(checkNullString(sampling.getDotime()));
                    checkForm.setAcceptProduceCYRQ(sampling.getDotime());
                }

                if(!TextUtils.isEmpty(sampling.getEncapsulationstate())){
                    lwTvAcceptProduceFYZT.setText(checkNullString(sampling.getEncapsulationstate()));//封样状态
                    checkForm.setAcceptProduceFYZT(sampling.getEncapsulationstate());
                }

                if(!TextUtils.isEmpty(sampling.getRvandfc())){
                    lwTvAcceptProduceBYLJFCDD.setText(checkNullString(sampling.getRvandfc()));//备样量
                    checkForm.setAcceptProduceBYLFCDD(sampling.getRvandfc());
                }


                if(!TextUtils.isEmpty(sampling.getSendaddress())){
                    lwTvAcceptProduceJSYDD.setText(checkNullString(sampling.getSendaddress()));//寄送地址
                    checkForm.setAcceptProduceJSYDD(sampling.getSendaddress());
                }


                if(!TextUtils.isEmpty(sampling.getEndtime())){
                    lwTvAcceptProduceJSYJZRQ.setText(checkNullString(sampling.getEndtime()));
                    checkForm.setAcceptProduceJSYJZRQ(checkNullString(sampling.getEndtime()));
                }

                //抽样单位如果取不到数据就显示默认数据
                if(!TextUtils.isEmpty(sampling.getSammanname())){//服务端存的的是单位的id，界面需要显示id对应的名称
                    lwTvSampleCompanyName.setText(sampling.getSamplingnamemean());
                    checkForm.setCheckCompanyName(sampling.getSamplingnamemean());
                    checkForm.setCheckCompanyNameId(sampling.getSammanname());
                    lwTvSampleCompanyAddress.setText(checkNullString(sampling.getSamplingaddress()));
                    checkForm.setCheckCompanyAddress(sampling.getSamplingaddress());
                    lwTvSampleCompanyEMSID.setText(checkNullString(sampling.getSamplingcode()));
                    checkForm.setCheckCompanyEMS(sampling.getSamplingcode());
                    lwTvSampleCompanyConnectName.setText(checkNullString(sampling.getSamplingman()));
                    checkForm.setCheckCompanyConnectName(sampling.getSamplingman());
                    lwTvSampleCompanyConnectTel.setText(checkNullString(sampling.getSamplingtel()));
                    checkForm.setCheckCompanyConnectTel(sampling.getSamplingtel());
                    lwTvSampleCompanyConnectEmail.setText(checkNullString(sampling.getSamplingemail()));
                    checkForm.setCheckCompanyEmail(sampling.getSamplingemail());
                }

                if(!TextUtils.isEmpty(sampling.getRemark())){
                    //备注
                    lwTvBz.setText(sampling.getRemark());
                    checkForm.setBzInfo(sampling.getRemark());
                }

                //日期格式
                try {
                    String fillInDate = sampling.getFillInDate();
                    SimpleDateFormat fillFormat = new SimpleDateFormat("yyyy 年MM 月dd 日",Locale.CHINA);
                    Date filldate = fillFormat.parse(fillInDate);
                    SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
                    fillInDate =  nowFormat.format(filldate);
                    if(!TextUtils.isEmpty(fillInDate)){
                        String date[] = fillInDate.split("-");
                        lwTvCYR_NYR.setText(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                        lwTvAcceptProduceCYRQ.setText(fillInDate);
                        checkForm.setAcceptProduceCYRQ(fillInDate);
                        CYR_DATE_YEAR = date[0];
                        CYR_DATE_MONTH = date[1];
                        CYR_DATE_DAY = date[2];
                        checkForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    }
                } catch (Exception e) {
                    SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
                    Date nowDate = new Date();
                    String fillInDate =  nowFormat.format(nowDate);
                    if(!TextUtils.isEmpty(fillInDate)){
                        String date[] = fillInDate.split("-");
                        lwTvCYR_NYR.setText(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                        CYR_DATE_YEAR = date[0];
                        CYR_DATE_MONTH = date[1];
                        CYR_DATE_DAY = date[2];
                        lwTvAcceptProduceCYRQ.setText(fillInDate);
                        checkForm.setAcceptProduceCYRQ(fillInDate);
                        checkForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    }
                }

            }
            DbController.getInstance(this).getDaoSession().getCheckFormDao().insertOrReplace(checkForm);
        }

    }

    private String checkNullString(String s){
        if(TextUtils.isEmpty(s)) {
            return "";
        }else {
            return s;
        }
    }


    /**
     * 封样状态
     */
    private void initCheckProduceFYZT(TextView textView){
        String [] listItems = new String[]{
                "封样完好，封条完整",
                "纸箱包装，封条完整",
                "罐装包装，封条完整",
                "裸装，封条完整",
                "塑料包装，封条完整",
                "其他"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 5){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"输入样状态");
                }

                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };
        mNormalPopup = QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
    }


    /**
     * 封存地点
     */
    private void initCheckProduceFCDD(TextView textView){
        String [] listItems = new String[]{
                "受检单位",
                "生产单位",
                "抽样单位",
                "检验单位",
                "当地市场管理部门",
                "其他"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 5){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"封存地点");
                }
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };
       mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);

    }

    /**
     * 抽样数量单位
     */
    private void initCheckProduceCYSlDW(TextView textView){
        String [] listItems = new String[]{
                "m",
                "m²",
                "m³",
                "ml",
                "L",
                "t ",
                "g",
                "kg",

        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1){
                    lwTvAcceptProduceCYSL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>2</sup>");
                }else if(i == 2){
                    lwTvAcceptProduceCYSL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>3</sup>");
                }else{
                    lwTvAcceptProduceCYSL.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
                }
                upDataBase(lwTvAcceptProduceCYSL.getId(),lwTvAcceptProduceCYSL.getText().toString());
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                if(i == 1){
//                    lwTvAcceptProduceCYSL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>2</sup>");
//                }else if(i == 2){
//                    lwTvAcceptProduceCYSL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>3</sup>");
//                }else{
//                    lwTvAcceptProduceCYSL.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
//                }
//                upDataBase(lwTvAcceptProduceCYSL.getId(),lwTvAcceptProduceCYSL.getText().toString());
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
//        mListPopup.show(textView);
    }
//    /**
//     * 抽样批量单位
//     */
//    private void initCheckProduceCYPLDW(TextView textView){
//        String [] listItems = new String[]{
//                "m",
//                "m²",
//                "m³",
//                "ml",
//                "L",
//                "t ",
//                "g",
//                "kg"
//        };
//
//        List<String> data = new ArrayList<>();
//        Collections.addAll(data, listItems);
//        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
//        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 1){
//                    lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>2</sup>");
//                }else if(i == 2){
//                    lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>3</sup>");
//                }else{
//                    lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
//                }
//
//                upDataBase(lwTvAcceptProducePL.getId(),lwTvAcceptProducePL.getText().toString());
//                if (mNormalPopup != null) {
//                    mNormalPopup.dismiss();
//                }
//            }
//        };
//
//        mNormalPopup =  QMUIPopups.listPopup(this,
//                QMUIDisplayHelper.dp2px(this, 250),
//                QMUIDisplayHelper.dp2px(this, 500),
//                adapter,
//                onItemClickListener)
//                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
//                .preferredDirection(QMUIPopup.DIRECTION_TOP)
//                .shadow(true)
//                .dimAmount(0.6f)
//                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
//                .onDismiss(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//
//                    }
//                })
//                .show(textView);
////        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
////        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                if(i == 1){
////                    lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>2</sup>");
////                }else if(i == 2){
////                    lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>3</sup>");
////                }else{
////                    lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
////                }
////
////                upDataBase(lwTvAcceptProducePL.getId(),lwTvAcceptProducePL.getText().toString());
////                mListPopup.dismiss();
////            }
////        });
////        mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
////            @Override
////            public void onDismiss() {
////
////            }
////        });
////        mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
////        mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
////        mListPopup.show(textView);
//    }


    /**
     * 角标
     */
    private void initJB(EditText editText){
        String [] listItems = new String[]{
                "上角标",
                "下角标"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){//上
                    showEditTextDialog(editText,0);
                }else if(i == 1){//下
                    showEditTextDialog(editText,1);
                }

                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(editText);

//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 0){//上
//                    showEditTextDialog(editText,0);
//                }else if(i == 1){//下
//                    showEditTextDialog(editText,1);
//                }
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
//        mListPopup.show(editText);
    }

    /**
     * 单价单位
     */
    private void initCheckProduceDJDW(TextView textView){
        String [] listItems = new String[]{
                "m",
                "m²",
                "m³",
                "ml",
                "L",
                "t ",
                "g",
                "kg"
        };



        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1){
                    lwTvAcceptProduceDJ.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>2</sup>");
                }else if(i == 2){
                    lwTvAcceptProduceDJ.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>3</sup>");
                }else{
                    lwTvAcceptProduceDJ.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
                }
                upDataBase(lwTvAcceptProduceDJ.getId(),lwTvAcceptProduceDJ.getText().toString());

                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 1){
//                    lwTvAcceptProduceDJ.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>2</sup>");
//                }else if(i == 2){
//                    lwTvAcceptProduceDJ.setText(lwTvAcceptProducePL.getText().toString() +"m<sup>3</sup>");
//                }else{
//                    lwTvAcceptProduceDJ.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
//                }
//                upDataBase(lwTvAcceptProduceDJ.getId(),lwTvAcceptProduceDJ.getText().toString());
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
//        mListPopup.show(textView);
    }


    /**
     * 从业人员数
     */
    private void initCheckProduceCYRYS(TextView textView){
        String [] listItems = new String[]{
                "人",
                "万人",
        };



        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lwTevProductCYRYS.setText(lwTevProductCYRYS.getText().toString() + listItems[i]);
                upDataBase(lwTevProductCYRYS.getId(),lwTevProductCYRYS.getText().toString());

                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                lwTevProductCYRYS.setText(lwTevProductCYRYS.getText().toString() + listItems[i]);
//                upDataBase(lwTevProductCYRYS.getId(),lwTevProductCYRYS.getText().toString());
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
//        mListPopup.show(textView);
    }

    /**
     * 营业收入单位
     */
    private void initCheckProduceYYSRDW(TextView textView){
        String [] listItems = new String[]{
                "元",
                "万元",
                "亿元"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lwTevProductYYSR.setText(lwTevProductYYSR.getText().toString() + listItems[i]);
                upDataBase(lwTevProductYYSR.getId(),lwTevProductYYSR.getText().toString());
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                lwTevProductYYSR.setText(lwTevProductYYSR.getText().toString() + listItems[i]);
//                upDataBase(lwTevProductYYSR.getId(),lwTevProductYYSR.getText().toString());
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
//        mListPopup.show(textView);
    }

    /**
     * 营业收入单位
     */
    private void initCheckProduceGYFDW(TextView textView){
        String [] listItems = new String[]{
                "元",
                "万元",
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bzGYF.setText(bzGYF.getText().toString() + listItems[i]);
                upDataBase(bzGYF.getId(),bzGYF.getText().toString());
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                bzGYF.setText(bzGYF.getText().toString() + listItems[i]);
//                upDataBase(bzGYF.getId(),bzGYF.getText().toString());
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
//        mListPopup.show(textView);
    }
    /**
     * 商标选择
     */
    private void initCheckProduceSB(TextView textView){
        String [] listItems = new String[]{
                "图形商标",
                "/",
                "其他"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 2){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"输入商标");
                }
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i != 2){//不是点击其他
//                    textView.setText(listItems[i]);
//                    upDataBase(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"输入商标");
//                }
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
//        mListPopup.show(textView);

    }

    /**
     * 产品等级
     */
    private void initCheckProduceCPDJ(TextView textView){
        String [] listItems = new String[]{
                "合格品",
                "优等品",
                "Ⅰ级",
                "Ⅱ级",
                "其他"

        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 4){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"产品等级");
                }

                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i != 4){//不是点击其他
//                    textView.setText(listItems[i]);
//                    upDataBase(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"产品等级");
//                }
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
//        mListPopup.show(textView);

    }
    /**
     * 寄送杨截止日期
     */
    private void initCheckProduceJSYJZRQ(TextView textView){
        String [] listItems = new String[]{
                "/",
                "选择日期",
        };
        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showTimePicker(lwTvAcceptProduceJSYJZRQ.getId());
                }
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 0){
//                    textView.setText(listItems[i]);
//                    upDataBase(textView.getId(),listItems[i]);
//                }else{
//                    showTimePicker(lwTvAcceptProduceJSYJZRQ.getId());
//                }
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
//        mListPopup.show(textView);
    }
    /**
     * 寄送地址
     */
    private void initCheckProduceJSYDD(TextView textView){
        String [] listItems = new String[]{
                "长沙市雨花亭新建西路189号 ",
                "湖南省长沙市经开区漓湘东路198号 ",
                "其他"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 2){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"输入寄送地址");
                }
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup = QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i != 2){//不是点击其他
//                    textView.setText(listItems[i]);
//                    upDataBase(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"输入寄送地址");
//                }
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
//        mListPopup.show(textView);

    }


    private void initProductEMSID(TextView textView){
        String [] listItems = new String[]{
                "/",
                "其他",
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"邮政编码");
                }
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup = QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 0){
//                    textView.setText(listItems[i]);
//                    upDataBase(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"邮政编码");
//                }
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
//        mListPopup.show(textView);
    }
    @SingleClick(500)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lwtv_descrable02:
//                showLongMessageDialog();
                showEditTextDialog(lwTvReportID,"在此输入报告编号");
                break;
            case R.id.lwtv_task_from://任务来源
                checkOrRecheckFormController.requestTaskCompanyInfo(CheckOrRecheckFormController.TYPE_TASK_FROM);
                break;
            case R.id.lwtv_task_class://任务类别
                checkOrRecheckFormController.requestTaskCompanyInfo(CheckOrRecheckFormController.TYPE_TASK_CALSS);
                break;
            case R.id.lwtv_accept_com_desc_name_in://单位名称
                showEditTextDialog(lwTvAcceptComName,"在此输入单位名称");
                break;
            case R.id.lwtv_accept_com_desc_address_in://通讯地址
                showEditTextDialog(lwTvAcceptComAddres,"在此输入单位地址");
                break;
            case R.id.lwtv_descrable_faren_in://法人代表
                showEditTextDialog(lwTvFaRen,"在此输入法人代表");
                break;
            case R.id.lwtv_connect_name_in://联系人
                showEditTextDialog(lwTvConnectName,"在此输入联系人姓名");
                break;
            case R.id.lwtv_connect_tel_in://联系人电话
                showEditTextDialog(lwTvConnectTel,"在此输入联系人电话");
                break;
            case R.id.lwtv_product_com_name_in://单位名称
                showEditTextDialog(lwTvProductName,"在此输入单位名称");
                break;
            case R.id.lwtv_product_com_address_in://单位地址
                showEditTextDialog(lwTvProductAddress,"在此输入单位名称");
                break;
            case R.id.lwtv_product_com_ems_id_in://邮政编码
                initProductEMSID(lwTvProductEMSID);
                break;
            case R.id.lwtv_product_com_faren_in://法人代表
                showEditTextDialog(lwTvProdictFaRen,"在此输入法人代表");
                break;
            case R.id.lwtv_product_com_tel_name_in://联系人
                showEditTextDialog(lwTvProductTelName,"在此输入联系人");
                break;
            case R.id.lwtv_product_com_tel_num_in://联系电话
                showEditTextDialog(lwTvProductTleNum,"在此输入联系电话");
                break;
            case R.id.lwtv_product_com_business_license_in://营业执照
                showEditTextDialog(lwTvProductBusinessLicense,"在此输入营业执照");
                break;
            case R.id.lwtv_product_com_id_in://机构代码
                showEditTextDialog(lwTevProductID,"在此输入统一信用代码");
                break;
            case R.id.lwtv_product_com_cyrys_in://从业人员数
                showEditTextDialog(lwTevProductCYRYS,"在此输入从业人员数");
                break;
            case R.id.lwtv_product_com_yysr_in://营业收入
                showEditTextDialog(lwTevProductYYSR,"在此输入营业收入");
                break;
            case R.id.lwtv_product_com_yysr://营业收入单位
                initCheckProduceYYSRDW(lwTevProductYYSRDW);
                break;

            case R.id.lwtv_accept_produce_info_zs_id_in://证书编号
                if(rbGYCPSCXKZ.isChecked() || rbSC.isChecked() ||rbCCC.isChecked()||rbOthers.isChecked()){
                    showEditTextDialog(lwTvAcceptProduceInfoZSID,"在此输入证书编号");
                }else{
                    showMasterInfoFail("请先选择证书类型。");
                }

                break;
//            case R.id.lwtv_accept_produce_info_cpmc_in://产品名称
//                showEditTextDialog(lwTvAcceptProduceCPMC,"在此输入产品名称");
//                break;
            case R.id.lwtv_accept_produce_info_cpmc://产品名称
                initJB(lwTvAcceptProduceCPMC);
                break;
            case   R.id.lwtv_accept_produce_info_ggxh://规格型号
                    initJB(lwTvAcceptProduceGGXH);

                break;


            case R.id.lwtv_accept_produce_info_scrqph_in://生产日期/批号
                showEditTextDialog(lwTvAcceptProduceSCRI,"在此输入生产日期/批号");
                break;
            case R.id.lwtv_accept_produce_info_sb_in://商标

                initCheckProduceSB(lwTvAcceptProduceSB);
//                showEditTextDialog(lwTvAcceptProduceSB,"在此输入商标");
                break;
            case R.id.lwtv_accept_produce_info_cysl_in://抽样数量
                showEditTextDialog(lwTvAcceptProduceCYSL,"在此输入抽样数量");
                break;
            case R.id.lwtv_accept_produce_info_byljfcdd_in:
                initCheckProduceFCDD(lwTvAcceptProduceBYLJFCDD);
                break;

            case R.id.lwtv_accept_produce_info_cpdj_in://产品等级
                initCheckProduceCPDJ(lwTvAcceptProduceCPDJ);
                break;

            case R.id.lwtv_accept_produce_info_bjzxbzjswj_in://标注执行标准/技术文件
                showEditTextDialog(lwTvAcceptProduceBZZXBZ,"在此输入标注执行标准/技术文件");
                break;
            case R.id.lwtv_accept_produce_info_cyrq_in://抽样日期
                showTimePicker(lwTvAcceptProduceCYRQ.getId());
                break;

            case R.id.lwtv_accept_produce_info_pl_in:
                showEditTextDialog(lwTvAcceptProducePL,"抽样基数/批量");
                break;
//            case R.id.lwtv_accept_produce_info_pl:
////                initCheckProduceCYPLDW(lwTvAcceptProducePLDW);
//                break;

            case R.id.lwtv_accept_produce_info_dj_in:
                showEditTextDialog(lwTvAcceptProduceDJ,"单价");
                break;
            case R.id.lwtv_accept_produce_info_dj://单价单位
                initCheckProduceDJDW(lwTvAcceptProduceDJDW);
                break;
            case R.id.lwtv_product_com_cyrys://从业人员数单位
                initCheckProduceCYRYS(lwTevProductCYRY);
                break;

            case R.id.lwtv_accept_produce_info_fyzt_in://封样状态
                initCheckProduceFYZT(lwTvAcceptProduceFYZT);
                break;
            case R.id.lwtv_accept_produce_info_jsydd_in://寄送样地点
                initCheckProduceJSYDD(lwTvAcceptProduceJSYDD);
//                showEditTextDialog(lwTvAcceptProduceJSYDD,"在此输入寄送样地点");
                break;
            case R.id.lwtv_accept_produce_info_jsyjzrqt_in://寄送样截至日期
                initCheckProduceJSYJZRQ(lwTvAcceptProduceJSYJZRQ);
                break;
            case R.id.lwtv_sample_company_info_name_in://抽样单位名称
                checkOrRecheckFormController.requestTaskCompanyInfo(CheckOrRecheckFormController.TYPE_CYDY_NAME);
                break;

            case R.id.lwtv_sample_company_info_address_in://抽样单位地址
                showEditTextDialog(lwTvSampleCompanyAddress,"在此输入抽样单位地址");
                break;

            case R.id.lwtv_sample_company_info_emss_in://抽样单位邮政编码
                showEditTextDialog(lwTvSampleCompanyEMSID,"在此输入抽样单位邮政编码");
                break;

            case R.id.lwtv_sample_company_info_lxr_in://抽样单位联系人
                showEditTextDialog(lwTvSampleCompanyConnectName,"在此输入抽样单位联系人");
                break;
            case R.id.lwtv_sample_company_info_lx_tel_in://抽样单位联系人电话
                showEditTextDialog(lwTvSampleCompanyConnectTel,"在此输入抽样单位联系人电话");

                break;
            case R.id.lwtv_sample_company_info_cz_email_in://抽样单位Email
                showEditTextDialog(lwTvSampleCompanyConnectEmail,"在此输入抽样单位Email");
                break;

            case R.id.lwtv_bz_in://备注说明
                showEditTextDialog(lwTvBz,"在此输入备注说明");
                break;
            case R.id.lwtv_bz_xmbah_in:
                showEditTextDialog(bzXHBAH,"项目备案号");
                break;
            case R.id.lwtv_bz_gyf_in:
                showEditTextDialog(bzGYF,"购样费");
                break;
            case R.id.lwtv_bz_gyf:
               initCheckProduceGYFDW(bzGYFDW);
                break;

            case R.id.lwtv_accept_produce_info_cysl://抽样数量单位
                initCheckProduceCYSlDW(lwTvAcceptProduceCYSLDW);
                break;


            case R.id.iv_scdw_qz://产单位签字
                SignBean signBeanSC = new SignBean();
                signBeanSC.setId(checkOrRecheckFormController.getTaskSampleId());
                signBeanSC.setImgType(SignActivityController.IMAGE_TYPE_CHECK_SCDW);
                toSign(signBeanSC);
                break;
            case R.id.iv_sjdw_qz://受检单位签字
                SignBean signBeanSJ = new SignBean();
                signBeanSJ.setId(checkOrRecheckFormController.getTaskSampleId());
                signBeanSJ.setImgType(SignActivityController.IMAGE_TYPE_CHECK_SJDW);
                toSign(signBeanSJ);

                break;
            case R.id.ll_cyr_qz://抽样人
                SignBean signBeanCY = new SignBean();
                signBeanCY.setId(checkOrRecheckFormController.getTaskSampleId());
                signBeanCY.setImgType(SignActivityController.IMAGE_TYPE_CHECK_CYR);
                toSign(signBeanCY);
                break;
            case R.id.lwtv_sjdw_nyr:
                showTimePicker(R.id.lwtv_sjdw_nyr);
                break;
            case R.id.lwtv_scdw_nyr:
                showTimePicker(R.id.lwtv_scdw_nyr);
                break;
            case R.id.lwtv_cyr_nyr:
                showTimePicker(R.id.lwtv_cyr_nyr);
                break;
            case R.id.btn_more:
                showMoreView(btnMore);
                break;
            case R.id.ll_back:
                onBackPressed();
                break;

        }
    }

    /**
     * 去手写签名
     */
    private void toSign(SignBean signBean){
        Intent intent = new Intent(this, SignActivity.class);
        intent.putExtra(SignActivityController.SIGN_TAG,signBean);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @Override
    protected void doOnBackPressed() {
        super.doOnBackPressed();
        saveToHandleForm();
    }

    /**
     * 将一些数据保存到处置单表中
     */
    private void saveToHandleForm(){
        if(handleForm != null){
            //受检单位名称
            handleForm.setSjdwmc(checkForm.getAcceptCompanyName());
            handleForm.setCpmc(checkForm.getAcceptProduceCPMC());
            handleForm.setGgxh(checkForm.getAcceptProduceGGCH());
            DBManagerFactory.getInstance().getHandleManager().updateByHandleForm(handleForm);
        }
    }



    public void showPopView(View v, List<TaskCompany> list){
        initListPopupIfNeed(v,list);
    }

    private void initListPopupIfNeed(View v,List<TaskCompany> list) {
            List<PopListBean> data = new ArrayList<>();
            for (int i = 0; i < list.size(); i++){
                PopListBean popListBean = new PopListBean();
                popListBean.setId(list.get(i).getValue());
                popListBean.setName(list.get(i).getItem());
                data.add(popListBean);
                if(list.get(i).getChild()!= null && list.get(i).getChild().size() > 0){
                    List<TaskCompany> childList = list.get(i).getChild();
                    for(TaskCompany taskCompany:childList){
                        PopListBean popBean = new PopListBean();
                        popBean.setId(taskCompany.getValue());
                        popBean.setName("------"+taskCompany.getItem());
                        data.add(popBean);
                    }
                }

            }
            ArrayList<String> arrayList = new ArrayList<>();

            for(int i = 0; i < data.size(); i++){
                arrayList.add(data.get(i).getName());
            }
            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, arrayList);
            AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
                switch (v.getId()){
                    case R.id.lwtv_task_from:
                        checkForm.setTaskFrom(data.get(i).getName());
                        checkForm.setTaskFromId(data.get(i).getId());
                        lwTvTaskFrom.setText(data.get(i).getName());
                        break;
                    case R.id.lwtv_task_class:
                        checkForm.setTaskClass(data.get(i).getName());
                        checkForm.setTaskClassId(data.get(i).getId());
                        lwTvTaskClass.setText(data.get(i).getName());
                        break;
                    case R.id.lwtv_sample_company_info_name_in:
                        checkForm.setCheckCompanyName(data.get(i).getName());
                        checkForm.setCheckCompanyNameId(data.get(i).getId());
                        lwTvSampleCompanyName.setText(data.get(i).getName());
                        break;

                }
                DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().update(checkForm);
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup = QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(v);
//            QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//            mListPopup.create(QMUIDisplayHelper.dp2px(this, 300), QMUIDisplayHelper.dp2px(this, 900), new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                    CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).searchByWhere(checkOrRecheckFormController.getTaskSampleId());
//                    CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
//                    switch (v.getId()){
//                        case R.id.lwtv_task_from:
//                            checkForm.setTaskFrom(data.get(i).getName());
//                            checkForm.setTaskFromId(data.get(i).getId());
//                            lwTvTaskFrom.setText(data.get(i).getName());
//                            break;
//                        case R.id.lwtv_task_class:
//                            checkForm.setTaskClass(data.get(i).getName());
//                            checkForm.setTaskClassId(data.get(i).getId());
//                            lwTvTaskClass.setText(data.get(i).getName());
//                            break;
//                        case R.id.lwtv_sample_company_info_name_in:
//                            checkForm.setCheckCompanyName(data.get(i).getName());
//                            checkForm.setCheckCompanyNameId(data.get(i).getId());
//                            lwTvSampleCompanyName.setText(data.get(i).getName());
//                            break;
//
//                    }
//                    DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().update(checkForm);
//
//                    mListPopup.dismiss();
//                }
//            });
//            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//
//            }
//            });
//        mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
//        mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
//        mListPopup.show(v);
    }

    /**
     * 编辑
     */
    private void showEditTextDialog(EditText editText,int type){
        if(editText == null){
            return;
        }
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("角标信息录入")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            if(type == 0){
                                String content = editText.getText().toString().trim() + "<sup>"+text+"</sup>";
                                editText.setText(content);//
                                editText.setSelection(content.length());
                            }else if(type ==1){
                                String content = editText.getText().toString().trim() + "<sub>"+text+"</sub>";
                                editText.setText(content);//下角标
                                editText.setSelection(content.length());
                            }
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    private void showEditTextDialog(TextView textView,String string){
        if(textView == null){
            return;
        }
        LayoutInflater factory = LayoutInflater.from(this);//提示框
        final View view = factory.inflate(R.layout.dialog_editext, null);//这里必须是final的
        final EditText edit = view.findViewById(R.id.editText);//获得输入框对象
        final TextView tip = view.findViewById(R.id.tip);//textview
        tip.setText("温馨提示：下角标:<sub> 内容 </sub>，上角标:<sup> 内容</sup>),\n例子：m³ = m<sup>3</sup>");
        String deafultInputText = "";
        if(textView.getId() == R.id.lwtv_accept_produce_info_scrqph_in){
            if(!TextUtils.isEmpty(textView.getText().toString())){
                deafultInputText = textView.getText().toString();
            }else {
                deafultInputText = "/";
            }

        }

//        else if(textView.getId() == R.id.lwtv_accept_produce_info_bjzxbzjswj_in){
//            if(!TextUtils.isEmpty(textView.getText().toString())){
//                deafultInputText = textView.getText().toString();
//            }else {
//                deafultInputText = "/";
//            }
//
//        }
        else if(textView.getId() == R.id.lwtv_accept_produce_info_dj_in){
            if(!TextUtils.isEmpty(textView.getText().toString())){
                deafultInputText = textView.getText().toString();
            }else {
                deafultInputText = "元/";
            }

        } else if(textView.getId() == R.id.lwtv_accept_produce_info_cysl_in){
            if(!TextUtils.isEmpty(textView.getText().toString())){
                deafultInputText = textView.getText().toString();
            }else {
                deafultInputText = "（检，备）";
            }
        }else{
            deafultInputText = textView.getText().toString();
        }
        edit.setText(deafultInputText);
        edit.setHint(string);
        new AlertDialog.Builder(this)
                .setTitle("信息录入")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CharSequence text = edit.getText();
                                if (text != null && text.length() > 0) {
                                    textView.setText(text);
                                    upDataBase(textView.getId(),text.toString());
                                    dialog.dismiss();
                                } else {
                                    textView.setText("");
                                    upDataBase(textView.getId(),"");
                                }
                            }
                }).setNegativeButton("取消", null).create().show();

    }

    //更新数据库
    private void upDataBase(int viewId,String text){
//        CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
        if(checkForm == null){return;}
        switch (viewId){
            case R.id.lwtv_report_id://报告编号
                checkForm.setReportId(text);
            case R.id.lwtv_accept_com_desc_name_in://受检单位
                checkForm.setAcceptCompanyName(text);
                break;
            case R.id.lwtv_accept_com_desc_address_in://通讯地址
                checkForm.setAcceptCompanyAddress(text);
                break;
            case R.id.lwtv_descrable_faren_in://法人代表
                checkForm.setAcceptCompanyFaren(text);
                break;
            case R.id.lwtv_connect_name_in://联系
                checkForm.setAcceptCompanyConnectNanme(text);
                break;
            case R.id.lwtv_connect_tel_in://联系fangshi
                checkForm.setAcceptCompanyConnecTel(text);
                break;
            case R.id.lwtv_product_com_name_in://单位名称
                checkForm.setProductCompanyName(text);
                // 流通领域调查此接口
                if(checkOrRecheckFormController.getPlantype().equals("2")){
                    checkOrRecheckFormController.requestProductsName(checkOrRecheckFormController.getPlanId(),text);
                }
                break;
            case R.id.lwtv_product_com_address_in://单位地址
                checkForm.setProductCompanyAdress(text);
                break;
            case R.id.lwtv_product_com_ems_id_in://邮政编码
                checkForm.setProductCompanyEMS(text);
                break;
            case R.id.lwtv_product_com_faren_in://法人代表
                checkForm.setProductCompanyFaren(text);
                break;
            case R.id.lwtv_product_com_tel_name_in://联系人
                checkForm.setProductCompanyConnectName(text);
                break;
            case R.id.lwtv_product_com_tel_num_in://联系电话
                checkForm.setProductCompanyConnectTEL(text);
                break;
            case R.id.lwtv_product_com_business_license_in://营业执照
                checkForm.setProductCompanyLicense(text);
                break;
            case R.id.lwtv_product_com_id_in://机构代码
               checkForm.setProductCompanyID(text);
                break;

            case R.id.lwtv_product_com_cyrys_in://从业人员数
                checkForm.setProductCompanyDimensionsCYRYS(text);
                break;
            case R.id.lwtv_product_com_yysr_in://营业收入
                checkForm.setProductCompanyDimensionsYYSR(text);
                break;


            case R.id.rb_gv:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_person:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_group:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_youxian:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_lianying:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gufen:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gufenhz:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_others:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_hzjy:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_h_zuo_jy:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gatdzjy:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gattzgfyxgs:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_zwhz:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_zwhzhuo:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_wzqy:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_wstzgfyxgs:
                checkForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gycpscxkz:
                 checkForm.setAcceptProduceLicenseType(text);
                break;
            case R.id.rb_product_info_sc:
                checkForm.setAcceptProduceLicenseType(text);
                break;
            case R.id.rb_product_info_ccc:
                checkForm.setAcceptProduceLicenseType(text);
                break;
            case R.id.rb_product_info_others:
                checkForm.setAcceptProduceLicenseType(text);
                break;
//            case R.id.cb_pl://批量类型
//                checkForm.setAcceptProType(text);
//                break;
//            case R.id.cb_js://基数类型
//                checkForm.setAcceptProType(text);
//                break;

            case R.id.lwtv_accept_produce_info_zs_id_in://证书编号
                //校验证书id是否正确
                if(rbGYCPSCXKZ.isChecked() && verifyGYSCXKZ(text)){
                    checkForm.setAcceptProduceLicenseId(text);
                }else if(rbSC.isChecked() && verifySC(text)){
                    checkForm.setAcceptProduceLicenseId(text);
                }else if(rbCCC.isChecked() && verifyCCC(text)){
                    checkForm.setAcceptProduceLicenseId(text);
                }else if(rbOthers.isChecked()){
                    checkForm.setAcceptProduceLicenseId(text);
                }else {
                    new QMUIDialog.MessageDialogBuilder(this)
                            .setTitle("证书编号")
                            .setMessage("证书编号输入异常。")
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
                                    checkForm.setAcceptProduceLicenseId(text);
                                }
                            })
                            .create().show();
                }

                break;
            case R.id.lwtv_accept_produce_info_cpmc_in://产品名称
                checkForm.setAcceptProduceCPMC(text);
                break;
            case R.id.lwtv_accept_produce_info_ggxh_in://规格型号
                checkForm.setAcceptProduceGGCH(text);
                break;
            case R.id.lwtv_accept_produce_info_scrqph_in://生产日期/批号
                checkForm.setAcceptProduceSCRQ(text);
                break;
            case R.id.lwtv_accept_produce_info_sb_in://商标
                checkForm.setAcceptProduceSB(text);
                break;
            case R.id.lwtv_accept_produce_info_cysl_in://抽样数量
                checkForm.setAcceptProduceCYSL(text);
                break;
            case R.id.lwtv_accept_produce_info_cpdj_in://产品等级
                checkForm.setAcceptProduceCPDJ(text);
                break;

            case R.id.lwtv_accept_produce_info_bjzxbzjswj_in://标注执行标准/技术文件
                checkForm.setAcceptProduceBZZXBZ(text);
                break;

            case R.id.lwtv_accept_produce_info_pl_in:
                checkForm.setAcceptProducePL(text);
                break;

            case R.id.lwtv_accept_produce_info_dj_in:
                checkForm.setAcceptProduceDJ(text);
                break;

            case R.id.lwtv_accept_produce_info_byljfcdd_in://封存地点
                checkForm.setAcceptProduceBYLFCDD(text);
                break;
            case R.id.lwtv_accept_produce_info_fyzt_in://封样状态
                checkForm.setAcceptProduceFYZT(text);

                break;
            case R.id.lwtv_accept_produce_info_jsydd_in://寄送样地点
                checkForm.setAcceptProduceJSYDD(text);
                break;
            case R.id.lwtv_sample_company_info_name_in://抽样单位名称
                checkOrRecheckFormController.requestTaskCompanyInfo(CheckOrRecheckFormController.TYPE_CYDY_NAME);
                break;

            case R.id.lwtv_sample_company_info_address_in://抽样单位地址
                checkForm.setCheckCompanyAddress(text);
                break;

            case R.id.lwtv_sample_company_info_emss_in://抽样单位邮政编码
               checkForm.setCheckCompanyEMS(text);
                break;

            case R.id.lwtv_sample_company_info_lxr_in://抽样单位联系人
                checkForm.setCheckCompanyConnectName(text);
                break;
            case R.id.lwtv_sample_company_info_lx_tel_in://抽样单位联系人电话
                checkForm.setCheckCompanyConnectTel(text);

                break;
            case R.id.lwtv_sample_company_info_cz_email_in://抽样单位Email
               checkForm.setCheckCompanyEmail(text);
                break;

            case R.id.lwtv_bz_in://备注说明
                checkForm.setBzInfo(text);
                break;
            case R.id.lwtv_bz_xmbah_in://项目备案号
                checkForm.setBzXMBAH(text);
                break;
            case R.id.lwtv_bz_gyf_in:
                checkForm.setBzGYF(text);
                break;
        }
        DBManagerFactory.getInstance().getCheckFormManager().updateByCheckForm(checkForm);
//        DbController.getInstance(this).getDaoSession().getCheckFormDao().update(checkForm);
    }


    /**
     * 显示日期选择
     * @param viewId
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
                    new QMUIDialog.MessageDialogBuilder(CheckOrRecheckFormActivity.this)
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
        CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
        String dates[] = dateString.split("-");
        switch (viewId){
            case R.id.lwtv_sjdw_nyr:
                String sjTime = dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
                SJDW_DATE_YEAR = dates[0];
                SJDW_DATE_MONTH = dates[1];
                SJDW_DATE_DAY = dates[2];
                lwTvSJDW_NYR.setText(sjTime);
                checkForm.setSjDwRQ(sjTime);
                break;
            case R.id.lwtv_scdw_nyr:
                String scTime = dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
                SCDW_DATE_YEAR = dates[0];
                SCDW_DATE_MONTH = dates[1];
                SCDW_DATE_DAY = dates[2];
                lwTvSCDW_NYR.setText(scTime);
                checkForm.setScDwRQ(scTime);
                break;
            case R.id.lwtv_cyr_nyr:
                String cyrTime = dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
                CYR_DATE_YEAR = dates[0];
                CYR_DATE_MONTH = dates[1];
                CYR_DATE_DAY = dates[2];
                lwTvCYR_NYR.setText(cyrTime);
                checkForm.setCyDwRQ(cyrTime);
                break;
            case R.id.lwtv_accept_produce_info_cyrq_in:
                lwTvAcceptProduceCYRQ.setText(dateString);
                checkForm.setAcceptProduceCYRQ(dateString);
                break;

            case R.id.lwtv_accept_produce_info_jsyjzrqt_in://寄送样截至日期
                lwTvAcceptProduceJSYJZRQ.setText(dateString);
                checkForm.setAcceptProduceJSYJZRQ(dateString);
                break;
        }
        DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().update(checkForm);
    }

    /**
     * 更多
     */
    private void showMoreView(View view){
        String[] listMoreItems = new String[3];
//        listMoreItems[0] = "扫一扫";
        if(AccountManager.getInstance().getIsOpenCode().equals(AccountManager.OPEN_CODE_YES)){
            listMoreItems[0] = "生成PDF文件并电子签章";
        }else {
            listMoreItems[0] = "生成PDF文件";
        }
        listMoreItems[1] = "使用模版数据";
        listMoreItems[2] = "通过统一信用代码或工商注册获取";
        List<String> data = new ArrayList<>();
        Collections.addAll(data, listMoreItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
//                    case SCAN:
//                        break;
                    case PDF:
                        pdfDialog = new QMUITipDialog.Builder(CheckOrRecheckFormActivity.this)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord("PDF生成中...")
                                .create(false);
                        pdfDialog.show();
                        //将表单数据全部上传到后台
                        formToService();
//                        formToPDF();
                        break;
                    case MOBAN:
                        checkOrRecheckFormController.requestMasterpleList(MasterplterMainActivity.REPORT_TYPE_SAMPLIG);
                        break;
                    case COMPANYID:
                        QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(CheckOrRecheckFormActivity.this);
                        builder.setTitle("统一信用代码或者工商注册码")
                                .setPlaceholder("请在此输入代码")
                                .setInputType(InputType.TYPE_CLASS_TEXT)
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        CharSequence text = builder.getEditText().getText();
                                        if (text != null && text.length() > 0) {
                                            checkOrRecheckFormController.requestCompanyInfoByCode(text.toString());
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(CheckOrRecheckFormActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .show();
                        break;

                }


                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup = QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(view);

//        QMUIListPopup qmuiListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        qmuiListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 200), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
//                switch (i){
////                    case SCAN:
////                        break;
//                    case PDF:
//                         pdfDialog = new QMUITipDialog.Builder(CheckOrRecheckFormActivity.this)
//                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                                .setTipWord("PDF生成中...")
//                                .create(false);
//                        pdfDialog.show();
//                        //将表单数据全部上传到后台
//                        formToService();
////                        formToPDF();
//                        break;
//                    case MOBAN:
//                        checkOrRecheckFormController.requestMasterpleList(MasterplterMainActivity.REPORT_TYPE_SAMPLIG);
//                        break;
//                    case COMPANYID:
//                        QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(CheckOrRecheckFormActivity.this);
//                        builder.setTitle("统一信用代码或者工商注册码")
//                                .setPlaceholder("请在此输入代码")
//                                .setInputType(InputType.TYPE_CLASS_TEXT)
//                                .addAction("取消", new QMUIDialogAction.ActionListener() {
//                                    @Override
//                                    public void onClick(QMUIDialog dialog, int index) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .addAction("确定", new QMUIDialogAction.ActionListener() {
//                                    @Override
//                                    public void onClick(QMUIDialog dialog, int index) {
//                                        CharSequence text = builder.getEditText().getText();
//                                        if (text != null && text.length() > 0) {
//                                            checkOrRecheckFormController.requestCompanyInfoByCode(text.toString());
//                                            dialog.dismiss();
//                                        } else {
//                                            Toast.makeText(CheckOrRecheckFormActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                })
//                                .show();
//                        break;
//
//                }
//
//                qmuiListPopup.dismiss();
//            }
//        });
//        qmuiListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//
//            }
//        });
//        qmuiListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
//        qmuiListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
//        qmuiListPopup.show(view);
    }

    /**
     * 校验工业生产许可证号
     */
    private boolean verifyGYSCXKZ(String string){
        if(string.contains("-")){
            return true;
        }
        return false;
    }

    /**
     * 校验CCC号
     */
    private boolean verifyCCC(String string){
        if(string.length() == 16){
            return true;
        }
        return false;
    }
    /**
     * 校验SC号
     */
    private boolean verifySC(String string){
        if(string.length() == 16 && string.contains("SC")){
            return true;
        }
        return false;
    }



    /**
     * 将模板数据更新到本地
     * @param list
     */
    public void showMenuDialog(List<MasterpleListBean> list) {
        ArrayList<String> listString = new ArrayList();
        for(MasterpleListBean listBean:list){
            listString.add(listBean.getMouldtitle());
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, listString);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
                showMessageNegativeDialog(list,i);
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup = QMUIPopups.listPopup(this,
                QMUIDisplayHelper.dp2px(this, 250),
                QMUIDisplayHelper.dp2px(this, 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .dimAmount(0.6f)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(btnMore);


    }


    private void showMessageNegativeDialog(List<MasterpleListBean> list,int pos) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("使用模版")
                .setMessage("确定要使用吗？（使用后将覆盖当前页面所有数据）")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String userid = AccountManager.getInstance().getUserId();
                        String moudleType = MasterplterMainActivity.REPORT_TYPE_SAMPLIG;
                        String moudleId = list.get(pos).getId();
                        checkOrRecheckFormController.requestTasksample(userid,moudleId,moudleType);
                        dialog.dismiss();
                    }
                })
                .show();
    }


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
//                        int height = QMUIDisplayHelper.getScreenHeight(CheckOrRecheckFormActivity.this);
//                        int statusBarHeight = QMUIDisplayHelper.getStatusBarHeight(CheckOrRecheckFormActivity.this);
//                        int width = QMUIDisplayHelper.getScreenWidth(CheckOrRecheckFormActivity.this);
//                        View view = getWindow().getDecorView();
//                        view.setDrawingCacheEnabled(true);
//                        view.buildDrawingCache();
//                        Bitmap activityBitmap = view.getDrawingCache();
//                        Rect rect = new Rect();
//                        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//                        Bitmap nowBitmap = Bitmap.createBitmap(activityBitmap,0,statusBarHeight,width,height-statusBarHeight*2);
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
//                        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));                        canvas.drawBitmap(nowBitmap,matrix,paint);
//                        doc.finishPage(page);
//                        CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
//                        String name;
//                        if(TextUtils.isEmpty(checkForm.getTaskCode())){
//                             name = dateString +"-" + checkOrRecheckFormController.getTaskSampleId()+".pdf";
//                        }else{
//                             name = dateString +"-"+ checkForm.getTaskCode()+".pdf";
//                        }
//                        //将地址插入数据库
//                        checkForm.setPdfPath(Constants.PDF_PATH + "/" + name);
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
//                        DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().update(checkForm);
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
//                        if(AccountManager.getInstance().getIsOpenCode().equals(AccountManager.OPEN_CODE_YES)){
//                            checkOrRecheckFormController.requestPDFSign(checkOrRecheckFormController.getTaskSampleId(),checkOrRecheckFormController.getTaskId(),MasterplterMainActivity.REPORT_TYPE_SAMPLIG);
//                        }else {
//                            if(pdfDialog != null && pdfDialog.isShowing()){
//                                pdfDialog.dismiss();
//                            }
//                            showPDFSuccess();
//                            CheckForm checkForm =  DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
//                            shareBySystem(checkForm.getPdfPath());
//                            setResult(CheckOrRecheckFormActivity.RESULT_CODE_PDF_SUCCESS);
//                        }
//
//                    }
//                });
//
//    }



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

    /**
     * 根据信用代码填写信息
     */
    private void setUIByCompanyId(CodeCompany codeCompany,int type){
//        String taskSampleId = checkOrRecheckFormController.getTaskSampleId();
//        Sampling sampling = checkOrRecheckFormController.getSampling(checkOrRecheckFormController.getTaskSampleId());
//        CheckForm checkForm = checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId());
        if(checkForm != null){
            if(type == 0){//受检单位信息
                lwTvAcceptComName.setText(checkNullString(codeCompany.getOrgName()));//受检单位名称和地址
                checkForm.setAcceptCompanyName(checkNullString(codeCompany.getOrgName()));
                lwTvAcceptComAddres.setText(checkNullString(codeCompany.getOrgAddress()));
                checkForm.setAcceptCompanyAddress(checkNullString(codeCompany.getOrgAddress()));
                lwTvFaRen.setText(checkNullString(codeCompany.getLegalName()));//受检单位法人
                checkForm.setAcceptCompanyFaren(codeCompany.getLegalName());
            }else if(type == 1){//生产单位信息
                lwTvProductName.setText(checkNullString(codeCompany.getOrgName()));//生产单位名称
                checkForm.setProductCompanyName(codeCompany.getOrgName());
                lwTvProductAddress.setText(checkNullString(codeCompany.getOrgAddress()));//生产单位地址
                checkForm.setProductCompanyAdress(codeCompany.getOrgAddress());
                lwTvProductEMSID.setText(checkNullString(codeCompany.getZipCode()));//生产单位邮政编码
                checkForm.setProductCompanyEMS(codeCompany.getZipCode());
                lwTvProdictFaRen.setText(checkNullString(codeCompany.getLegalName()));//生产单位法人
                checkForm.setProductCompanyFaren(codeCompany.getLegalName());
                lwTvProductBusinessLicense.setText(checkNullString(codeCompany.getOrgCode()));//生产单位营业执照
                checkForm.setProductCompanyLicense(codeCompany.getOrgCode());
                lwTevProductID.setText(checkNullString(codeCompany.getOrgCode()));//生产单位机构代码
                checkForm.setProductCompanyID(codeCompany.getOrgCode());
            }

            DbController.getInstance(this).getDaoSession().getCheckFormDao().insertOrReplace(checkForm);
        }



    }

    public void showMultiChoiceDialog(List<CodeCompany> list) {
        final String[] items = new String[]{"受检单位信息带入", "生产单位信息带入"};
        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(this)
                .setCheckedItems(new int[]{0, 1})
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {

                for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                    int checkPos = builder.getCheckedItemIndexes()[i];
                    if(checkPos == 0){//受检单位信息
                        setUIByCompanyId(list.get(0),0);

                    }else if(checkPos == 1){//生产单位信息
                        setUIByCompanyId(list.get(0),1);
                    }
                }
                dialog.dismiss();
            }
        });
        builder.show();
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

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "拒绝了读写存储权限", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
    public View getTaskFromView(){
        return lwTvTaskFrom;
    }

    public View getTaskClassView(){
        return lwTvTaskClass;
    }

    public View getCydwNameView(){
        return lwTvSampleCompanyName;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == IMAGE_REQUEST_CODE && resultCode == SignActivityController.IMAGE_SUCCESS_RESULT_CODE){
           Bundle bundle = data.getExtras(); //读取intent的数据给bundle对象
           String imagePath = bundle.getString(SignActivityController.RESULT_IMAGE_PATH);
           String imageType = bundle.getString(SignActivityController.RESULT_IMAGE_TYPE);
           switch (imageType){
               case SignActivityController.IMAGE_TYPE_CHECK_SCDW:
                   GlideManager.getInstance().ImageLoad(this,imagePath,ivSCDW,true);
                   break;
               case SignActivityController.IMAGE_TYPE_CHECK_SJDW:
                   GlideManager.getInstance().ImageLoad(this,imagePath,ivSJDW,true);

                   break;
               case SignActivityController.IMAGE_TYPE_CHECK_CYR:
//                   CheckForm checkForm = checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId());
                   String signCPath_1 = checkForm.getCheckPeopleSignImagePath_1();
                   if(!TextUtils.isEmpty(signCPath_1)){
                       GlideManager.getInstance().ImageLoad(this,signCPath_1,cyrImg_1,true);

                   }
                   String signCPath_2 = checkForm.getCheckPeopleSignImagePath_2();
                   if(!TextUtils.isEmpty(signCPath_2)){
                       GlideManager.getInstance().ImageLoad(this,signCPath_2,cyrImg_2,true);
                   }
                   break;
           }

       }
    }



    public void showPDFSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("PDF生成成功！")
                .create();
        sucDialog.show();


        btnMore.postDelayed(new Runnable() {
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


        btnMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }
    public void showMasterInfoFail(String string){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(string)
                .create();
        failDialog.show();


        btnMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSelectedTaskCompany(SelectedTaskCompanyMessage selectedTaskCompany){
        if(selectedTaskCompany.getType().equals(checkOrRecheckFormController.TYPE_TASK_FROM)){
            TaskCompany taskCompany = selectedTaskCompany.getTaskCompany();
            lwTvTaskFrom.setText(taskCompany.getItem());
//            CheckForm checkForm = checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId());
            checkForm.setTaskFrom(taskCompany.getItem());
            checkForm.setTaskFromId(taskCompany.getValue());
            DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().update(checkForm);

        }else if(selectedTaskCompany.getType().equals(checkOrRecheckFormController.TYPE_TASK_CALSS)){
            TaskCompany taskCompany = selectedTaskCompany.getTaskCompany();
            lwTvTaskClass.setText(taskCompany.getItem());
//            CheckForm checkForm = checkOrRecheckFormController.getCheckFormToDB(checkOrRecheckFormController.getTaskSampleId());
            checkForm.setTaskClass(taskCompany.getItem());
            checkForm.setTaskClassId(taskCompany.getValue());
            DbController.getInstance(CheckOrRecheckFormActivity.this).getDaoSession().getCheckFormDao().update(checkForm);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });

        }
    }



    /**
     * 将表单数据转换成json传递给服务端
     */
    private void formToService(){
//        CheckForm checkForm = DBManagerFactory.getInstance().getCheckFormManager().queryBuilder().where(CheckFormDao.Properties.Id.eq(checkOrRecheckFormController.getTaskSampleId())).build().unique();
        FormCheckBean formCheckBean = new FormCheckBean();
        if(checkForm != null){
            formCheckBean.setTaskFrom(checkForm.getTaskFrom());
            if(checkForm.getTaskClass() != null && checkForm.getTaskClass().contains("----")){
                formCheckBean.setTaskClass(checkForm.getTaskClass().substring(6));
            }else{
                formCheckBean.setTaskClass(checkForm.getTaskClass());
            }

            formCheckBean.setCheckFormCode(checkForm.getTaskCode());
            formCheckBean.setCheckFormReportCode(checkForm.getReportId());
            formCheckBean.setAcceptComName(checkForm.getAcceptCompanyName());
            formCheckBean.setAcceptComAddress(checkForm.getAcceptCompanyAddress());
            formCheckBean.setAcceptComLegal(checkForm.getAcceptCompanyFaren());
            formCheckBean.setAcceptComConNameAndTel(checkNullString(checkForm.getAcceptCompanyConnectNanme()) + checkNullString(checkForm.getAcceptCompanyConnecTel()));
            //生产
            formCheckBean.setProductComName(checkForm.getProductCompanyName());
            formCheckBean.setProductComAddress(checkForm.getProductCompanyAdress());
            formCheckBean.setProductComEMSCode(checkForm.getProductCompanyEMS());
            formCheckBean.setProductComLegal(checkForm.getProductCompanyFaren());
            formCheckBean.setProductComConName(checkForm.getProductCompanyConnectName());
            formCheckBean.setProductComTel(checkForm.getProductCompanyConnectTEL());
            formCheckBean.setProductComId(checkForm.getProductCompanyID());
            formCheckBean.setProductComLicense(checkForm.getProductCompanyLicense());
            formCheckBean.setProductComPerNum(checkForm.getProductCompanyDimensionsCYRYS());
            formCheckBean.setProductComIncome(checkForm.getProductCompanyDimensionsYYSR());
            formCheckBean.setProductComEcType(checkForm.getProductCompanySmallType());
            //受检产品信息
            formCheckBean.setAcceptProCerType(checkForm.getAcceptProduceLicenseType());
            formCheckBean.setAcceptProCerID(checkForm.getAcceptProduceLicenseId());
            formCheckBean.setAcceptProName(checkForm.getAcceptProduceCPMC());
            formCheckBean.setAcceptProModel(checkForm.getAcceptProduceGGCH());
            formCheckBean.setAcceptProDate(checkForm.getAcceptProduceSCRQ());
            formCheckBean.setAcceptProCheckDate(checkForm.getAcceptProduceCYRQ());
            formCheckBean.setAcceptProCheckNum(checkForm.getAcceptProduceCYSL());
            formCheckBean.setAcceptProBrand(checkForm.getAcceptProduceSB());
//            formCheckBean.setAcceptProBatch(checkForm.getAcceptProducePL());
            formCheckBean.setSamplingbaseandbatch(checkForm.getAcceptProducePL());//新批量
            formCheckBean.setAcceptProGrade(checkForm.getAcceptProduceCPDJ());
            formCheckBean.setAcceptProUnPrice(checkForm.getAcceptProduceDJ());
            formCheckBean.setAcceptProState(checkForm.getAcceptProduceFYZT());
            formCheckBean.setAcceptProField(checkForm.getAcceptProduceBZZXBZ());
            formCheckBean.setAcceptProSendAddress(checkForm.getAcceptProduceJSYDD());
            formCheckBean.setAcceptProSampleSizeAndAddress(checkForm.getAcceptProduceBYLFCDD());
            formCheckBean.setAcceptProSendEndDate(checkForm.getAcceptProduceJSYJZRQ());
//            formCheckBean.setAcceptProType(checkForm.getAcceptProType());
            //抽样单位
            formCheckBean.setCheckComName(checkForm.getCheckCompanyName());
            formCheckBean.setCheckComConName(checkForm.getCheckCompanyConnectName());
            formCheckBean.setCheckComAddress(checkForm.getCheckCompanyAddress());
            formCheckBean.setCheckComComTel(checkForm.getCheckCompanyConnectTel());
            formCheckBean.setCheckComEMSCode(checkForm.getCheckCompanyEMS());
            formCheckBean.setCheckComEmail(checkForm.getCheckCompanyEmail());
            //备注
            formCheckBean.setRemarkProjectNo(checkForm.getBzXMBAH());
            formCheckBean.setRemarkGetPrice(checkForm.getBzGYF());
            formCheckBean.setRemarkInfo(checkForm.getBzInfo());
            formCheckBean.setCheckPersonDateYEAR(CYR_DATE_YEAR);
            formCheckBean.setCheckPersonDateMONTH(CYR_DATE_MONTH);
            formCheckBean.setCheckPersonDateDAY(CYR_DATE_DAY);
            List<String> imgList = new ArrayList<>();
            imgList.add(checkForm.getCheckPeopleSignImagePath_1());
            imgList.add(checkForm.getCheckPeopleSignImagePath_2());
            formCheckBean.setCheckImg(imgList);

            //受检单位
            formCheckBean.setAcceptProYear(SJDW_DATE_YEAR);
            formCheckBean.setAcceptProMonth(SJDW_DATE_MONTH);
            formCheckBean.setAcceptProDay(SJDW_DATE_DAY);
            formCheckBean.setAcceptProImage(checkForm.getAcceptCompanySignImagePath());
            //生产单位
            formCheckBean.setProductProYear(SCDW_DATE_YEAR);
            formCheckBean.setProductProMonth(SCDW_DATE_MONTH);
            formCheckBean.setProductProDay(SCDW_DATE_DAY);
            formCheckBean.setProductProImage(checkForm.getProductCompanySignImagePath());
            checkOrRecheckFormController.requestSubmit(formCheckBean);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void showLongMessageDialog(String message) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("该单位本计划已有抽样产品")
                .setMessage(message)
                .addAction("我知道了", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}



