package com.product.sampling.ui.form.bean;

import com.product.sampling.ui.form.ReType;

import java.util.List;

/**
 * Created by 陆伟 on 2020/2/26.
 * Copyright (c) 2020 . All rights reserved.
 */


public class  FormCheckBean {
    //编号
    private String checkFormCode;
    //任务编号
    private String checkFormReportCode;
    //任务来源
    private String taskFrom;
    //任务类别
    private String taskClass;
    //受检单位名称
    private String acceptComName;
    //受检单位通讯地址
    private String acceptComAddress;
    //受检单位法人代表
    private String acceptComLegal;
    //受检单位联系人及电话
    private String acceptComConNameAndTel;
    //生产单位名称
        private String productComName;
    //生产单位地址
    private String productComAddress;
    //生产单位邮政编码
    private String productComEMSCode;
    //生产单位法人
    private String productComLegal;
    //生产单位联系人
    private String productComConName;
    //生产单位联系电话
    private String productComTel;
    //生产单位统一社会信用代码
    private String productComId;
    //生产单位营业执照
    private String productComLicense;
    //生产单位从业人员数
    private String productComPerNum;
    //生产单位营业收入
    private String productComIncome;
    //生产单位经济类型
    private String productComEcType;
    //受检产品证书类型
    private String acceptProCerType;
    //受检产品证书编号
    private String acceptProCerID;
    //受检产品名称
    private String acceptProName;
    //受检产品生产日期/批号
    private String acceptProDate;
    //受检产品抽样数量
    private String acceptProCheckNum;
    //受检产品批量
    private String acceptProBatch;
    //受检产品批量(新)
    private String samplingbaseandbatch;
    //受检产品单价
    private String acceptProUnPrice;
    //受检产品标注执行标准/技术文件
    private String acceptProField;
    //受检产品备样量及封存地点
    private String acceptProSampleSizeAndAddress;
    //受检产品规格型号
    private String acceptProModel;
    //受检产品抽样日期
    private String acceptProCheckDate;
    //受检产品商标
    private String acceptProBrand;
    //受检产品产品等级
    private String acceptProGrade;
    //受检产品封样状态
    private String acceptProState;
    //受检产品寄送样地点
    private String acceptProSendAddress;
    //受检产品寄送样截至日期
    private String acceptProSendEndDate;
    //受检产品基数和批量类型
    private String acceptProType;
    //抽样单位名称
    private String checkComName;
    //抽样单位地址
    private String checkComAddress;
    //抽样单位邮政编码
    private String checkComEMSCode;
    //抽样单位联系人
    private String checkComConName;
    //抽样单位联系电话
    private String checkComComTel;
    //抽样单位传真/email
    private String checkComEmail;
    //备注 项目备案号
    private String remarkProjectNo;
    //备注 购样费
    private String remarkGetPrice;
    //备注 信息
    private String remarkInfo;
    //抽样人 日期年
    private String checkPersonDateYEAR;
    //抽样人 日期月
    private String checkPersonDateMONTH;
    //抽样人 日期日
    private String checkPersonDateDAY;
    @ReType("0")
    private List<String > checkImg;
    //受检单位 日期年
    private String acceptProYear;
    //受检单位 日期月
    private String acceptProMonth;
    //受检单位 日期日
    private String acceptProDay;
    //受检单位签字图
    @ReType("0")
    private String acceptProImage;

    //生产单位 日期年
    private String productProYear;
    //生产单位 日期月
    private String productProMonth;
    //生产单位 日期日
    private String productProDay;
    @ReType("0")
    private String productProImage;



    public String getCheckFormCode() {
        return checkFormCode;
    }

    public void setCheckFormCode(String checkFormCode) {
        this.checkFormCode = checkFormCode;
    }

    public String getCheckFormReportCode() {
        return checkFormReportCode;
    }

    public void setCheckFormReportCode(String checkFormReportCode) {
        this.checkFormReportCode = checkFormReportCode;
    }

    public List<String> getCheckImg() {
        return checkImg;
    }

    public void setCheckImg(List<String> checkImg) {
        this.checkImg = checkImg;
    }

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

    public String getAcceptComName() {
        return acceptComName;
    }

    public void setAcceptComName(String acceptComName) {
        this.acceptComName = acceptComName;
    }

    public String getAcceptComAddress() {
        return acceptComAddress;
    }

    public void setAcceptComAddress(String acceptComAddress) {
        this.acceptComAddress = acceptComAddress;
    }

    public String getAcceptComLegal() {
        return acceptComLegal;
    }

    public void setAcceptComLegal(String acceptComLegal) {
        this.acceptComLegal = acceptComLegal;
    }

    public String getAcceptComConNameAndTel() {
        return acceptComConNameAndTel;
    }

    public void setAcceptComConNameAndTel(String acceptComConNameAndTel) {
        this.acceptComConNameAndTel = acceptComConNameAndTel;
    }

    public String getProductComName() {
        return productComName;
    }

    public void setProductComName(String productComName) {
        this.productComName = productComName;
    }

    public String getProductComAddress() {
        return productComAddress;
    }

    public void setProductComAddress(String productComAddress) {
        this.productComAddress = productComAddress;
    }

    public String getProductComEMSCode() {
        return productComEMSCode;
    }

    public void setProductComEMSCode(String productComEMSCode) {
        this.productComEMSCode = productComEMSCode;
    }

    public String getProductComLegal() {
        return productComLegal;
    }

    public void setProductComLegal(String productComLegal) {
        this.productComLegal = productComLegal;
    }

    public String getProductComConName() {
        return productComConName;
    }

    public void setProductComConName(String productComConName) {
        this.productComConName = productComConName;
    }

    public String getProductComTel() {
        return productComTel;
    }

    public void setProductComTel(String productComTel) {
        this.productComTel = productComTel;
    }

    public String getProductComId() {
        return productComId;
    }

    public void setProductComId(String productComId) {
        this.productComId = productComId;
    }

    public String getProductComLicense() {
        return productComLicense;
    }

    public void setProductComLicense(String productComLicense) {
        this.productComLicense = productComLicense;
    }

    public String getProductComPerNum() {
        return productComPerNum;
    }

    public void setProductComPerNum(String productComPerNum) {
        this.productComPerNum = productComPerNum;
    }

    public String getProductComIncome() {
        return productComIncome;
    }

    public void setProductComIncome(String productComIncome) {
        this.productComIncome = productComIncome;
    }

    public String getProductComEcType() {
        return productComEcType;
    }

    public void setProductComEcType(String productComEcType) {
        this.productComEcType = productComEcType;
    }

    public String getAcceptProCerType() {
        return acceptProCerType;
    }

    public void setAcceptProCerType(String acceptProCerType) {
        this.acceptProCerType = acceptProCerType;
    }

    public String getAcceptProCerID() {
        return acceptProCerID;
    }

    public void setAcceptProCerID(String acceptProCerID) {
        this.acceptProCerID = acceptProCerID;
    }

    public String getAcceptProName() {
        return acceptProName;
    }

    public void setAcceptProName(String acceptProName) {
        this.acceptProName = acceptProName;
    }

    public String getAcceptProDate() {
        return acceptProDate;
    }

    public void setAcceptProDate(String acceptProDate) {
        this.acceptProDate = acceptProDate;
    }

    public String getAcceptProCheckNum() {
        return acceptProCheckNum;
    }

    public void setAcceptProCheckNum(String acceptProCheckNum) {
        this.acceptProCheckNum = acceptProCheckNum;
    }

    public String getAcceptProBatch() {
        return acceptProBatch;
    }

    public void setAcceptProBatch(String acceptProBatch) {
        this.acceptProBatch = acceptProBatch;
    }

    public String getAcceptProUnPrice() {
        return acceptProUnPrice;
    }

    public void setAcceptProUnPrice(String acceptProUnPrice) {
        this.acceptProUnPrice = acceptProUnPrice;
    }

    public String getAcceptProField() {
        return acceptProField;
    }

    public void setAcceptProField(String acceptProField) {
        this.acceptProField = acceptProField;
    }

    public String getAcceptProSampleSizeAndAddress() {
        return acceptProSampleSizeAndAddress;
    }

    public void setAcceptProSampleSizeAndAddress(String acceptProSampleSizeAndAddress) {
        this.acceptProSampleSizeAndAddress = acceptProSampleSizeAndAddress;
    }

    public String getAcceptProModel() {
        return acceptProModel;
    }

    public void setAcceptProModel(String acceptProModel) {
        this.acceptProModel = acceptProModel;
    }

    public String getAcceptProCheckDate() {
        return acceptProCheckDate;
    }

    public void setAcceptProCheckDate(String acceptProCheckDate) {
        this.acceptProCheckDate = acceptProCheckDate;
    }

    public String getAcceptProBrand() {
        return acceptProBrand;
    }

    public void setAcceptProBrand(String acceptProBrand) {
        this.acceptProBrand = acceptProBrand;
    }

    public String getAcceptProGrade() {
        return acceptProGrade;
    }

    public void setAcceptProGrade(String acceptProGrade) {
        this.acceptProGrade = acceptProGrade;
    }

    public String getAcceptProState() {
        return acceptProState;
    }

    public void setAcceptProState(String acceptProState) {
        this.acceptProState = acceptProState;
    }

    public String getAcceptProSendAddress() {
        return acceptProSendAddress;
    }

    public void setAcceptProSendAddress(String acceptProSendAddress) {
        this.acceptProSendAddress = acceptProSendAddress;
    }

    public String getAcceptProSendEndDate() {
        return acceptProSendEndDate;
    }

    public void setAcceptProSendEndDate(String acceptProSendEndDate) {
        this.acceptProSendEndDate = acceptProSendEndDate;
    }

    public String getCheckComName() {
        return checkComName;
    }

    public void setCheckComName(String checkComName) {
        this.checkComName = checkComName;
    }

    public String getCheckComAddress() {
        return checkComAddress;
    }

    public void setCheckComAddress(String checkComAddress) {
        this.checkComAddress = checkComAddress;
    }

    public String getCheckComEMSCode() {
        return checkComEMSCode;
    }

    public void setCheckComEMSCode(String checkComEMSCode) {
        this.checkComEMSCode = checkComEMSCode;
    }

    public String getCheckComConName() {
        return checkComConName;
    }

    public void setCheckComConName(String checkComConName) {
        this.checkComConName = checkComConName;
    }

    public String getCheckComComTel() {
        return checkComComTel;
    }

    public void setCheckComComTel(String checkComComTel) {
        this.checkComComTel = checkComComTel;
    }

    public String getCheckComEmail() {
        return checkComEmail;
    }

    public void setCheckComEmail(String checkComEmail) {
        this.checkComEmail = checkComEmail;
    }

    public String getRemarkProjectNo() {
        return remarkProjectNo;
    }

    public void setRemarkProjectNo(String remarkProjectNo) {
        this.remarkProjectNo = remarkProjectNo;
    }

    public String getRemarkGetPrice() {
        return remarkGetPrice;
    }

    public void setRemarkGetPrice(String remarkGetPrice) {
        this.remarkGetPrice = remarkGetPrice;
    }

    public String getRemarkInfo() {
        return remarkInfo;
    }

    public void setRemarkInfo(String remarkInfo) {
        this.remarkInfo = remarkInfo;
    }

    public String getCheckPersonDateYEAR() {
        return checkPersonDateYEAR;
    }

    public void setCheckPersonDateYEAR(String checkPersonDateYEAR) {
        this.checkPersonDateYEAR = checkPersonDateYEAR;
    }

    public String getCheckPersonDateMONTH() {
        return checkPersonDateMONTH;
    }

    public void setCheckPersonDateMONTH(String checkPersonDateMONTH) {
        this.checkPersonDateMONTH = checkPersonDateMONTH;
    }

    public String getCheckPersonDateDAY() {
        return checkPersonDateDAY;
    }

    public void setCheckPersonDateDAY(String checkPersonDateDAY) {
        this.checkPersonDateDAY = checkPersonDateDAY;
    }

    public String getAcceptProYear() {
        return acceptProYear;
    }

    public void setAcceptProYear(String acceptProYear) {
        this.acceptProYear = acceptProYear;
    }

    public String getAcceptProMonth() {
        return acceptProMonth;
    }

    public void setAcceptProMonth(String acceptProMonth) {
        this.acceptProMonth = acceptProMonth;
    }

    public String getAcceptProDay() {
        return acceptProDay;
    }

    public void setAcceptProDay(String acceptProDay) {
        this.acceptProDay = acceptProDay;
    }

    public String getAcceptProImage() {
        return acceptProImage;
    }

    public void setAcceptProImage(String acceptProImage) {
        this.acceptProImage = acceptProImage;
    }

    public String getProductProYear() {
        return productProYear;
    }

    public void setProductProYear(String productProYear) {
        this.productProYear = productProYear;
    }

    public String getProductProMonth() {
        return productProMonth;
    }

    public void setProductProMonth(String productProMonth) {
        this.productProMonth = productProMonth;
    }

    public String getProductProDay() {
        return productProDay;
    }

    public void setProductProDay(String productProDay) {
        this.productProDay = productProDay;
    }

    public String getProductProImage() {
        return productProImage;
    }

    public void setProductProImage(String productProImage) {
        this.productProImage = productProImage;
    }

    public String getAcceptProType() {
        return acceptProType;
    }

    public void setAcceptProType(String acceptProType) {
        this.acceptProType = acceptProType;
    }

    public String getSamplingbaseandbatch() {
        return samplingbaseandbatch;
    }

    public void setSamplingbaseandbatch(String samplingbaseandbatch) {
        this.samplingbaseandbatch = samplingbaseandbatch;
    }
}
