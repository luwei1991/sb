package com.product.sampling.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.product.sampling.db.tables.DaoMaster;
import com.product.sampling.db.tables.DaoSession;

/**
 * 1、管理数据库（打开或者关闭 升级 存储位置）
 * 2、获取daoSession
 * Created by 陆伟 on 2019/11/12.
 * Copyright (c) 2019 . All rights reserved.
 */


public class DbController {
    private static final String TAG = "DbController";
    private static final String DB_NAME = "mobile.db";

    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;

    /**
     * 上下文
     */
    private Context context;
    private DaoSession mDaoSession;

    private static DbController mDbController;

    /**
     * 获取单例
     */
    public static DbController getInstance(Context context){
        if(mDbController == null){
            synchronized (DbController.class){
                if(mDbController == null){
                    mDbController = new DbController(context);
                }
            }
        }
        return mDbController;
    }

    /**
     * 初始化
     * @param context
     */
    private DbController(Context context) {
        this.context = context;
    }

    public synchronized DaoSession getDaoSession(){
        if(mDaoSession == null){
            mDaoSession = getDaoMaster().newSession();
        }

        return mDaoSession;
    }

    public synchronized void closeDataBase(){
        closeHelper();
        closeDaoSession();
    }


    /**
     * 判断数据库是否存在，如果不存在则创建
     *
     * @return
     */
    private DaoMaster getDaoMaster() {
        if (null == mDaoMaster) {
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            mDaoMaster = new DaoMaster(mHelper.getWritableDb());
        }
        return mDaoMaster;
    }

    private void closeDaoSession() {
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
    private void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }

}
