package com.product.sampling.db;

import com.product.sampling.MainApplication;
import com.product.sampling.db.manager.CheckFormManager;
import com.product.sampling.db.manager.HandleFormManager;
import com.product.sampling.db.manager.NotCheckFormManager;
import com.product.sampling.db.manager.RefuseFormManager;

/**
 * Created by 陆伟 on 2019/11/22.
 * Copyright (c) 2019 . All rights reserved.
 */


public class DBManagerFactory {
    /**
     * 每一个BeanManager都管理着数据库中的一个表，我将这些管理者在ManagerFactory中进行统一管理
     */
    CheckFormManager checkFormManager;
    HandleFormManager handleFormManager;
    NotCheckFormManager notCheckFormManager;
    RefuseFormManager refuseFormManager;

    private static DBManagerFactory mInstance = null;

    private DBManagerFactory(){}

    /**
     * 获取DaoFactory的实例
     *
     * @return
     */
    public static DBManagerFactory getInstance() {
        if (mInstance == null) {
            synchronized (DBManagerFactory.class) {
                if (mInstance == null) {
                    mInstance = new DBManagerFactory();
                }
            }
        }
        return mInstance;
    }

    public synchronized CheckFormManager getCheckFormManager() {
        if (checkFormManager == null){
            checkFormManager = new CheckFormManager(DbController.getInstance(MainApplication.INSTANCE).getDaoSession().getCheckFormDao());
        }
        return checkFormManager;
    }

    public synchronized HandleFormManager getHandleManager(){
        if (handleFormManager == null){
            handleFormManager = new HandleFormManager(DbController.getInstance(MainApplication.INSTANCE).getDaoSession().getHandleFormDao());
        }
        return handleFormManager;
    }

    public synchronized NotCheckFormManager getNotCheckFormManager(){
        if (notCheckFormManager == null){
            notCheckFormManager = new NotCheckFormManager(DbController.getInstance(MainApplication.INSTANCE).getDaoSession().getNotCheckFormDao());
        }
        return notCheckFormManager;
    }
    public synchronized RefuseFormManager getRefuseFormManager(){
        if (refuseFormManager == null){
            refuseFormManager = new RefuseFormManager(DbController.getInstance(MainApplication.INSTANCE).getDaoSession().getRefuseFormDao());
        }
        return refuseFormManager;
    }

}
