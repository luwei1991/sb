package com.product.sampling.ui.form.bean;

import com.product.sampling.ui.form.ReType;

import java.util.List;

/**
 * Created by 陆伟 on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 */


public class RefuseBean {
    private String producename;		// 产品名称
    private String companyname;		// 企业名称
    private String representative;		// 法人代表
    private String companyaddress;		// 企业地址
    private String telandman;		// 联系人及电话
    private String tel;		// 联系人及电话
    private String taskfromyear;		// 任务来源
    private String taskfrompro;		// 任务来源
    private String taskfromjd;		// 任务来源
    private String samplingtel;		// 抽样单位联系电话
    private String progrss;		// 过程描述
    private String advice;		// 当地部门建议
    private String fillInDateSYear;//事实认定填写日期
    private String fillInDateSMonth;//事实认定填写日期
    private String fillInDateSDay;//事实认定填写日期
    @ReType("0")
    private List<String> samplemanbase64;		// 抽样人签字
    @ReType("0")
    private String companymanbase64;   //相关人员签字
    private String fillInDateDYear;//当地部门日期
    private String fillInDateDMonth;//当地部门日期
    private String fillInDateDDay;//当地部门日期

    public String getProducename() {
        return producename;
    }

    public void setProducename(String producename) {
        this.producename = producename;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getCompanyaddress() {
        return companyaddress;
    }

    public void setCompanyaddress(String companyaddress) {
        this.companyaddress = companyaddress;
    }

    public String getTelandman() {
        return telandman;
    }

    public void setTelandman(String telandman) {
        this.telandman = telandman;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTaskfromyear() {
        return taskfromyear;
    }

    public void setTaskfromyear(String taskfromyear) {
        this.taskfromyear = taskfromyear;
    }

    public String getTaskfrompro() {
        return taskfrompro;
    }

    public void setTaskfrompro(String taskfrompro) {
        this.taskfrompro = taskfrompro;
    }

    public String getTaskfromjd() {
        return taskfromjd;
    }

    public void setTaskfromjd(String taskfromjd) {
        this.taskfromjd = taskfromjd;
    }

    public String getSamplingtel() {
        return samplingtel;
    }

    public void setSamplingtel(String samplingtel) {
        this.samplingtel = samplingtel;
    }

    public String getProgrss() {
        return progrss;
    }

    public void setProgrss(String progrss) {
        this.progrss = progrss;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getFillInDateSYear() {
        return fillInDateSYear;
    }

    public void setFillInDateSYear(String fillInDateSYear) {
        this.fillInDateSYear = fillInDateSYear;
    }

    public String getFillInDateSMonth() {
        return fillInDateSMonth;
    }

    public void setFillInDateSMonth(String fillInDateSMonth) {
        this.fillInDateSMonth = fillInDateSMonth;
    }

    public String getFillInDateSDay() {
        return fillInDateSDay;
    }

    public void setFillInDateSDay(String fillInDateSDay) {
        this.fillInDateSDay = fillInDateSDay;
    }

    public String getFillInDateDYear() {
        return fillInDateDYear;
    }

    public void setFillInDateDYear(String fillInDateDYear) {
        this.fillInDateDYear = fillInDateDYear;
    }

    public String getFillInDateDMonth() {
        return fillInDateDMonth;
    }

    public void setFillInDateDMonth(String fillInDateDMonth) {
        this.fillInDateDMonth = fillInDateDMonth;
    }

    public String getFillInDateDDay() {
        return fillInDateDDay;
    }

    public void setFillInDateDDay(String fillInDateDDay) {
        this.fillInDateDDay = fillInDateDDay;
    }

    public List<String> getSamplemanbase64() {
        return samplemanbase64;
    }

    public void setSamplemanbase64(List<String> samplemanbase64) {
        this.samplemanbase64 = samplemanbase64;
    }

    public String getCompanymanbase64() {
        return companymanbase64;
    }

    public void setCompanymanbase64(String companymanbase64) {
        this.companymanbase64 = companymanbase64;
    }
}
