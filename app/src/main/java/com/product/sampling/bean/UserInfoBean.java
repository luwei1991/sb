package com.product.sampling.bean;

/**
 * 创建时间：2018/7/3
 * 描述：
 */
public class UserInfoBean {

    private String userid;
    private String loginName;
    private String password;
    private String name;
    private String persontel;
    private String photo;
    private String rtcid;
    /**是否需要电子签章 0:需要 1：不需要*/
    private String isOpenCode = "1";//默认不需要

    public String getIsOpenCode() {
        return isOpenCode;
    }

    public void setIsOpenCode(String isOpenCode) {
        this.isOpenCode = isOpenCode;
    }

    private String account;

    private int remindtime = 60;//坐标间隔

    public String getRtcid() {
        return rtcid;
    }

    public void setRtcid(String rtcid) {
        this.rtcid = rtcid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersontel() {
        return persontel;
    }

    public void setPersontel(String persontel) {
        this.persontel = persontel;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getRemindtime() {
        return remindtime;
    }

    public void setRemindtime(int remindtime) {
        this.remindtime = remindtime;
    }
}
