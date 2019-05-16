package com.product.sampling.manager;

import android.text.TextUtils;

import com.product.sampling.bean.UserInfoBean;

/**
 * 创建时间：2018/7/3
 * 描述：用户账号管理类
 */
public class AccountManager {
    private UserInfoBean userInfoBean;
    private String userPhone;
    private String userId;

    public String getUserPhone() {
        if (userInfoBean != null) {
            return userInfoBean.getPersontel();
        }
        return "";
    }

    public void setUserPhone(String userPhone) {
        if (!TextUtils.isEmpty(userPhone) && userInfoBean != null) {
            userInfoBean.setPersontel(userPhone);
        }
        this.userPhone = userPhone;
    }

    public String getUserId() {
        if (userInfoBean != null) {
            return String.valueOf(userInfoBean.getUserid());
        }
        return userId;
    }


    private static final AccountManager ourInstance = new AccountManager();

    public static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean == null ? new UserInfoBean() : userInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

    /**
     * 判断是否首次登录
     *
     * @return
     */
    public boolean isFiristRun() {
        boolean isFisrtRun;
        if (userInfoBean != null && !TextUtils.isEmpty(userInfoBean.getPersontel())) {
            isFisrtRun = true;
        } else {
            isFisrtRun = false;
        }
        return isFisrtRun;
    }
}
