package com.product.sampling.db.manager;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.db.tables.HandleFormDao;
import com.product.sampling.thread.ThreadManager;
import com.product.sampling.utils.LogUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.io.File;

/**
 * Created by 陆伟 on 2019/11/22.
 * Copyright (c) 2019 . All rights reserved.
 */


public class CheckFormManager extends BaseBeanManager<CheckForm,Long> {
    public static final int CHECK_FORM_QUERY_ID = 0x003;
    public static final int CHECK_FORM_QUERY_SAMPLE_CODE = 0x003;
    public static final int CHECK_FORM_QUERY_SAMPLE_ID_BY_SAMPLE_CODE = 0x004;
    public static final int CHECK_FORM_DELETE_ID = 0x005;

    public CheckFormManager(AbstractDao dao) {
        super(dao);
    }

    /**
     * 查询
     * @param sampleId
     * @param mHandler
     */
    public void queryBySampleId(String sampleId, Handler mHandler){
        final Query query = queryBuilder().where(CheckFormDao.Properties.Id.eq(sampleId)).build();
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
                Message msg = Message.obtain();
                msg.what = CHECK_FORM_QUERY_ID ;
                msg.obj = checkForm;
                mHandler.sendMessage(msg);
            }
        });

//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
//                Message msg = Message.obtain();
//                msg.what = CHECK_FORM_QUERY_ID ;
//                msg.obj = checkForm;
//                mHandler.sendMessage(msg);
//            }
//        }.start();
    }


    /**
     * 查询
     * @param sampleCode
     * @param mHandler
     */
    public void queryBySampleCode(String sampleCode, Handler mHandler){
        final Query query = queryBuilder().where(CheckFormDao.Properties.TaskCode.eq(sampleCode)).build();
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
                Message msg = Message.obtain();
                msg.what = CHECK_FORM_QUERY_SAMPLE_CODE ;
                msg.obj = checkForm;
                mHandler.sendMessage(msg);
            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
//                Message msg = Message.obtain();
//                msg.what = CHECK_FORM_QUERY_SAMPLE_CODE ;
//                msg.obj = checkForm;
//                mHandler.sendMessage(msg);
//            }
//        }.start();
    }


    public void updateByCheckForm(CheckForm checkForm){
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                saveOrUpdate(checkForm);
            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                saveOrUpdate(checkForm);
//            }
//        }.start();
    }
    /**
     * 查询
     * @param sampleCode
     * @param mHandler
     */
    public void querySampleIdBySampleCode(String sampleCode, Handler mHandler){
        final Query query = queryBuilder().where(CheckFormDao.Properties.TaskCode.eq(sampleCode)).build();
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
                Message msg = Message.obtain();
                msg.what = CHECK_FORM_QUERY_SAMPLE_ID_BY_SAMPLE_CODE ;
                msg.obj = checkForm;
                mHandler.sendMessage(msg);
            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
//                Message msg = Message.obtain();
//                msg.what = CHECK_FORM_QUERY_SAMPLE_CODE ;
//                msg.obj = checkForm;
//                mHandler.sendMessage(msg);
//            }
//        }.start();
    }



    /**
     * 删除
     * @param sampleId
     */
    public void deleteBySampleId(String sampleId){
        final Query query = queryBuilder().where(CheckFormDao.Properties.Id.eq(sampleId)).build();
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                CheckForm checkForm = (CheckForm) query.forCurrentThread().unique();
                if(checkForm != null){
                    String pdfPath = checkForm.getPdfPath();
                    if(!TextUtils.isEmpty(pdfPath)){
                        File pdfFile = new File(pdfPath);
                        if(pdfFile.exists()){
                            pdfFile.delete();
                        }
                    }

                    String sign1 = checkForm.getAcceptCompanySignImagePath();
                    if(!TextUtils.isEmpty(sign1)){
                        File sign1File = new File(sign1);
                        if(sign1File.exists()){
                            sign1File.delete();
                        }
                    }

                    String sign2 = checkForm.getCheckPeopleSignImagePath_1();
                    if(!TextUtils.isEmpty(sign2)){
                        File sign2File = new File(sign2);
                        if(sign2File.exists()){
                            sign2File.delete();
                        }
                    }

                    String sign4 = checkForm.getCheckPeopleSignImagePath_2();
                    if(!TextUtils.isEmpty(sign4)){
                        File sign2File = new File(sign4);
                        if(sign2File.exists()){
                            sign2File.delete();
                        }
                    }

                    String sign3 = checkForm.getProductCompanySignImagePath();
                    if(!TextUtils.isEmpty(sign3)){
                        File sign3File = new File(sign3);
                        if(sign3File.exists()){
                            sign3File.delete();
                        }
                    }
                    DBManagerFactory.getInstance().getCheckFormManager().delete(checkForm);
                }

            }
        });


}
}
