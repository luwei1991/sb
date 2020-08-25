package com.product.sampling.ui.sign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.product.sampling.Constants;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.db.tables.HandleFormDao;
import com.product.sampling.db.tables.NotCheckForm;
import com.product.sampling.db.tables.NotCheckFormDao;
import com.product.sampling.db.tables.RefuseForm;
import com.product.sampling.db.tables.RefuseFormDao;
import com.product.sampling.ui.base.BaseUIController;
import com.product.sampling.ui.form.bean.SignBean;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 陆伟 on 2019/11/25.
 * Copyright (c) 2019 . All rights reserved.
 */


public class SignActivityController extends BaseUIController<SignActivity> {
    private static final String TAG = "SignActivityController";
    /**对象标记*/
    public static final String SIGN_TAG = "SIGNBEAN";
    /**结果返回图片类型*/
    public static final String RESULT_IMAGE_TYPE = "RESULT_IMAGE_TYPE";
    public static final String RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH";
    /***抽样单签名标签*/
    public static final String IMAGE_TYPE_CHECK_CYR = "IMAGE_TYPE_CHECK_CYR";//抽样人
    public static final String IMAGE_TYPE_CHECK_SJDW = "IMAGE_TYPE_CHECK_SJDW";//受检单位
    public static final String IMAGE_TYPE_CHECK_SCDW = "IMAGE_TYPE_CHECK_SCDW";//生产单位
    /**处置单签名标签*/
    public static final String IMAGE_TYPE_HANDLE_SJDW = "IMAGE_TYPE_HANDLE_SJDW";//受检单位
    public static final String IMAGE_TYPE_HANDLE_CYR = "IMAGE_TYPE_HANDLE_CYR";//抽样人
    /**未抽到样签名标签*/
    public static final String IMAGE_TYPE_NOT_CHECK_SJDW = "IMAGE_TYPE_NOT_CHECK_SJDW";//受检单位
    public static final String IMAGE_TYPE_NOT_CHECK_CYR = "IMAGE_TYPE_NOT_CHECK_CYR";//抽样人
    /**拒检单签名标签*/
    public static final String IMAGE_TYPE_REFUSE_SJDW = "IMAGE_TYPE_REFUSE_SJDW";//受检单位
    public static final String IMAGE_TYPE_REFUSE_CYR = "IMAGE_TYPE_REFUSE_CYR";//抽样人



    public static final int IMAGE_SUCCESS_RESULT_CODE = 0x07;
    private SignActivity signActivity;
    private String imgType;
    private String id;
    public String imgUrl = "";
    public String imgUrl_1 = "";
    public String imgUrl_2 = "";



    @Override
    public void setUI(SignActivity sc) {
            signActivity = sc;
            getIntent();
    }

    private void getIntent(){
        SignBean signBean = (SignBean) signActivity.getIntent().getSerializableExtra(SIGN_TAG);
        imgType = signBean.getImgType();
        id = signBean.getId();
        if(imgType.endsWith("CYR")){
            signActivity.isOneSign = false;
            imgUrl = getImagePath(imgType,id);
            imgUrl_1 = getImagePath(imgType +"-1",id);
            imgUrl_2 = getImagePath(imgType +"-2",id);
        }else{
            signActivity.isOneSign = true;
            imgUrl = getImagePath(imgType,id);
        }

    }

    /**
     * 生成Image文件名和地址路径
     * @param imgType
     * @param id
     * @return
     */
    private String getImagePath(String imgType,String id){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String dateString = formatter.format(currentTime);
        String imgDirPath = Constants.SIGN_IMAGE_PATH;
        File file = new File(imgDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return imgDirPath +"/" + dateString +"-" + id + "-" + imgType + ".png";
    }
//    /**
//     * 生成Image文件名和地址路径
//     * @return
//     */
//    public String[] getImagePath(){
//        if(!TextUtils.isEmpty(imgUrl_2)){
//            return new String[]{imgUrl,imgUrl_1,imgUrl_2};
//        }else {
//            return new String[]{imgUrl};
//        }
//
//    }
//


    public String getImgType() {
        return imgType;
    }

    public String getId() {
        return id;
    }

    public void saveSignImage(String imgPath, Bitmap bitmap, String imgType){
        Observable.just(imgPath)
                .observeOn(Schedulers.computation())
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        FileOutputStream fout = new FileOutputStream(imgPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,fout);
                        fout.flush();
                        File imageFile = new File(imgPath);
                        return imageFile;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(File file) {

                    }


                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString(RESULT_IMAGE_PATH,imgPath);
                        bundle.putString(RESULT_IMAGE_TYPE,imgType);
                        updateImgPath(imgPath);
                        intent.putExtras(bundle);
                        signActivity.setResult(IMAGE_SUCCESS_RESULT_CODE,intent);
                        signActivity.finish();
                    }
                });

    }




    /**
     * 将图像地址更新到本地数据库
     * @param imgPath
     */
    public void updateImgPath(String imgPath){
        switch (imgType){
            case IMAGE_TYPE_CHECK_CYR:
                CheckForm checkFormCYR = DBManagerFactory.getInstance().getCheckFormManager().queryBuilder().where(CheckFormDao.Properties.Id.eq(id)).build().unique();
                if(checkFormCYR != null){
                    if(imgPath.endsWith("1.png")){
                        checkFormCYR.setCheckPeopleSignImagePath_1(imgPath);
                    }else if(imgPath.endsWith("2.png")) {
                        checkFormCYR.setCheckPeopleSignImagePath_2(imgPath);
                    }
                    DBManagerFactory.getInstance().getCheckFormManager().update(checkFormCYR);
                }

                break;
            case IMAGE_TYPE_CHECK_SJDW:
                CheckForm checkFormSJDW = DBManagerFactory.getInstance().getCheckFormManager().queryBuilder().where(CheckFormDao.Properties.Id.eq(id)).build().unique();
                if(checkFormSJDW != null){
                    checkFormSJDW.setAcceptCompanySignImagePath(imgPath);
                    DBManagerFactory.getInstance().getCheckFormManager().update(checkFormSJDW);
                }

                break;
            case IMAGE_TYPE_CHECK_SCDW:
                CheckForm checkFormSCDW = DBManagerFactory.getInstance().getCheckFormManager().queryBuilder().where(CheckFormDao.Properties.Id.eq(id)).build().unique();
                if(checkFormSCDW != null){
                    checkFormSCDW.setProductCompanySignImagePath(imgPath);
                    DBManagerFactory.getInstance().getCheckFormManager().update(checkFormSCDW);
                }

                break;
            case IMAGE_TYPE_HANDLE_CYR:
                HandleForm handleFormCYR = DBManagerFactory.getInstance().getHandleManager().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(id)).build().unique();
                if(handleFormCYR != null){
                    if(imgPath.endsWith("1.png")){
                        handleFormCYR.setCyrImg_1(imgPath);
                    }else if(imgPath.endsWith("2.png")) {
                        handleFormCYR.setCyrImg_2(imgPath);
                    }
                    DBManagerFactory.getInstance().getHandleManager().update(handleFormCYR);
                }



                break;
            case IMAGE_TYPE_HANDLE_SJDW:
                HandleForm handleFormSJDW = DBManagerFactory.getInstance().getHandleManager().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(id)).build().unique();
                if(handleFormSJDW == null){
                    return;
                }
                handleFormSJDW.setSjdwImg(imgPath);
                DBManagerFactory.getInstance().getHandleManager().update(handleFormSJDW);
                break;
            case IMAGE_TYPE_NOT_CHECK_CYR:
                NotCheckForm notCheckFormCYR = DBManagerFactory.getInstance().getNotCheckFormManager().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(id)).build().unique();
                if(notCheckFormCYR == null){
                    return;
                }
                if(imgPath.endsWith("1.png")){
                    notCheckFormCYR.setCyrQZ_1(imgPath);
                }else if(imgPath.contains("2.png")) {
                    notCheckFormCYR.setCyrQZ_2(imgPath);
                }
                DBManagerFactory.getInstance().getNotCheckFormManager().update(notCheckFormCYR);
                break;
            case IMAGE_TYPE_NOT_CHECK_SJDW:
                NotCheckForm notCheckFormSJDW = DBManagerFactory.getInstance().getNotCheckFormManager().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(id)).build().unique();
                if(notCheckFormSJDW == null){
                    return;
                }

                notCheckFormSJDW.setQyfzrQZ(imgPath);
                DBManagerFactory.getInstance().getNotCheckFormManager().update(notCheckFormSJDW);
                break;
            case IMAGE_TYPE_REFUSE_CYR:
                RefuseForm refuseFormCYR = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(id)).build().unique();
                if(refuseFormCYR == null){
                    return;
                }

                if(imgPath.endsWith("1.png")){
                    refuseFormCYR.setRefuseCyrImg_1(imgPath);
                }else if(imgPath.endsWith("2.png")) {
                    refuseFormCYR.setRefuseCyrImg_2(imgPath);
                }
                DBManagerFactory.getInstance().getRefuseFormManager().update(refuseFormCYR);
                break;
            case IMAGE_TYPE_REFUSE_SJDW:
                RefuseForm refuseFormSJDW = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(id)).build().unique();
                if(refuseFormSJDW == null){
                    return;
                }

                refuseFormSJDW.setRefuseXgryImg(imgPath);
                DBManagerFactory.getInstance().getRefuseFormManager().update(refuseFormSJDW);
                break;
        }

    }
}
