package com.product.sampling.db.manager;

import com.product.sampling.db.tables.RefuseForm;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by 陆伟 on 2019/11/22.
 * Copyright (c) 2019 . All rights reserved.
 */


public class RefuseFormManager extends BaseBeanManager<RefuseForm,Long> {

    public RefuseFormManager(AbstractDao dao) {
        super(dao);
    }
}
