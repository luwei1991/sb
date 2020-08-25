package com.product.sampling.ui.masterplate.bean;

import java.io.Serializable;

/**
 * Created by 陆伟 on 2019/11/21.
 * Copyright (c) 2019 . All rights reserved.
 */


public class MasterpleListBean implements Serializable {
    private String id;
    private boolean isNewRecord;
    private String remarks;
    private String createDate;
    private String updateDate;
    private String mouldtitle;
    private String mouldtext;
    private String personid;
    private String mouldtype;
    private String reporttype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getMouldtitle() {
        return mouldtitle;
    }

    public void setMouldtitle(String mouldtitle) {
        this.mouldtitle = mouldtitle;
    }

    public String getMouldtext() {
        return mouldtext;
    }

    public void setMouldtext(String mouldtext) {
        this.mouldtext = mouldtext;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getMouldtype() {
        return mouldtype;
    }

    public void setMouldtype(String mouldtype) {
        this.mouldtype = mouldtype;
    }

    public String getReporttype() {
        return reporttype;
    }

    public void setReporttype(String reporttype) {
        this.reporttype = reporttype;
    }
}
