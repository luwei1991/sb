package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pics extends TaskImageEntity implements Parcelable, Serializable {

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
     * "pics": [{
     * "id": "1a69acb72420432cb687d47c7c53779a",
     * "isNewRecord": false,
     * "remarks": "中国尊",
     * "createDate": "2019-06-02 09:03:08",
     * "updateDate": "2019-06-02 09:03:08",
     * "fileName": "P90602-165603.jpg",
     * "fileSize": "1889786",
     * "fileType": ".jpg",
     * "folderPath": "/d444d03c23ca4a75aae89c81dbcbcdf6",
     * "isMarge": null,
     * "belongID": "0cb2d6b25d09464fb7882252d2a6f168",
     * "ftpFileName": "1a69acb72420432cb687d47c7c53779a.jpg",
     * "picorpdf": "0"
     * }],
     */

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
    private String picorpdf;

    public Pics() {

    }

    protected Pics(Parcel in) {
        id = in.readString();
        isNewRecord = in.readByte() != 0;
        remarks = in.readString();
        createDate = in.readString();
        updateDate = in.readString();
        fileName = in.readString();
        fileSize = in.readString();
        fileType = in.readString();
        folderPath = in.readString();
        belongID = in.readString();
        ftpFileName = in.readString();
        picorpdf = in.readString();

    }

    public static final Creator<Pics> CREATOR = new Creator<Pics>() {
        @Override
        public Pics createFromParcel(Parcel in) {
            return new Pics(in);
        }

        @Override
        public Pics[] newArray(int size) {
            return new Pics[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (isNewRecord ? 1 : 0));
        dest.writeString(remarks);
        dest.writeString(createDate);
        dest.writeString(updateDate);
        dest.writeString(fileName);
        dest.writeString(fileSize);
        dest.writeString(fileType);
        dest.writeString(folderPath);
        dest.writeString(belongID);
        dest.writeString(ftpFileName);
        dest.writeString(picorpdf);
    }
}
