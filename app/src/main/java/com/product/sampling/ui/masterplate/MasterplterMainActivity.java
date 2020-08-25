package com.product.sampling.ui.masterplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.product.sampling.R;
import com.product.sampling.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by 陆伟 on 2019/11/18.
 * Copyright (c) 2019 . All rights reserved.
 */


public class MasterplterMainActivity extends BaseActivity<MasterplterMainController> {
    private static final String TAG = "MasterplterMainActivity";
    public static final String REPORT_TYPE = "reporttype";
    //refuse 拒检单
    public static final String REPORT_TYPE_REFUSE = "refuse";
    //feed 反馈单
    public static final String REPORT_TYPE_FEED = "feed";
    //unfind 未抽到样单
    public static final String REPORT_TYPE_UNFIND = "unfind";
    //sampling 抽样单
    public static final String REPORT_TYPE_SAMPLIG = "sampling";
    //advice 处置单
    public static final String REPORT_TYPE_ADVICE = "advice";
    //risk 风险单
    public static final String REPORT_TYPE_RISK = "risk";
    //work 工作单
    public static final String REPORT_TYPE_WORK = "work";

//    @BindView(R.id.tv_check_or_recheck)
//    TextView checkOrRecheck;

    Toolbar toolbar;
    LinearLayout llBack;

    private MasterplterMainController masterplterMainController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_masterplate, null);
        setContentView(root);
//        setContentView(R.layout.activity_masterplate);
        ButterKnife.bind(this);
        findToolBar();
        initController();

    }

    public void findToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            llBack = findViewById(R.id.ll_back);
            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    private void initController(){
        masterplterMainController = new MasterplterMainController();
        setUIController(masterplterMainController);
        masterplterMainController.setUI(this);
    }

    @Override
    public void setUIController(MasterplterMainController sc) {
        masterplterMainController = sc;
    }



    @OnClick(R.id.tv_check_or_recheck)
    public void toSampilg(){
        toMasterpleterList(REPORT_TYPE_SAMPLIG);
    }
    @OnClick(R.id.tv_not_check)
    public void toNotCheck(){
        Toast.makeText(this,"业务开发中...",Toast.LENGTH_LONG).show();
//        toMasterpleterList(REPORT_TYPE_UNFIND);
    }
    @OnClick(R.id.tv_refuse)
    public void toRefuse(){
        Toast.makeText(this,"业务开发中...",Toast.LENGTH_LONG).show();
//        toMasterpleterList(REPORT_TYPE_REFUSE);
    }

    private void toMasterpleterList(String type){
        Intent intent = new Intent(this, MasterplterListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(REPORT_TYPE,type);
        intent.putExtras(bundle);
        startActivity(intent);
    }





}
