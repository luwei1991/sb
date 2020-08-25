package com.product.sampling.bean;

import java.io.Serializable;

public class Sampling implements Serializable {


    /**
     * id : null
     * isNewRecord : true
     * remarks : null
     * createDate : null
     * updateDate : null
     * taskfrom : tt
     * tasktype : ghh
     * inspectedname : tt
     * inspectedaddress : gg
     * inspectedman : hh
     * inspectedtel : hhb
     * representative : hh
     * producename : tgt
     * produceaddress : gg
     * producezipcode : tt
     * prepresentative : gg
     * produceconman : gg
     * producetel : gg
     * producelicense : gg
     * producecode : gg
     * producepcount : gg
     * produceoutput : null
     * produceharvest : null
     * producetype : null
     * productnumtype : 1
     * productnum : null
     * productname : tt
     * productmodel : tt
     * productbnum : tt
     * productmark : tt
     * samplingcount : tt
     * productlevel : tt
     * productpl : tt
     * dostandard : gg
     * dotime : 2019-07-09 01:47:18
     * encapsulationstate : gg
     * rvandfc : tt
     * sendaddress : gg
     * export : null
     * endtime : null
     * sammanname : null
     * samplingman : null
     * samplingaddress : tt
     * samplingtel : gggtt
     * samplingemail : gggtt
     * samplingcode : tt
     * remark : ggggg
     * recordnumber : null
     * unitprice : tt
     * getreporttype : null
     * enddotype : null
     * reportcode : null
     * taskcode : null
     * createMan : 8ad2b1727a3544bf845cdd2c83abaf5a
     * createTime : 2019-07-09 01:47:18
     * deleteFlag : null
     * producesamlltype : 7
     * fillInDate : 2019-7-9
     */

    private String id;
    private boolean isNewRecord;
    private String remarks;
    private String createDate;
    private String updateDate;
    private String taskfrom;
    private String tasktype;
    private String taskfrommean;
    private String tasktypemean;
    private String inspectedname;
    private String inspectedaddress;
    private String inspectedman;
    private String inspectedtel;
    private String representative;
    private String producename;
    private String produceaddress;
    private String producezipcode;
    private String prepresentative;
    private String produceconman;
    private String producetel;
    private String producelicense;
    private String producecode;
    private String producepcount;
    private String produceoutput;
    private String produceharvest;
    private String producetype;
    private String productnumtype;
    private String productnum;
    private String productname;
    private String doman;
    private String productmodel;
    private String productbnum;
    private String productmark;
    private String samplingcount;
    private String productlevel;
    private String productpl;
    private String dostandard;
    private String dotime;
    private String encapsulationstate;
    private String rvandfc;
    private String sendaddress;
    private String export;
    private String endtime;
    private String sealUp;//封存地点
    private String samplecost;
    private String acceptProType;//基数和批量类型
    private String samplingbaseandbatch;//新基数批量



    public String getAcceptProType() {
        return acceptProType;
    }

    public void setAcceptProType(String acceptProType) {
        this.acceptProType = acceptProType;
    }

    public String getSealUp() {
        return sealUp;
    }

    public String getSamplecost() {
        return samplecost;
    }

    public void setSamplecost(String samplecost) {
        this.samplecost = samplecost;
    }

    public void setSealUp(String sealUp) {
        this.sealUp = sealUp;
    }

    public String getSamplingnamemean() {
        return samplingnamemean;
    }

    public void setSamplingnamemean(String samplingnamemean) {
        this.samplingnamemean = samplingnamemean;
    }

    private String sammanname;
    private String samplingnamemean;
    private String samplingman;
    private String samplingaddress;
    private String samplingtel;
    private String samplingemail;
    private String samplingcode;
    private String remark;
    private String recordnumber;
    private String unitprice;
    private String getreporttype;
    private String enddotype;
    private String reportcode;
    private String taskcode;
    private String createMan;
    private String createTime;
    private String deleteFlag;
    private String producesamlltype;
    private String fillInDate;

    public Sampling() {
    }

    public String getTaskfrommean() {
        return taskfrommean;
    }

    public void setTaskfrommean(String taskfrommean) {
        this.taskfrommean = taskfrommean;
    }

    public String getTasktypemean() {
        return tasktypemean;
    }

    public void setTasktypemean(String tasktypemean) {
        this.tasktypemean = tasktypemean;
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

    public String getTaskfrom() {
        return taskfrom;
    }

    public void setTaskfrom(String taskfrom) {
        this.taskfrom = taskfrom;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getInspectedname() {
        return inspectedname;
    }

    public void setInspectedname(String inspectedname) {
        this.inspectedname = inspectedname;
    }

    public String getInspectedaddress() {
        return inspectedaddress;
    }

    public void setInspectedaddress(String inspectedaddress) {
        this.inspectedaddress = inspectedaddress;
    }

    public String getInspectedman() {
        return inspectedman;
    }

    public void setInspectedman(String inspectedman) {
        this.inspectedman = inspectedman;
    }

    public String getInspectedtel() {
        return inspectedtel;
    }

    public void setInspectedtel(String inspectedtel) {
        this.inspectedtel = inspectedtel;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getProducename() {
        return producename;
    }

    public void setProducename(String producename) {
        this.producename = producename;
    }

    public String getProduceaddress() {
        return produceaddress;
    }

    public void setProduceaddress(String produceaddress) {
        this.produceaddress = produceaddress;
    }

    public String getProducezipcode() {
        return producezipcode;
    }

    public void setProducezipcode(String producezipcode) {
        this.producezipcode = producezipcode;
    }

    public String getPrepresentative() {
        return prepresentative;
    }

    public void setPrepresentative(String prepresentative) {
        this.prepresentative = prepresentative;
    }

    public String getProduceconman() {
        return produceconman;
    }

    public void setProduceconman(String produceconman) {
        this.produceconman = produceconman;
    }

    public String getProducetel() {
        return producetel;
    }

    public void setProducetel(String producetel) {
        this.producetel = producetel;
    }

    public String getProducelicense() {
        return producelicense;
    }

    public void setProducelicense(String producelicense) {
        this.producelicense = producelicense;
    }

    public String getProducecode() {
        return producecode;
    }

    public void setProducecode(String producecode) {
        this.producecode = producecode;
    }

    public String getProducepcount() {
        return producepcount;
    }

    public void setProducepcount(String producepcount) {
        this.producepcount = producepcount;
    }

    public String getProduceoutput() {
        return produceoutput;
    }

    public void setProduceoutput(String produceoutput) {
        this.produceoutput = produceoutput;
    }

    public String getProduceharvest() {
        return produceharvest;
    }

    public void setProduceharvest(String produceharvest) {
        this.produceharvest = produceharvest;
    }

    public String getProducetype() {
        return producetype;
    }

    public void setProducetype(String producetype) {
        this.producetype = producetype;
    }

    public String getProductnumtype() {
        return productnumtype;
    }

    public void setProductnumtype(String productnumtype) {
        this.productnumtype = productnumtype;
    }

    public String getProductnum() {
        return productnum;
    }

    public void setProductnum(String productnum) {
        this.productnum = productnum;
    }

    public String getDoman() {
        return doman;
    }

    public void setDoman(String doman) {
        this.doman = doman;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductmodel() {
        return productmodel;
    }

    public void setProductmodel(String productmodel) {
        this.productmodel = productmodel;
    }

    public String getProductbnum() {
        return productbnum;
    }

    public void setProductbnum(String productbnum) {
        this.productbnum = productbnum;
    }

    public String getProductmark() {
        return productmark;
    }

    public void setProductmark(String productmark) {
        this.productmark = productmark;
    }

    public String getSamplingcount() {
        return samplingcount;
    }

    public void setSamplingcount(String samplingcount) {
        this.samplingcount = samplingcount;
    }

    public String getProductlevel() {
        return productlevel;
    }

    public void setProductlevel(String productlevel) {
        this.productlevel = productlevel;
    }

    public String getProductpl() {
        return productpl;
    }

    public void setProductpl(String productpl) {
        this.productpl = productpl;
    }

    public String getDostandard() {
        return dostandard;
    }

    public void setDostandard(String dostandard) {
        this.dostandard = dostandard;
    }

    public String getDotime() {
        return dotime;
    }

    public void setDotime(String dotime) {
        this.dotime = dotime;
    }

    public String getEncapsulationstate() {
        return encapsulationstate;
    }

    public void setEncapsulationstate(String encapsulationstate) {
        this.encapsulationstate = encapsulationstate;
    }

    public String getRvandfc() {
        return rvandfc;
    }

    public void setRvandfc(String rvandfc) {
        this.rvandfc = rvandfc;
    }

    public String getSendaddress() {
        return sendaddress;
    }

    public void setSendaddress(String sendaddress) {
        this.sendaddress = sendaddress;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getSammanname() {
        return sammanname;
    }

    public void setSammanname(String sammanname) {
        this.sammanname = sammanname;
    }

    public String getSamplingman() {
        return samplingman;
    }

    public void setSamplingman(String samplingman) {
        this.samplingman = samplingman;
    }

    public String getSamplingaddress() {
        return samplingaddress;
    }

    public void setSamplingaddress(String samplingaddress) {
        this.samplingaddress = samplingaddress;
    }

    public String getSamplingtel() {
        return samplingtel;
    }

    public void setSamplingtel(String samplingtel) {
        this.samplingtel = samplingtel;
    }

    public String getSamplingemail() {
        return samplingemail;
    }

    public void setSamplingemail(String samplingemail) {
        this.samplingemail = samplingemail;
    }

    public String getSamplingcode() {
        return samplingcode;
    }

    public void setSamplingcode(String samplingcode) {
        this.samplingcode = samplingcode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRecordnumber() {
        return recordnumber;
    }

    public void setRecordnumber(String recordnumber) {
        this.recordnumber = recordnumber;
    }

    public String getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(String unitprice) {
        this.unitprice = unitprice;
    }

    public String getGetreporttype() {
        return getreporttype;
    }

    public void setGetreporttype(String getreporttype) {
        this.getreporttype = getreporttype;
    }

    public String getEnddotype() {
        return enddotype;
    }

    public void setEnddotype(String enddotype) {
        this.enddotype = enddotype;
    }

    public String getReportcode() {
        return reportcode;
    }

    public void setReportcode(String reportcode) {
        this.reportcode = reportcode;
    }

    public String getTaskcode() {
        return taskcode;
    }

    public void setTaskcode(String taskcode) {
        this.taskcode = taskcode;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getProducesamlltype() {
        return producesamlltype;
    }

    public void setProducesamlltype(String producesamlltype) {
        this.producesamlltype = producesamlltype;
    }

    public String getFillInDate() {
        return fillInDate;
    }

    public void setFillInDate(String fillInDate) {
        this.fillInDate = fillInDate;
    }

    public String getSamplingbaseandbatch() {
        return samplingbaseandbatch;
    }

    public void setSamplingbaseandbatch(String samplingbaseandbatch) {
        this.samplingbaseandbatch = samplingbaseandbatch;
    }
}
