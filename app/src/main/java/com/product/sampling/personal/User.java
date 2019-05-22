package com.product.sampling.personal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用戶信息
 * Created by gongdongyang on 2018/9/25.
 */

public class User implements Parcelable {

    private String email;//email
    private int accountGroup;//用来做负载均衡类似用的，比如
    private boolean twoFactorVerified;//谷歌验证是否通过
    private boolean twoFactorRequired;//表示是否需要谷歌验证
    private String authorization;//是用户身份标示
//    private boolean isEmailVerified;//表示是否邮箱验证通过
//    private boolean canTrade;//表示是否可以交易
//    private boolean canWithDraw;//表示是否可以提币
//    private String maxWithdrawalBtcValue;//表示最大提币btc数量
    private int kycLevel;//用户kyc等级
    private String username;//用户名

    private String emailOrPhone;
    private boolean isEmail;
    private String userId;
    private String accountId;
//    private String passwordStrength;
    private boolean dataFeeTermAccepted;
//    private String accountType;
//    private String dailyWithdrawLimitInBtc;
//    private String makerMiningOption;
    private boolean marginRiskTermsAccepted;//切换10倍时候 是否显示答题页面
    //表示是否接受过杠杆风险提示
    private int marginTermsAccepted = -1; // 0 新用户需要跳完整协议 1 老用户没有签协议 2 已经同意
    private String cookie;//
    private double btmxUnlockSpeed; //账户btmx待入账加速等级 默认1倍



    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAccountGroup() {
        return accountGroup;
    }

    public boolean isTwoFactorVerified() {
        return twoFactorVerified;
    }


    public double getBtmxUnlockSpeed() {
        return btmxUnlockSpeed;
    }
    public boolean isTwoFactorRequired() {
        return twoFactorRequired;
    }

    public void setTwoFactorRequired(boolean twoFactorRequired) {
        this.twoFactorRequired = twoFactorRequired;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public int getMarginTermsAccepted() {
        return marginTermsAccepted;
    }

    public int getKycLevel() {
        return kycLevel;
    }

    public void setKycLevel(int kycLevel) {
        this.kycLevel = kycLevel;
    }



    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public boolean isEmail() {
        return isEmail;
    }

    public void setEmail(boolean email) {
        isEmail = email;
    }

    public String getUserId() {
        return userId;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountId() {
        return accountId;
    }



    public boolean isDataFeeTermAccepted() {
        return dataFeeTermAccepted;
    }


    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }


    public boolean isMarginRiskTermsAccepted() {
        return marginRiskTermsAccepted;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeInt(this.accountGroup);
        dest.writeByte(this.twoFactorVerified ? (byte) 1 : (byte) 0);
        dest.writeByte(this.twoFactorRequired ? (byte) 1 : (byte) 0);
        dest.writeString(this.authorization);
        dest.writeInt(this.kycLevel);
        dest.writeString(this.username);
        dest.writeString(this.emailOrPhone);
        dest.writeByte(this.isEmail ? (byte) 1 : (byte) 0);
        dest.writeString(this.userId);
        dest.writeString(this.accountId);
        dest.writeByte(this.dataFeeTermAccepted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.marginRiskTermsAccepted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.marginTermsAccepted);
        dest.writeString(this.cookie);
        dest.writeDouble(this.btmxUnlockSpeed);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.accountGroup = in.readInt();
        this.twoFactorVerified = in.readByte() != 0;
        this.twoFactorRequired = in.readByte() != 0;
        this.authorization = in.readString();
        this.kycLevel = in.readInt();
        this.username = in.readString();
        this.emailOrPhone = in.readString();
        this.isEmail = in.readByte() != 0;
        this.userId = in.readString();
        this.accountId = in.readString();
        this.dataFeeTermAccepted = in.readByte() != 0;
        this.marginRiskTermsAccepted = in.readByte() != 0;
        this.marginTermsAccepted = in.readInt();
        this.cookie = in.readString();
        this.btmxUnlockSpeed = in.readDouble();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
