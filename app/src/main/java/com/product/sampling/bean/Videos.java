package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Videos extends LocalMediaInfo implements Parcelable, Serializable {
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

    protected Videos(Parcel in) {
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
    }

    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };

    private String id;
    private boolean isNewRecord;
    private String remarks = " ";
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
