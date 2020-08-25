package com.product.sampling.ui.masterplate;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.product.sampling.R;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.form.bean.PopListBean;
import com.product.sampling.ui.widget.LWTextView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 抽样单模版
 * Created by 陆伟 on 2019/11/22.
 * Copyright (c) 2019 . All rights reserved.
 */


public class MasterplterEditActivity extends BaseActivity<MasterplterEditController> implements View.OnClickListener {
    private static final String TAG = "MasterplterEditActivity";
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
    private LWTextView lwTevProductCYRY;//从业人员单

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
    private LWTextView lwTvAcceptProduceCPMC;
    private LWTextView lwTvAcceptProduceGGXH;
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
//    private LWTextView lwTvAcceptProducePLDW;
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
    private ImageView ivSJDW,ivSCDW,cyrImg_1,cyrImg_2;
    private LinearLayout llCYR;

    private LWTextView lwTvSCDW_NYR,lwTvSJDW_NYR,lwTvCYR_NYR;
    private MasterplterEditController masterplterEditController;
    private TimePickerView timePicker;

    private LWTextView bzXHBAH;
    private LWTextView bzGYF;
    private LWTextView bzGYFDW;



    private ImageView ivQIANZHANG;//电子签章
    /**================================更多===================================*/
    private QMUIRoundButton btnMore;
    private static final int PDF = 0;
    private static final int MOBAN = 1;
    private static final int COMPANYID = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private LinearLayout llBack;
    private CheckForm masterpleForm = new CheckForm();
    /**
     * 多选
     */
    private QMUIPopup mNormalPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_masterple_form_default);
        initController();
        initView();
        initData();
    }

    @Override
    public void setUIController(MasterplterEditController sc) {

    }

    private void initController(){
        masterplterEditController= new MasterplterEditController();
        setUIController(masterplterEditController);
        masterplterEditController.setUI(this);
    }

    private void initView(){
        {
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
                    }
                }
            });

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
                    }
                }
            });
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
                                    }
                }
            });

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
                    }
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
                    }
                }
            });


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
                    }
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
                    }
                }
            });


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
                                     }
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
                                        }
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
                    }
                }
            });





            lwTvAcceptProduceInfoZSID = findViewById(R.id.lwtv_accept_produce_info_zs_id_in);
            lwTvAcceptProduceInfoZSID.setOnClickListener(this);
            lwTvAcceptProduceCPMC = findViewById(R.id.lwtv_accept_produce_info_cpmc_in);
            lwTvAcceptProduceCPMC.setOnClickListener(this);
            lwTvAcceptProduceGGXH = findViewById(R.id.lwtv_accept_produce_info_ggxh_in);
            lwTvAcceptProduceGGXH.setOnClickListener(this);
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

//            checkBoxPL = findViewById(R.id.cb_pl);
//            checkBoxJS = findViewById(R.id.cb_js);
//
//            checkBoxPL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        checkBoxJS.setChecked(false);
//                        upDataBean(R.id.cb_pl,ACCEPT_PL);
//                    }
//                }
//            });
//
//            checkBoxJS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        checkBoxPL.setChecked(false);
//                        upDataBean(R.id.cb_js,ACCEPT_JS);
//                    }
//                }
//            });

//
//            lwTvAcceptProducePLDW = findViewById(R.id.lwtv_accept_produce_info_pl);
//            lwTvAcceptProducePLDW.setOnClickListener(this);
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
    }

    private void initData(){
        String userid = AccountManager.getInstance().getUserId();
        String moudleType = masterplterEditController.getMoudleType();
        String moudleId = masterplterEditController.getMoudleId();
        masterplterEditController.requestTasksample(userid,moudleId,moudleType);
    }

    public void setData(Sampling sampling){
        if(sampling != null){
            lwTvCheckID.setText(checkNullString(sampling.getTaskcode()));//编号
            masterpleForm.setId(masterplterEditController.getMoudleId());
            masterpleForm.setTaskCode(sampling.getTaskcode());
            if(!TextUtils.isEmpty(sampling.getReportcode())){
                lwTvReportID.setText(sampling.getReportcode());
            }
            masterpleForm.setReportId(sampling.getReportcode());
            lwTvTaskFrom.setText(checkNullString(sampling.getTaskfrommean()));//任务来源
            masterpleForm.setTaskFrom(sampling.getTaskfrommean());
            masterpleForm.setTaskFromId(sampling.getTaskfrom());
            lwTvTaskClass.setText(checkNullString(sampling.getTasktypemean()));//任务类别
            masterpleForm.setTaskClass(sampling.getTasktypemean());
            masterpleForm.setTaskClassId(sampling.getTasktype());
            lwTvAcceptComName.setText(checkNullString(sampling.getInspectedname()));//受检单位名称和地址
            masterpleForm.setAcceptCompanyName(checkNullString(sampling.getInspectedname()));
            lwTvAcceptComAddres.setText(checkNullString(sampling.getInspectedaddress()));
            masterpleForm.setAcceptCompanyAddress(checkNullString(sampling.getInspectedaddress()));
            lwTvConnectName.setText(checkNullString(sampling.getInspectedman()));//受检单位联系人及电话
            masterpleForm.setAcceptCompanyConnectNanme(checkNullString(sampling.getInspectedman()));
            lwTvConnectTel.setText(checkNullString(sampling.getInspectedtel()));
            masterpleForm.setAcceptCompanyConnecTel(checkNullString(sampling.getInspectedtel()));
            lwTvFaRen.setText(checkNullString(sampling.getRepresentative()));//受检单位法人
            masterpleForm.setAcceptCompanyFaren(sampling.getRepresentative());
            lwTvProductName.setText(checkNullString(sampling.getProducename()));//生产单位名称
            masterpleForm.setProductCompanyName(sampling.getProducename());
            lwTvProductAddress.setText(checkNullString(sampling.getProduceaddress()));//生产单位地址
            masterpleForm.setProductCompanyAdress(sampling.getProduceaddress());
            lwTvProductEMSID.setText(checkNullString(sampling.getProducezipcode()));//生产单位邮政编码
            masterpleForm.setProductCompanyEMS(sampling.getProducezipcode());
            lwTvProdictFaRen.setText(checkNullString(sampling.getPrepresentative()));//生产单位法人
            masterpleForm.setProductCompanyFaren(sampling.getPrepresentative());
            lwTvProductTelName.setText(checkNullString(sampling.getProduceconman()));//生产单位联系人
            masterpleForm.setProductCompanyConnectName(sampling.getProduceconman());
            lwTvProductTleNum.setText(checkNullString(sampling.getProducetel()));//生产单位联系人电话
            masterpleForm.setProductCompanyConnectTEL(sampling.getProducetel());
            lwTvProductBusinessLicense.setText(checkNullString(sampling.getProducelicense()));//生产单位营业执照
            masterpleForm.setProductCompanyLicense(sampling.getProducelicense());
            lwTevProductID.setText(checkNullString(sampling.getProducecode()));//生产单位机构代码
            masterpleForm.setProductCompanyID(sampling.getProducecode());
            masterpleForm.setProductCompanyDimensionsCYRYS(checkNullString(sampling.getProducepcount()));
            lwTevProductCYRYS.setText(sampling.getProducepcount());
            masterpleForm.setProductCompanyDimensionsYYSR(checkNullString(sampling.getProduceoutput()));
            lwTevProductYYSR.setText(sampling.getProduceoutput());

//            lwTvAcceptProducePL.setText(sampling.getProductpl());
//            masterpleForm.setAcceptProducePL(checkNullString(sampling.getProductpl()));



            lwTvAcceptProducePL.setText(sampling.getSamplingbaseandbatch());
            masterpleForm.setAcceptProducePL(checkNullString(sampling.getSamplingbaseandbatch()));
            lwTvAcceptProduceDJ.setText(sampling.getUnitprice());
            masterpleForm.setAcceptProduceDJ(checkNullString(sampling.getUnitprice()));

            bzXHBAH.setText(sampling.getRecordnumber());
            masterpleForm.setBzXMBAH(checkNullString(sampling.getRecordnumber()));
            bzGYF.setText(sampling.getSamplecost());
            masterpleForm.setBzGYF(checkNullString(sampling.getSamplecost()));
            String ecType = sampling.getProducesamlltype();
            if(!TextUtils.isEmpty(ecType)){
                switch (ecType){
                    case PRODUCT_COMPANY_SMALL_TYPE_GY :
                        rb_GY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GY);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_SY :
                        rb_SY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_SY);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_JT :
                        rb_JT.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_JT);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_YXZRGS :
                        rb_YXZRGS.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_YXZRGS);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_LY:
                        rb_LY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_LY);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_GFYXGS :
                        rb_GFYXGS.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GFYXGS);
                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_GFHZ :
                        rb_GFHZ.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_GFHZ);
                        break;
                    case  PRODUCT_COMPANY_SMALL_TYPE_QTQY :
                        rb_QTQY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_QTQY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_HZJY :
                        rb_HZJY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_HZJY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_HZOJY :
                        rb_HZOJY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_HZOJY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_DZJY :
                        rb_GATDZJY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_DZJY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS :

                        rb_GATTZGFYXGS.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_TZGFYXGS);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_ZWHZ :
                        rb_ZWHZ.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_ZWHZ);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_ZWHZO :
                        rb_ZWHZO.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_ZWHZO);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_WZQY :
                        rb_WZQY.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_WZQY);

                        break;
                    case PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS :
                        rb_WSTZGFYXGS.setChecked(true);
                        masterpleForm.setProductCompanySmallType(PRODUCT_COMPANY_SMALL_TYPE_WSTZGFYXGS);
                        break;
                }
            }


            String type = sampling.getProductnumtype();
            if(!TextUtils.isEmpty(type)){
                switch (type){
                    case LINSENCE_TYPE_GYCPSCXKZ:
                        rbGYCPSCXKZ.setChecked(true);
                        masterpleForm.setProductCompanyLicense(LINSENCE_TYPE_GYCPSCXKZ);
                        break;
                    case LINSENCE_TYPE_SC:
                        rbSC.setChecked(true);
                        masterpleForm.setProductCompanyLicense(LINSENCE_TYPE_SC);
                        break;
                    case LINSENCE_TYPE_CCC:
                        rbCCC.setChecked(true);
                        masterpleForm.setProductCompanyLicense(LINSENCE_TYPE_CCC);
                        break;
                    case LINSENCE_TYPE_OTHERS:
                        rbOthers.setChecked(true);
                        masterpleForm.setProductCompanyLicense(LINSENCE_TYPE_OTHERS);
                        break;
                    case LINSENCE_TYPE_NONE:
                        rbGYCPSCXKZ.setChecked(false);
                        rbSC.setChecked(false);
                        rbCCC.setChecked(false);
                        rbOthers.setChecked(false);
                        masterpleForm.setProductCompanyLicense(LINSENCE_TYPE_NONE);
                        break;
                }
            }

//            String plType = sampling.getAcceptProType();
//            if(!TextUtils.isEmpty(plType)){
//                switch (plType){
//                    case ACCEPT_JS:
//                        checkBoxJS.setChecked(true);
//                        checkBoxPL.setChecked(false);
//                        masterpleForm.setAcceptProType(ACCEPT_JS);
//                        break;
//                    case ACCEPT_PL:
//                        checkBoxJS.setChecked(false);
//                        checkBoxPL.setChecked(true);
//                        masterpleForm.setAcceptProType(ACCEPT_PL);
//                        break;
//                }
//            }



            masterpleForm.setAcceptProduceLicenseType(sampling.getProductnumtype());
            lwTvAcceptProduceInfoZSID.setText(checkNullString(sampling.getProductnum()));//证书编号
            masterpleForm.setAcceptProduceLicenseId(sampling.getProductnum());
            lwTvAcceptProduceCPMC.setText(checkNullString(sampling.getProductname()));//产品名称
            masterpleForm.setAcceptProduceCPMC(sampling.getProductname());
            lwTvAcceptProduceGGXH.setText(checkNullString(sampling.getProductmodel()));//规格型号
            masterpleForm.setAcceptProduceGGCH(sampling.getProductmodel());
            lwTvAcceptProduceSCRI.setText(checkNullString(sampling.getProductbnum()));//生产日期/批号
            masterpleForm.setAcceptProduceSCRQ(sampling.getProductbnum());
            lwTvAcceptProduceCPDJ.setText(checkNullString(sampling.getProductlevel()));//产品等级
            masterpleForm.setAcceptProduceCPDJ(sampling.getProductlevel());
            lwTvAcceptProduceSB.setText(checkNullString(sampling.getProductmark()));//商标
            masterpleForm.setAcceptProduceSB(sampling.getProductmark());
            lwTvAcceptProduceCYSL.setText(checkNullString(sampling.getSamplingcount()));//抽样数量
            masterpleForm.setAcceptProduceCYSL(sampling.getSamplingcount());

            lwTvAcceptProduceBZZXBZ.setText(checkNullString(sampling.getDostandard()));//标注执行标准，技术文件
            masterpleForm.setAcceptProduceBZZXBZ(sampling.getDostandard());
            lwTvAcceptProduceCYRQ.setText(checkNullString(sampling.getDotime()));
            masterpleForm.setAcceptProduceCYRQ(sampling.getDotime());
            lwTvAcceptProduceFYZT.setText(checkNullString(sampling.getEncapsulationstate()));//封样状态
            masterpleForm.setAcceptProduceFYZT(sampling.getEncapsulationstate());

            lwTvAcceptProduceJSYDD.setText(checkNullString(sampling.getSendaddress()));//寄送地址
            masterpleForm.setAcceptProduceJSYDD(sampling.getSendaddress());
            lwTvAcceptProduceBYLJFCDD.setText(checkNullString(sampling.getRvandfc()));//封存地点
            masterpleForm.setAcceptProduceBYLFCDD(sampling.getRvandfc());


            lwTvAcceptProduceJSYJZRQ.setText(checkNullString(sampling.getEndtime()));
            masterpleForm.setAcceptProduceJSYJZRQ(checkNullString(sampling.getEndtime()));
            //抽样单位如果取不到数据就显示默认数据
//            if(!TextUtils.isEmpty(sampling.getSammanname())){//服务端存的的是单位的id，界面需要显示id对应的名称
//
//
//            }
            if(!TextUtils.isEmpty(sampling.getSamplingnamemean())){
                lwTvSampleCompanyName.setText(sampling.getSamplingnamemean());
                masterpleForm.setCheckCompanyName(sampling.getSamplingnamemean());
                masterpleForm.setCheckCompanyNameId(sampling.getSammanname());
            }
            if(!TextUtils.isEmpty(sampling.getSamplingaddress())){
                lwTvSampleCompanyAddress.setText(checkNullString(sampling.getSamplingaddress()));
                masterpleForm.setCheckCompanyAddress(sampling.getSamplingaddress());
            }else{
                lwTvSampleCompanyAddress.setText(CheckCompany.companyAddress);
                masterpleForm.setCheckCompanyAddress(sampling.getSamplingaddress());
            }
            if(!TextUtils.isEmpty(sampling.getSamplingcode())){
                lwTvSampleCompanyEMSID.setText(checkNullString(sampling.getSamplingcode()));
                masterpleForm.setCheckCompanyEMS(sampling.getSamplingcode());
            }else{
                lwTvSampleCompanyEMSID.setText(CheckCompany.EMS);
                masterpleForm.setCheckCompanyEMS(CheckCompany.EMS);
            }
           if(!TextUtils.isEmpty(sampling.getSamplingman())){
               lwTvSampleCompanyConnectName.setText(checkNullString(sampling.getSamplingman()));
               masterpleForm.setCheckCompanyConnectName(sampling.getSamplingman());
           }else{
               lwTvSampleCompanyConnectName.setText(CheckCompany.connectName);
               masterpleForm.setCheckCompanyConnectName(CheckCompany.connectName);
           }
            if(!TextUtils.isEmpty(sampling.getSamplingtel())){
                lwTvSampleCompanyConnectTel.setText(checkNullString(sampling.getSamplingtel()));
                masterpleForm.setCheckCompanyConnectTel(sampling.getSamplingtel());
            }else{
                lwTvSampleCompanyConnectTel.setText(CheckCompany.connectTel);
                masterpleForm.setCheckCompanyConnectTel(CheckCompany.connectTel);
            }
            if(!TextUtils.isEmpty(sampling.getSamplingemail())){
                lwTvSampleCompanyConnectEmail.setText(checkNullString(sampling.getSamplingemail()));
                masterpleForm.setCheckCompanyEmail(sampling.getSamplingemail());
            }else{
                lwTvSampleCompanyConnectEmail.setText(CheckCompany.connectEmail);
                masterpleForm.setCheckCompanyEmail(CheckCompany.connectEmail);
            }

            //备注
            lwTvBz.setText(sampling.getRemark());
            masterpleForm.setBzInfo(sampling.getRemark());




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
                    masterpleForm.setAcceptProduceCYRQ(fillInDate);
                }
            } catch (Exception e) {
                SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
                Date nowDate = new Date();
                String fillInDate =  nowFormat.format(nowDate);
                if(!TextUtils.isEmpty(fillInDate)){
                    String date[] = fillInDate.split("-");
                    lwTvCYR_NYR.setText(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                    lwTvAcceptProduceCYRQ.setText(fillInDate);
                    masterpleForm.setAcceptProduceCYRQ(fillInDate);

                }
            }

        }
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
                    masterpleForm.setProductCompanyEMS(listItems[i]);
                }else{
                    showEditTextDialog(textView,"邮政编码");
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


    /**
     * 营业收入单位
     */
    private void initCheckProduceYYSRDW(TextView textView){
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
                lwTevProductYYSR.setText(lwTevProductYYSR.getText().toString() + listItems[i]);
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
                .dimAmount(0.6f)
                .shadow(true)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//
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
                    masterpleForm.setAcceptProduceSB(listItems[i]);
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
//                    masterpleForm.setAcceptProduceSB(listItems[i]);
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
                    masterpleForm.setAcceptProduceBYLFCDD(listItems[i]);
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
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i != 5){//不是点击其他
//                    textView.setText(listItems[i]);
//                    masterpleForm.setAcceptProduceBYLFCDD(listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"封存地点");
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
                    masterpleForm.setAcceptProduceCPDJ(listItems[i]);
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
//                    masterpleForm.setAcceptProduceCPDJ(listItems[i]);
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
     * 抽样批量单位
     */
    private void initCheckProduceCYPLDW(TextView textView){
        String [] listItems = new String[]{
                "m",
                "㎡",
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
                lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
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
//                lwTvAcceptProducePL.setText(lwTvAcceptProducePL.getText().toString() +listItems[i]);
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
     * 单价单位
     */
    private void initCheckProduceDJDW(TextView textView){
        String [] listItems = new String[]{
                "m",
                "㎡",
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
                lwTvAcceptProduceDJ.setText(lwTvAcceptProduceDJ.getText().toString() + listItems[i]);
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
//                lwTvAcceptProduceDJ.setText(lwTvAcceptProduceDJ.getText().toString() + listItems[i]);
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
                    masterpleForm.setAcceptProduceJSYDD(listItems[i]);
                }else{
                    showEditTextDialog(textView,"输入寄送地址");
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
//                    masterpleForm.setAcceptProduceJSYDD(listItems[i]);
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
                    masterpleForm.setAcceptProduceJSYJZRQ(listItems[i]);
                }else{
                    showTimePicker(lwTvAcceptProduceJSYJZRQ);
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
//                    masterpleForm.setAcceptProduceJSYJZRQ(listItems[i]);
//                }else{
//                    showTimePicker(lwTvAcceptProduceJSYJZRQ);
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
     * 抽样数量单位
     */
    private void initCheckProduceCYSlDW(TextView textView){
        String [] listItems = new String[]{
                "m",
                "㎡",
                "㎥",
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
                lwTvAcceptProduceCYSL.setText(lwTvAcceptProduceCYSL.getText().toString() +listItems[i]);
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
//                lwTvAcceptProduceCYSL.setText(lwTvAcceptProduceCYSL.getText().toString() +listItems[i]);
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



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lwtv_id:
                showEditTextDialog(lwTvCheckID,"在此输入编号");
                break;
            case R.id.lwtv_descrable02:
                showEditTextDialog(lwTvReportID,"在此输入报告编号");
                break;
            case R.id.lwtv_task_from://任务来源
                masterplterEditController.requestTaskCompanyInfo(masterplterEditController.TYPE_TASK_FROM);
                break;
            case R.id.lwtv_task_class://任务类别
                masterplterEditController.requestTaskCompanyInfo(masterplterEditController.TYPE_TASK_CALSS);
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
            case R.id.lwtv_accept_produce_info_cpmc_in://产品名称
                showEditTextDialog(lwTvAcceptProduceCPMC,"在此输入产品名称");
                break;

            case R.id.lwtv_accept_produce_info_ggxh_in://规格型号

                showEditTextDialog(lwTvAcceptProduceGGXH,"在此输入规格型号");

                break;
            case R.id.lwtv_accept_produce_info_scrqph_in://生产日期/批号
                showEditTextDialog(lwTvAcceptProduceSCRI,"在此输入生产日期/批号");
                break;
            case R.id.lwtv_accept_produce_info_sb_in://商标

                initCheckProduceSB(lwTvAcceptProduceSB);
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
                showTimePicker(lwTvAcceptProduceCYRQ);
                break;

            case R.id.lwtv_accept_produce_info_pl_in:
                showEditTextDialog(lwTvAcceptProducePL,"抽样基数/批量");
                break;
//            case R.id.lwtv_accept_produce_info_pl:
//                initCheckProduceCYPLDW(lwTvAcceptProducePLDW);
//
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
                masterplterEditController.requestTaskCompanyInfo(MasterplterEditController.TYPE_CYDY_NAME);
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
                Toast.makeText(MasterplterEditActivity.this,"模版功能暂不支持签字",Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_sjdw_qz://受检单位签字
                Toast.makeText(MasterplterEditActivity.this,"模版功能暂不支持签字",Toast.LENGTH_SHORT).show();

                break;
            case R.id.ll_cyr_qz://抽样人
                Toast.makeText(MasterplterEditActivity.this,"模版功能暂不支持签字",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_more:
                new QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("模版信息")
                        .setMessage("确定要提交吗？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "提交", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                masterpleForm.setBzInfo(lwTvBz.getText().toString().trim());
                                masterplterEditController.postSampling(masterpleForm);
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;

        }
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

    private void upDataBean(int viewId ,String text){
        if(masterpleForm == null){return;}
        switch (viewId){
            case R.id.lwtv_report_id://报告编号
                masterpleForm.setReportId(text);
            case R.id.lwtv_accept_com_desc_name_in://通讯地址
                masterpleForm.setAcceptCompanyName(text);
                break;
            case R.id.lwtv_accept_com_desc_address_in://通讯地址
                masterpleForm.setAcceptCompanyAddress(text);
                break;
            case R.id.lwtv_descrable_faren_in://法人代表
                masterpleForm.setAcceptCompanyFaren(text);
                break;
            case R.id.lwtv_connect_name_in://联系
                masterpleForm.setAcceptCompanyConnectNanme(text);
                break;
            case R.id.lwtv_connect_tel_in://联系fangshi
                masterpleForm.setAcceptCompanyConnecTel(text);
                break;
            case R.id.lwtv_product_com_name_in://单位名称
                masterpleForm.setProductCompanyName(text);
                break;
            case R.id.lwtv_product_com_address_in://单位地址
                masterpleForm.setProductCompanyAdress(text);
                break;
            case R.id.lwtv_product_com_ems_id_in://邮政编码
                masterpleForm.setProductCompanyEMS(text);
                break;
            case R.id.lwtv_product_com_faren_in://法人代表
                masterpleForm.setProductCompanyFaren(text);
                break;
            case R.id.lwtv_product_com_tel_name_in://联系人
                masterpleForm.setProductCompanyConnectName(text);
                break;
            case R.id.lwtv_product_com_tel_num_in://联系电话
                masterpleForm.setProductCompanyConnectTEL(text);
                break;
            case R.id.lwtv_product_com_business_license_in://营业执照
                masterpleForm.setProductCompanyLicense(text);
                break;
            case R.id.lwtv_product_com_id_in://机构代码
                masterpleForm.setProductCompanyID(text);
                break;

            case R.id.lwtv_product_com_cyrys_in://从业人员数
                masterpleForm.setProductCompanyDimensionsCYRYS(text);
                break;
            case R.id.lwtv_product_com_yysr_in://营业收入
                masterpleForm.setProductCompanyDimensionsYYSR(text);
                break;


            case R.id.rb_gv:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_person:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_group:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_youxian:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_lianying:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gufen:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gufenhz:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_others:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_hzjy:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_h_zuo_jy:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gatdzjy:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gattzgfyxgs:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_zwhz:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_zwhzhuo:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_wzqy:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_wstzgfyxgs:
                masterpleForm.setProductCompanySmallType(text);
                break;
            case R.id.rb_gycpscxkz:
                masterpleForm.setAcceptProduceLicenseType(text);
                break;
            case R.id.rb_product_info_sc:
                masterpleForm.setAcceptProduceLicenseType(text);
                break;
            case R.id.rb_product_info_ccc:
                masterpleForm.setAcceptProduceLicenseType(text);
                break;
            case R.id.rb_product_info_others:
                masterpleForm.setAcceptProduceLicenseType(text);
                break;
//            case R.id.cb_pl:
//                masterpleForm.setAcceptProType(ACCEPT_PL);
//                break;
//            case R.id.cb_js:
//                masterpleForm.setAcceptProType(ACCEPT_JS);
//                break;
            case R.id.lwtv_accept_produce_info_zs_id_in://证书编号
                //校验证书id是否正确
                if(rbGYCPSCXKZ.isChecked() && verifyGYSCXKZ(text)){
                    masterpleForm.setAcceptProduceLicenseId(text);
                }else if(rbSC.isChecked() && verifySC(text)){
                    masterpleForm.setAcceptProduceLicenseId(text);
                }else if(rbCCC.isChecked() && verifyCCC(text)){
                    masterpleForm.setAcceptProduceLicenseId(text);
                }else if(rbOthers.isChecked()){
                    masterpleForm.setAcceptProduceLicenseId(text);
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
                                    masterpleForm.setAcceptProduceLicenseId(text);
                                }
                            })
                            .create().show();
                }

                break;
            case R.id.lwtv_accept_produce_info_cpmc_in://产品名称
                masterpleForm.setAcceptProduceCPMC(text);
                break;
            case R.id.lwtv_accept_produce_info_ggxh_in://规格型号
                masterpleForm.setAcceptProduceGGCH(text);
                break;
            case R.id.lwtv_accept_produce_info_scrqph_in://生产日期/批号
                masterpleForm.setAcceptProduceSCRQ(text);
                break;
            case R.id.lwtv_accept_produce_info_sb_in://商标
                masterpleForm.setAcceptProduceSB(text);
                break;
            case R.id.lwtv_accept_produce_info_cysl_in://抽样数量
                masterpleForm.setAcceptProduceCYSL(text);
                break;
            case R.id.lwtv_accept_produce_info_cpdj_in://产品等级
                masterpleForm.setAcceptProduceCPDJ(text);
                break;

            case R.id.lwtv_accept_produce_info_bjzxbzjswj_in://标注执行标准/技术文件
                masterpleForm.setAcceptProduceBZZXBZ(text);
                break;

            case R.id.lwtv_accept_produce_info_pl_in:
                masterpleForm.setAcceptProducePL(text);
                break;

            case R.id.lwtv_accept_produce_info_dj_in:
                masterpleForm.setAcceptProduceDJ(text);
                break;

            case R.id.lwtv_accept_produce_info_byljfcdd_in://封存地点
                masterpleForm.setAcceptProduceBYLFCDD(text);
                break;
            case R.id.lwtv_accept_produce_info_fyzt_in://封样状态
                masterpleForm.setAcceptProduceFYZT(text);

                break;
            case R.id.lwtv_accept_produce_info_jsydd_in://寄送样地点
                masterpleForm.setAcceptProduceJSYDD(text);
                break;
            case R.id.lwtv_sample_company_info_name_in://抽样单位名称
                masterplterEditController.requestTaskCompanyInfo(MasterplterEditController.TYPE_CYDY_NAME);
                break;

            case R.id.lwtv_sample_company_info_address_in://抽样单位地址
                masterpleForm.setCheckCompanyAddress(text);
                break;

            case R.id.lwtv_sample_company_info_emss_in://抽样单位邮政编码
                masterpleForm.setCheckCompanyEMS(text);
                break;

            case R.id.lwtv_sample_company_info_lxr_in://抽样单位联系人
                masterpleForm.setCheckCompanyConnectName(text);
                break;
            case R.id.lwtv_sample_company_info_lx_tel_in://抽样单位联系人电话
                masterpleForm.setCheckCompanyConnectTel(text);

                break;
            case R.id.lwtv_sample_company_info_cz_email_in://抽样单位Email
                masterpleForm.setCheckCompanyEmail(text);
                break;

            case R.id.lwtv_bz_in://备注说明
                masterpleForm.setBzInfo(text);
                break;
            case R.id.lwtv_bz_xmbah_in://项目备案号
                masterpleForm.setBzXMBAH(text);
                break;
            case R.id.lwtv_bz_gyf_in:
                masterpleForm.setBzGYF(text);
                break;
        }
    }

    private String checkNullString(String s){
        if(TextUtils.isEmpty(s)) {
            return "";
        }else {
            return s;
        }
    }
    private void showEditTextDialog(TextView textView,String string){
        String deafultInputText = "";
        if(textView.getId() == R.id.lwtv_accept_produce_info_scrqph_in){
            if(!TextUtils.isEmpty(textView.getText().toString())){
                deafultInputText = textView.getText().toString();
            }else {
                deafultInputText = "/";
            }

        }else{
            deafultInputText = textView.getText().toString();
        }


        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("信息录入")
                .setPlaceholder(string)
                .setDefaultText(deafultInputText)
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
                            textView.setText(text);
                            upDataBean(textView.getId(),text.toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MasterplterEditActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }
    /**
     * 时间选择器
     */
    private void showTimePicker(TextView view){
        initTimePick(view);
        timePicker.show(view);
    }
    /**
     * 初始化时间选择空间
     */
    private void initTimePick(TextView view){
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2015,0,1);
        endDate.set(2029,11,31);
        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String string  = getTimes(date,0);
                view.setText(string);
                switch (v.getId()){
                    case R.id.lwtv_accept_produce_info_cyrq_in://抽样日期
                        masterpleForm.setAcceptProduceCYRQ(string);
                        break;
                    case R.id.lwtv_accept_produce_info_jsyjzrqt_in://寄送样截至日期
                        masterpleForm.setAcceptProduceJSYJZRQ(string);
                        break;
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
                .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();
    }

//    private void showTimePicker(int type){
//        Calendar selectedDate = Calendar.getInstance();
//        Calendar startDate = Calendar.getInstance();
//        Calendar endDate = Calendar.getInstance();
//        startDate.set(2015,0,1);
//        endDate.set(2029,11,31);
//        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date, View v) {
//                String dates[] = getTimes(date,type).split("-");
//                lwTvSJDW_NYR.setText(dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日");
//                lwTvSCDW_NYR.setText(dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日");
//                lwTvCYR_NYR.setText(dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日");
//                masterpleForm.setCyDwRQ((dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日"));
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
    private String getTimes(Date date,int type) {//可根据需要自行截取数据显示
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        return format.format(date);
    }


    /**
     * 备样量存放地点
     */
//    private void initCheckProduceCFDD(TextView textView){
//        String [] listItems = new String[]{
//                "受检单位",
//                "抽样单位",
//                "检验单位",
//                "当地市场监管部门",
//                "其他"
//        };
//
//        List<String> data = new ArrayList<>();
//        Collections.addAll(data, listItems);
//        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i != 4){//不是点击其他
//                    textView.setText(listItems[i]);
//                    upDataBean(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"在此封存地点");
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
//
//    }

    /**
     * 备样量存放地点
     */
    private void initCheckProduceFYZT(TextView textView){
        String [] listItems = new String[]{
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
                if(i != 4){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBean(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"输入样状态");
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
//                    upDataBean(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"输入样状态");
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

    public void showPopView(View v,List<TaskCompany> list){
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
                switch (v.getId()){
                    case R.id.lwtv_task_from:
                        masterpleForm.setTaskFrom(data.get(i).getName());
                        masterpleForm.setTaskFromId(data.get(i).getId());
                        lwTvTaskFrom.setText(data.get(i).getName());
                        break;
                    case R.id.lwtv_task_class:
                        masterpleForm.setTaskClass(data.get(i).getName());
                        masterpleForm.setTaskClassId(data.get(i).getId());
                        lwTvTaskClass.setText(data.get(i).getName());
                        break;
                    case R.id.lwtv_sample_company_info_name_in:
                        masterpleForm.setCheckCompanyName(data.get(i).getName());
                        masterpleForm.setCheckCompanyNameId(data.get(i).getId());
                        lwTvSampleCompanyName.setText(data.get(i).getName());
                        break;

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
                .show(v);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 300), QMUIDisplayHelper.dp2px(this, 900), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                switch (v.getId()){
//                    case R.id.lwtv_task_from:
//                        masterpleForm.setTaskFrom(data.get(i).getName());
//                        masterpleForm.setTaskFromId(data.get(i).getId());
//                        lwTvTaskFrom.setText(data.get(i).getName());
//                        break;
//                    case R.id.lwtv_task_class:
//                        masterpleForm.setTaskClass(data.get(i).getName());
//                        masterpleForm.setTaskClassId(data.get(i).getId());
//                        lwTvTaskClass.setText(data.get(i).getName());
//                        break;
//                    case R.id.lwtv_sample_company_info_name_in:
//                        masterpleForm.setCheckCompanyName(data.get(i).getName());
//                        masterpleForm.setCheckCompanyNameId(data.get(i).getId());
//                        lwTvSampleCompanyName.setText(data.get(i).getName());
//                        break;
//
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
//        mListPopup.show(v);
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



    public void showSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("提交成功")
                .create();
        sucDialog.show();


        btnMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                sucDialog.dismiss();
            }
        },1500);

    }
    public void showFail(){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("提交失败")
                .create();
        failDialog.show();


        btnMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }


    private static class CheckCompany{
        public static String company = "湖南省产商品质量监督检验研究院";
        public static String companyId = "1001";
        public static String companyAddress = "湖南省长沙市新建西路189号";
        public static String EMS = "410007";
        public static String connectName = "康峰";
        public static String connectTel = "0731-89775245";
        public static String connectEmail = "0731-89775242";
    }

}
