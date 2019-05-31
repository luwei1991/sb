package com.product.sampling.bean;

public class Pics extends TaskImageEntity {

    /**
     * id : 591d5d7aeb504c14866cbf47ae744fce
     * isNewRecord : false
     * remarks :
     * createDate : 2019-05-28 13:35:51
     * updateDate : 2019-05-28 13:35:51
     * fileName : IMG_20190528_203515.jpg
     * fileSize : 1622670
     * fileType : .jpg
     * folderPath : /d444d03c23ca4a75aae89c81dbcbcdf6
     * isMarge : null
     * belongID : 7777
     * ftpFileName : 591d5d7aeb504c14866cbf47ae744fce.jpg
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
