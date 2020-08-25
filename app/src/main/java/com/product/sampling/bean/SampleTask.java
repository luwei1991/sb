package com.product.sampling.bean;

import java.util.List;

/**
 * 用于存储任务和样品之间的关系
 * Created by 陆伟 on 2020/6/30.
 * Copyright (c) 2020 . All rights reserved.
 */


public class SampleTask {
    private String taskId;
    private List<String> sampleIdList;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<String> getSampleIdList() {
        return sampleIdList;
    }

    public void setSampleIdList(List<String> sampleIdList) {
        this.sampleIdList = sampleIdList;
    }
}
