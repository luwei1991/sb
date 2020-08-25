package com.product.sampling.db.manager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.db.tables.HandleFormDao;
import com.product.sampling.thread.ThreadManager;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

/**
 * Created by 陆伟 on 2019/11/22.
 * Copyright (c) 2019 . All rights reserved.
 */


public class HandleFormManager extends BaseBeanManager<HandleForm,Long> {
    public static final int HANDLE_FORM_QUERY_ID = 0x001;

    public HandleFormManager(AbstractDao dao) {
        super(dao);
    }

    /**
     * 查询
     * @param sampleId
     * @param mHandler
     */
    public void queryBySampleId(String sampleId, Handler mHandler){
        final Query query = queryBuilder().where(HandleFormDao.Properties.SampleId.eq(sampleId)).build();
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                HandleForm handleForm = (HandleForm) query.forCurrentThread().unique();
                Message msg = Message.obtain();
                msg.what = HANDLE_FORM_QUERY_ID ;
                msg.obj = handleForm;
                mHandler.sendMessage(msg);
            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                HandleForm handleForm = (HandleForm) query.forCurrentThread().unique();
//                Message msg = Message.obtain();
//                msg.what = HANDLE_FORM_QUERY_ID ;
//                msg.obj = handleForm;
//                mHandler.sendMessage(msg);
//            }
//        }.start();
    }


    public void updateByHandleForm(HandleForm handleForm){
        ThreadManager.getDbThread().execute(new Runnable() {
            @Override
            public void run() {
                saveOrUpdate(handleForm);
//                HandleForm handleForm = (HandleForm) query.forCurrentThread().unique();
//                Message msg = Message.obtain();
//                msg.what = HANDLE_FORM_QUERY_ID ;
//                msg.obj = handleForm;
//                mHandler.sendMessage(msg);
            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                saveOrUpdate(handleForm);
//            }
//        }.start();
    }
}
