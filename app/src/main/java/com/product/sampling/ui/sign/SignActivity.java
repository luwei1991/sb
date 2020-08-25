package com.product.sampling.ui.sign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.product.sampling.R;

import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.product.sampling.ui.sign.SignActivityController.IMAGE_SUCCESS_RESULT_CODE;
import static com.product.sampling.ui.sign.SignActivityController.RESULT_IMAGE_PATH;
import static com.product.sampling.ui.sign.SignActivityController.RESULT_IMAGE_TYPE;

/**
 * Created by 陆伟 on 2019/11/25.
 * Copyright (c) 2019 . All rights reserved.
 */


public class SignActivity extends Activity {
    private static final String TAG = "SignActivity";
    private SignActivityController signActivityController;
    /**
     * 设置签字笔粗度
     */
    private static final float PEN_SIZE = 12;
    /**
     * 是否为单个签名，默认是单个
     */
    protected boolean isOneSign = true;
    @BindView(R.id.sp_sign_01)
    SignaturePad signView01;
    @BindView(R.id.clear_01)
    TextView btnClear01;
    @BindView(R.id.sp_sign_02)
    SignaturePad signView02;
    @BindView(R.id.clear_02)
    TextView btnClear02;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.rl_sign_2)
    RelativeLayout rlSign_2;
    @BindView(R.id.view_border)
    View view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        initController();
        initView();
    }

    private void initController(){
        signActivityController = new SignActivityController();
        setUIController(signActivityController);
        signActivityController.setUI(this);
    }

    private void initView(){
        signView01.setMinWidth(PEN_SIZE);
        if(!isOneSign){
            rlSign_2.setVisibility(View.VISIBLE);
            signView02.setMinWidth(PEN_SIZE);
            view.setVisibility(View.VISIBLE);
        }

    }


    public void setUIController(SignActivityController sc) {
            signActivityController = sc;
    }

    @OnClick(R.id.clear_01)
    public void clear01(){
        signView01.clear();
    }

    @OnClick(R.id.clear_02)
    public void clear02(){
        signView02.clear();
    }

    @OnClick(R.id.iv_close)
    public void close(){
        finish();
    }

    @OnClick(R.id.sign_sure)
    public void sure(){
        if(isOneSign){//单个签名
            if(!signView01.isEmpty()){
                Bitmap bitmap01 = signView01.getTransparentSignatureBitmap();
                signActivityController.saveSignImage(signActivityController.imgUrl,bitmap01,signActivityController.getImgType());
            }

        }else{//2个签名
            Bitmap bitmap01 = signView01.getTransparentSignatureBitmap();
            Bitmap bitmap02 = signView02.getTransparentSignatureBitmap();
            new Thread(){
                @Override
                public void run() {
                    if(!signView01.isEmpty()){
                        try {
                            FileOutputStream fout_1 = new FileOutputStream(signActivityController.imgUrl_1);
                            bitmap01.compress(Bitmap.CompressFormat.PNG,100,fout_1);
                            fout_1.flush();
                            fout_1.close();
                            signActivityController.updateImgPath(signActivityController.imgUrl_1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if(!signView02.isEmpty()){
                        try {
                            FileOutputStream fout_2 = new FileOutputStream(signActivityController.imgUrl_2);
                            bitmap02.compress(Bitmap.CompressFormat.PNG,100,fout_2);
                            fout_2.flush();
                            fout_2.close();
                            signActivityController.updateImgPath(signActivityController.imgUrl_2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(RESULT_IMAGE_PATH,"");
                    bundle.putString(RESULT_IMAGE_TYPE,signActivityController.getImgType());
                    intent.putExtras(bundle);
                    setResult(IMAGE_SUCCESS_RESULT_CODE,intent);
                    finish();

                }
            }.start();
        }

    }


}
