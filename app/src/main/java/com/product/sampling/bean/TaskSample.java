package com.product.sampling.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskSample {

    public List<TaskImageEntity> list = new ArrayList<>();
    public List<LocalMediaInfo> videoList = new ArrayList<>();
    public String samplingfile = "";
    public String disposalfile = "";
    public String samplingpicfile = "";
    public String disposalpicfile = "";
    public HashMap<String, String> samplingInfoMap = new HashMap<>();//检查单信息
    public HashMap<String, String> adviceInfoMap = new HashMap<>();//处置单信息
    public Advice advice = new Advice();
    public Sampling sampling = new Sampling();
    public List<Pics> pics = new ArrayList<>();
    /**
     * id : 7777
     * isNewRecord : false
     * remarks : null
     * createDate : null
     * updateDate : null
     * samplename : null
     * samplecount : null
     * typeid : null
     * samplecode : null
     * samplebase : null
     * taskid : 26cb34086f1c4213ad60122d016eb22f
     * remark : null
     * projectdescription : null
     * facture : null
     * batchnum : null
     * samplingreprotid : f593dcba4626463b93dc88140642b8ba
     * riskreprotid : null
     * disposalreportid : 9c702ee427254efcb9080de5db1d37c4
     * workreportid : null
     * orderno : null
     * type : null
     */

    private String id;
    private boolean isNewRecord;
    private String remarks = "";
    private String createDate;
    private String updateDate;
    private String samplename;
    private String samplecount;
    private String typeid;
    private String samplecode;
    private String samplebase;
    private String taskid;
    private String remark = "";
    private String projectdescription;
    private String facture;
    private String batchnum;
    private String samplingreprotid;
    private String riskreprotid;
    private String disposalreportid;
    private String workreportid;
    private String orderno;
    private String type;

    public boolean isLocalData = false;

    public TaskSample() {

    }

    public List<TaskImageEntity> getList() {
        return list;
    }

    public void setList(List<TaskImageEntity> list) {
        this.list = list;
    }

    public List<LocalMediaInfo> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<LocalMediaInfo> videoList) {
        this.videoList = videoList;
    }

    public String getSamplingfile() {
        return samplingfile;
    }

    public void setSamplingfile(String samplingfile) {
        this.samplingfile = samplingfile;
    }

    public String getDisposalfile() {
        return disposalfile;
    }

    public void setDisposalfile(String disposalfile) {
        this.disposalfile = disposalfile;
    }

    public HashMap<String, String> getAdviceInfoMap() {
        return adviceInfoMap;
    }

    public void setAdviceInfoMap(HashMap<String, String> adviceInfoMap) {
        this.adviceInfoMap = adviceInfoMap;
    }

    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public Sampling getSampling() {
        return sampling;
    }

    public void setSampling(Sampling sampling) {
        this.sampling = sampling;
    }

    public List<Pics> getPics() {
        return pics;
    }

    public void setPics(List<Pics> pics) {
        this.pics = pics;
    }

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

    public String getSamplename() {
        return samplename;
    }

    public void setSamplename(String samplename) {
        this.samplename = samplename;
    }

    public String getSamplecount() {
        return samplecount;
    }

    public void setSamplecount(String samplecount) {
        this.samplecount = samplecount;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getSamplecode() {
        return samplecode;
    }

    public void setSamplecode(String samplecode) {
        this.samplecode = samplecode;
    }

    public String getSamplebase() {
        return samplebase;
    }

    public void setSamplebase(String samplebase) {
        this.samplebase = samplebase;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProjectdescription() {
        return projectdescription;
    }

    public void setProjectdescription(String projectdescription) {
        this.projectdescription = projectdescription;
    }

    public String getFacture() {
        return facture;
    }

    public void setFacture(String facture) {
        this.facture = facture;
    }

    public String getBatchnum() {
        return batchnum;
    }

    public void setBatchnum(String batchnum) {
        this.batchnum = batchnum;
    }

    public String getSamplingreprotid() {
        return samplingreprotid;
    }

    public void setSamplingreprotid(String samplingreprotid) {
        this.samplingreprotid = samplingreprotid;
    }

    public String getRiskreprotid() {
        return riskreprotid;
    }

    public void setRiskreprotid(String riskreprotid) {
        this.riskreprotid = riskreprotid;
    }

    public String getDisposalreportid() {
        return disposalreportid;
    }

    public void setDisposalreportid(String disposalreportid) {
        this.disposalreportid = disposalreportid;
    }

    public String getWorkreportid() {
        return workreportid;
    }

    public void setWorkreportid(String workreportid) {
        this.workreportid = workreportid;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLocalData() {
        return isLocalData;
    }

    public void setLocalData(boolean localData) {
        isLocalData = localData;
    }
}

