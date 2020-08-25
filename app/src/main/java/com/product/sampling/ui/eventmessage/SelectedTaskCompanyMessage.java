package com.product.sampling.ui.eventmessage;

import com.product.sampling.bean.TaskCompany;

/**
 * Created by 陆伟 on 2019/11/28.
 * Copyright (c) 2019 . All rights reserved.
 */


public class SelectedTaskCompanyMessage {
    private TaskCompany taskCompany;
    private String type;
    public SelectedTaskCompanyMessage(TaskCompany taskCompany,String type){
        this.taskCompany = taskCompany;
        this.type = type;
    }

    public TaskCompany getTaskCompany() {
        return taskCompany;
    }

    public void setTaskCompany(TaskCompany taskCompany) {
        this.taskCompany = taskCompany;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
