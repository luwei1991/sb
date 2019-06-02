package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TaskEntity implements Parcelable {
    public String id;//任务唯一id
    public String planid;//所属计划id
    public String companyid;//抽样企业id
    public String remark;//任务备注
    public String starttime;//任务开始时间
    public String endtime;//任务结束时间

    public String taskstatus;//任务状态 0待办1本地保存2上传3退回修改4已确认5停用
    public String doman;//抽样人

    public String tasktypecount;//抽样产品名
    public String taskCode;//任务编号
    public String planname;//所属计划名
    public String companyname;//企业名

    public String goodscount;//计划抽样批次总数
    public String companyaddress;//企业地址
    public String taskisok;//任务异常状态 0抽样正常1企业拒检2未检测到样品
    public String companyperson;//企业联系人
    public String companytel;//企业联系号码

    public String businesslicence;//企业营业执照
    public String planno;//计划备案号
    public String planfrom;//计划来源
    public String groupPersonCount;//任务每组人数

    public String areasheng;//企业所在省
    public String areashi;//企业所在市
    public String areaqu;//企业所在区/县
    public String taskAddress;//任务提交时的地址
    public String taskTime;//任务提交时间

    public String longitude;//任务提交时的经度
    public String latitude;//任务提交时的维度
    public int leftday;//任务剩余天数

    public List<Pics> pics = new ArrayList<>();
    public List<Videos> voides = new ArrayList<>();


    public boolean isNewRecord;


    public TaskEntity() {

    }

    public TaskEntity(Parcel in) {
        id = in.readString();
        planid = in.readString();
        companyid = in.readString();
        remark = in.readString();
        starttime = in.readString();
        endtime = in.readString();
        taskstatus = in.readString();
        doman = in.readString();
        tasktypecount = in.readString();
        taskCode = in.readString();
        planname = in.readString();
        companyname = in.readString();
        goodscount = in.readString();
        companyaddress = in.readString();
        taskisok = in.readString();
        companyperson = in.readString();
        companytel = in.readString();
        businesslicence = in.readString();
        planno = in.readString();
        planfrom = in.readString();
        groupPersonCount = in.readString();
        areasheng = in.readString();
        areashi = in.readString();
        areaqu = in.readString();
        taskAddress = in.readString();
        taskTime = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    public static final Creator<TaskEntity> CREATOR = new Creator<TaskEntity>() {
        @Override
        public TaskEntity createFromParcel(Parcel in) {
            return new TaskEntity(in);
        }

        @Override
        public TaskEntity[] newArray(int size) {
            return new TaskEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(planid);
        dest.writeString(companyid);
        dest.writeString(remark);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeString(taskstatus);
        dest.writeString(doman);
        dest.writeString(tasktypecount);
        dest.writeString(taskCode);
        dest.writeString(planname);
        dest.writeString(companyname);
        dest.writeString(goodscount);
        dest.writeString(companyaddress);
        dest.writeString(taskisok);
        dest.writeString(companyperson);
        dest.writeString(companytel);
        dest.writeString(businesslicence);
        dest.writeString(planno);
        dest.writeString(planfrom);
        dest.writeString(groupPersonCount);
        dest.writeString(areasheng);
        dest.writeString(areashi);
        dest.writeString(areaqu);
        dest.writeString(taskAddress);
        dest.writeString(taskTime);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }

    public static class Videos extends LocalMediaInfo {

        /**
         * id : 00e5f055a9c84156b58e38fddd36fa46
         * isNewRecord : false
         * remarks :
         * createDate : 2019-05-30 15:09:51
         * updateDate : 2019-05-30 15:09:51
         * fileName : VID_20180810_185944.mp4
         * fileSize : 5598470
         * fileType : .mp4
         * folderPath : /d444d03c23ca4a75aae89c81dbcbcdf6
         * isMarge : null
         * belongID : a6ff0f67783f41cdbb3dfcace4f9c459
         * ftpFileName : 00e5f055a9c84156b58e38fddd36fa46.mp4
         */
        public Videos() {

        }

        private String id;
        private boolean isNewRecord;
        private String remarks = "";
        private String createDate;
        private String updateDate;
        private String fileName;
        private String fileSize;
        private String fileType;
        private String folderPath;
        private Object isMarge;
        private String belongID;
        private String ftpFileName;

        public boolean isLocal = false;

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

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFolderPath() {
            return folderPath;
        }

        public void setFolderPath(String folderPath) {
            this.folderPath = folderPath;
        }

        public Object getIsMarge() {
            return isMarge;
        }

        public void setIsMarge(Object isMarge) {
            this.isMarge = isMarge;
        }

        public String getBelongID() {
            return belongID;
        }

        public void setBelongID(String belongID) {
            this.belongID = belongID;
        }

        public String getFtpFileName() {
            return ftpFileName;
        }

        public void setFtpFileName(String ftpFileName) {
            this.ftpFileName = ftpFileName;
        }
    }

    public class Pics {

        /**
         * id : 764543eb7df24fe78d79c210555b96dd
         * isNewRecord : false
         * remarks :
         * createDate : 2019-05-30 15:09:51
         * updateDate : 2019-05-30 15:09:51
         * fileName : 1557456073738.jpg
         * fileSize : 8102
         * fileType : .jpg
         * folderPath : /d444d03c23ca4a75aae89c81dbcbcdf6
         * isMarge : null
         * belongID : 0cb2d6b25d09464fb7882252d2a6f168
         * ftpFileName : 764543eb7df24fe78d79c210555b96dd.jpg
         */


        // 0602
        /**
         * 	"pics": [{
         * 			"id": "1a69acb72420432cb687d47c7c53779a",
         * 			"isNewRecord": false,
         * 			"remarks": "中国尊",
         * 			"createDate": "2019-06-02 09:03:08",
         * 			"updateDate": "2019-06-02 09:03:08",
         * 			"fileName": "P90602-165603.jpg",
         * 			"fileSize": "1889786",
         * 			"fileType": ".jpg",
         * 			"folderPath": "/d444d03c23ca4a75aae89c81dbcbcdf6",
         * 			"isMarge": null,
         * 			"belongID": "0cb2d6b25d09464fb7882252d2a6f168",
         * 			"ftpFileName": "1a69acb72420432cb687d47c7c53779a.jpg",
         * 			"picorpdf": "0"
         *                }],
         */

        private String id;
        private boolean isNewRecord;
        private String remarks;
        private String createDate;
        private String updateDate;
        private String fileName;
        private String fileSize;
        private String fileType;
        private String folderPath;
        private Object isMarge;
        private String belongID;
        private String ftpFileName;
        private String picorpdf;

        public String getPicorpdf() {
            return picorpdf;
        }

        public void setPicorpdf(String picorpdf) {
            this.picorpdf = picorpdf;
        }

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

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFolderPath() {
            return folderPath;
        }

        public void setFolderPath(String folderPath) {
            this.folderPath = folderPath;
        }

        public Object getIsMarge() {
            return isMarge;
        }

        public void setIsMarge(Object isMarge) {
            this.isMarge = isMarge;
        }

        public String getBelongID() {
            return belongID;
        }

        public void setBelongID(String belongID) {
            this.belongID = belongID;
        }

        public String getFtpFileName() {
            return ftpFileName;
        }

        public void setFtpFileName(String ftpFileName) {
            this.ftpFileName = ftpFileName;
        }
    }
}

