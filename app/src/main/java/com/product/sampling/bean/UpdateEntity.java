package com.product.sampling.bean;

import java.io.Serializable;

public class UpdateEntity implements Serializable {


    /**
     * id : 1
     * isNewRecord : false
     * remarks : null
     * createDate : null
     * updateDate : null
     * versioncode : 1.0.0.0
     * appfileid : 22
     * isforce : 1
     * remark : 这是第一个版本
     * isnew : 1
     * createtime : 2019-05-14 01:48:57
     */

    private String id;
    private boolean isNewRecord;
    private Object remarks;
    private Object createDate;
    private Object updateDate;
    private String versioncode;
    private String appfileid;
    private String isforce;
    private String remark;
    private String isnew;
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsNewRecord() {
        return isNewRecord;
    }

    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public Object getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Object createDate) {
        this.createDate = createDate;
    }

    public Object getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Object updateDate) {
        this.updateDate = updateDate;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getAppfileid() {
        return appfileid;
    }

    public void setAppfileid(String appfileid) {
        this.appfileid = appfileid;
    }

    public String getIsforce() {
        return isforce;
    }

    public void setIsforce(String isforce) {
        this.isforce = isforce;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsnew() {
        return isnew;
    }

    public void setIsnew(String isnew) {
        this.isnew = isnew;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
