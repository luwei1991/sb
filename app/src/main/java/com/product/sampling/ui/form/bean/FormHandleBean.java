package com.product.sampling.ui.form.bean;

import com.product.sampling.ui.form.ReType;

import java.util.List;

/**
 * Created by 陆伟 on 2020/2/27.
 * Copyright (c) 2020 . All rights reserved.
 */


public class FormHandleBean {
    //抽样单编号
    private String checkFormID;
    //受检单位名称
    private String acceptComName;
    //监督管理单位
    private String supComName;
    //年
    private String year;
    //月
    private String month;
    //日
    private String day;
    //销售，生产类型
    private String saleOrProductType;
    //产品名称
    private String proName;
    //规格型号
    private String proModel;
    //样品状态（备用，检毕）
    private String proState;
    //抽样人日期
    private String checkPersonYear;
    //抽样人日期
    private String checkPersonMonth;
    //抽样人日期
    private String checkPersonDay;
    //抽样人签字图
    @ReType("0")
    private List<String > checkPersonImage;
    //受检单位日期
    private String acceptYear;
    //受检单位日期
    private String acceptMonth;
    //受检单位日期
    private String acceptDay;
    //受检单位签字
    @ReType("0")
    private String acceptImage;

    public String getCheckFormID() {
        return checkFormID;
    }

    public void setCheckFormID(String checkFormID) {
        this.checkFormID = checkFormID;
    }

    public String getAcceptComName() {
        return acceptComName;
    }

    public void setAcceptComName(String acceptComName) {
        this.acceptComName = acceptComName;
    }

    public String getSupComName() {
        return supComName;
    }

    public void setSupComName(String supComName) {
        this.supComName = supComName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSaleOrProductType() {
        return saleOrProductType;
    }

    public void setSaleOrProductType(String saleOrProductType) {
        this.saleOrProductType = saleOrProductType;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProModel() {
        return proModel;
    }

    public void setProModel(String proModel) {
        this.proModel = proModel;
    }

    public String getProState() {
        return proState;
    }

    public void setProState(String proState) {
        this.proState = proState;
    }

    public String getCheckPersonYear() {
        return checkPersonYear;
    }

    public void setCheckPersonYear(String checkPersonYear) {
        this.checkPersonYear = checkPersonYear;
    }

    public String getCheckPersonMonth() {
        return checkPersonMonth;
    }

    public void setCheckPersonMonth(String checkPersonMonth) {
        this.checkPersonMonth = checkPersonMonth;
    }

    public String getCheckPersonDay() {
        return checkPersonDay;
    }

    public void setCheckPersonDay(String checkPersonDay) {
        this.checkPersonDay = checkPersonDay;
    }

    public List<String> getCheckPersonImage() {
        return checkPersonImage;
    }

    public void setCheckPersonImage(List<String> checkPersonImage) {
        this.checkPersonImage = checkPersonImage;
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
