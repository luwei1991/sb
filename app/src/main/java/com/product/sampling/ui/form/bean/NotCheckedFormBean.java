package com.product.sampling.ui.form.bean;

import com.product.sampling.ui.form.ReType;

import java.util.List;

/**
 * Created by 陆伟 on 2020/2/27.
 * Copyright (c) 2020 . All rights reserved.
 */


public class NotCheckedFormBean {
    //任务来源
    private String taskFrom;
    //任务类别
    private String taskClass;
    //产品类别
    private String proClass;
    //许可证号
    private String cerId;
    //企业名称
    private String comName;
    //企业地址
    private String comAddress;
    //企业联系人
    private String comConName;
    //联系电话
    private String conTel;
    //最近一年是否有销售订单
    private String saleType;
    //产品流向
    private String proTo;
    //未抽到原因
    private String notCheckReason;
    //过程简要描述
    private String progressDes;
    //当地市场监管意见
    private String localAdvice;
    //抽样人日期
    private String checkPerYear;
    private String checkPerMonth;
    private String checkPerDay;
    //抽样人签字图
    @ReType("0")
    private List<String> checkPersonImage;
    //当地部门日期
    private String localYear;
    private String localMonth;
    private String localDay;
    //受检单位日期
    private String acceptYear;
    private String acceptMonth;
    private String acceptDay;
    //受检单位签字图
    @ReType("0")
    private String acceptImage;

    public String getTaskFrom() {
        return taskFrom;
    }

    public void setTaskFrom(String taskFrom) {
        this.taskFrom = taskFrom;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public String getProClass() {
        return proClass;
    }

    public void setProClass(String proClass) {
        this.proClass = proClass;
    }

    public String getCerId() {
        return cerId;
    }

    public void setCerId(String cerId) {
        this.cerId = cerId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getComAddress() {
        return comAddress;
    }

    public void setComAddress(String comAddress) {
        this.comAddress = comAddress;
    }

    public String getComConName() {
        return comConName;
    }

    public void setComConName(String comConName) {
        this.comConName = comConName;
    }

    public String getConTel() {
        return conTel;
    }

    public void setConTel(String conTel) {
        this.conTel = conTel;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getProTo() {
        return proTo;
    }

    public void setProTo(String proTo) {
        this.proTo = proTo;
    }

    public String getNotCheckReason() {
        return notCheckReason;
    }

    public void setNotCheckReason(String notCheckReason) {
        this.notCheckReason = notCheckReason;
    }

    public String getProgressDes() {
        return progressDes;
    }

    public void setProgressDes(String progressDes) {
        this.progressDes = progressDes;
    }

    public String getLocalAdvice() {
        return localAdvice;
    }

    public void setLocalAdvice(String localAdvice) {
        this.localAdvice = localAdvice;
    }

    public String getCheckPerYear() {
        return checkPerYear;
    }

    public void setCheckPerYear(String checkPerYear) {
        this.checkPerYear = checkPerYear;
    }

    public String getCheckPerMonth() {
        return checkPerMonth;
    }

    public void setCheckPerMonth(String checkPerMonth) {
        this.checkPerMonth = checkPerMonth;
    }

    public String getCheckPerDay() {
        return checkPerDay;
    }

    public void setCheckPerDay(String checkPerDay) {
        this.checkPerDay = checkPerDay;
    }

    public List<String> getCheckPersonImage() {
        return checkPersonImage;
    }

    public void setCheckPersonImage(List<String> checkPersonImage) {
        this.checkPersonImage = checkPersonImage;
    }

    public String getLocalYear() {
        return localYear;
    }

    public void setLocalYear(String localYear) {
        this.localYear = localYear;
    }

    public String getLocalMonth() {
        return localMonth;
    }

    public void setLocalMonth(String localMonth) {
        this.localMonth = localMonth;
    }

    public String getLocalDay() {
        return localDay;
    }

    public void setLocalDay(String localDay) {
        this.localDay = localDay;
    }

    public String getAcceptYear() {
        return acceptYear;
    }

    public void setAcceptYear(String acceptYear) {
        this.acceptYear = acceptYear;
    }

    public String getAcceptMonth() {
        return acceptMonth;
    }

    public void setAcceptMonth(String acceptMonth) {
        this.acceptMonth = acceptMonth;
    }

    public String getAcceptDay() {
        return acceptDay;
    }

    public void setAcceptDay(String acceptDay) {
        this.acceptDay = acceptDay;
    }

    public String getAcceptImage() {
        return acceptImage;
    }

    public void setAcceptImage(String acceptImage) {
        this.acceptImage = acceptImage;
    }
}
