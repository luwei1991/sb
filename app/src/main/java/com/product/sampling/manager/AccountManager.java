package com.product.sampling.manager;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.product.sampling.MainApplication;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.utils.SPUtil;

import java.lang.reflect.Type;

/**
 * 创建时间：2018/7/3
 * 描述：用户账号管理类
 */
public class AccountManager {
    /**是否需要电子签章 0:需要 1：不需要*/
    public static final String OPEN_CODE_YES = "0";
    /**是否需要电子签章 0:需要 1：不需要*/
    public static final String OPEN_CODE_NO = "1";
    private UserInfoBean userInfoBean;
    private String userPhone;
    private String userPhoto;
    private String userId;
    private String rtcid;
    /**是否需要电子签章 0:需要 1：不需要*/
    private String isOpenCode;
    public String getUserPhone() {
        userInfoBean = getUserInfoBean();
        if (userInfoBean != null) {
            return userInfoBean.getPersontel();
        }
        return "";
    }

    public void setUserPhone(String userPhone) {
        userInfoBean = getUserInfoBean();
        if (!TextUtils.isEmpty(userPhone) && userInfoBean != null) {
            userInfoBean.setPersontel(userPhone);
            AccountManager.getInstance().setUserInfoBean(userInfoBean);
        }
        this.userPhone = userPhone;
    }

    public void setUserPhoto(String photo) {
        userInfoBean = getUserInfoBean();
        if (!TextUtils.isEmpty(userPhoto) && userInfoBean != null) {
            userInfoBean.setPhoto(userPhone);
            AccountManager.getInstance().setUserInfoBean(userInfoBean);
        }
        this.userPhoto = photo;
    }

    public String getIsOpenCode() {
        userInfoBean = getUserInfoBean();
        if (userInfoBean != null) {
            return String.valueOf(userInfoBean.getIsOpenCode());
        }
        return isOpenCode;
    }

    public void setIsOpenCode(String isOpenCode) {
        userInfoBean = getUserInfoBean();
        if (!TextUtils.isEmpty(isOpenCode) && userInfoBean != null) {
            userInfoBean.setIsOpenCode(isOpenCode);
            AccountManager.getInstance().setUserInfoBean(userInfoBean);
        }
        this.isOpenCode = isOpenCode;
    }

    public String getUserId() {
        userInfoBean = getUserInfoBean();
        if (userInfoBean != null) {
            return String.valueOf(userInfoBean.getUserid());
        }
        return userId;
    }
    public String getRtcid() {
        userInfoBean = getUserInfoBean();
        if (userInfoBean != null) {
            return String.valueOf(userInfoBean.getRtcid());
        }
        return userId;
    }
    public void setRtcid(String rtcid) {
        userInfoBean = getUserInfoBean();
        if (!TextUtils.isEmpty(rtcid) && userInfoBean != null) {
            userInfoBean.setRtcid(rtcid);
            AccountManager.getInstance().setUserInfoBean(userInfoBean);
        }
        this.rtcid= rtcid;
    }

    public int getInterval() {
        userInfoBean = getUserInfoBean();
        if (userInfoBean != null) {
            return userInfoBean.getRemindtime();
        }
        return 25;
    }


    private static final AccountManager ourInstance = new AccountManager();

    public static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {
    }

    public UserInfoBean getUserInfoBean() {
        String userinfo = SPUtil.get(MainApplication.INSTANCE, "userinfo", "").toString();
        if (TextUtils.isEmpty(userinfo)) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<UserInfoBean>() {
        }.getType();
        UserInfoBean userBean = gson.fromJson(userinfo, type);
        return userBean;
    }

    public String getUserAccount() {
        String account = SPUtil.get(MainApplication.INSTANCE, "account", "").toString();
        if (TextUtils.isEmpty(account)) {
            return null;
        }
        return account;
    }

    public void setUserAccount(String account) {
        SPUtil.put(MainApplication.INSTANCE, "account", account);
    }

    public void setUserSaveAccount(boolean isSave) {
        SPUtil.put(MainApplication.INSTANCE, "save", isSave);
    }

    public boolean getUserSaveAccount() {
        boolean isSave = (boolean) SPUtil.get(MainApplication.INSTANCE, "save", false);
        return isSave;
    }

    public String getUserPassword() {
        String account = SPUtil.get(MainApplication.INSTANCE, "password", "").toString();
        if (TextUtils.isEmpty(account)) {
            return null;
        }
        return account;
    }

    public void setUserPassword(String password) {
        SPUtil.put(MainApplication.INSTANCE, "password", password);
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
        Gson gson = new Gson();
        String json = gson.toJson(userInfoBean);
        SPUtil.put(MainApplication.INSTANCE, "userinfo", json);
    }

    public void clearUserInfo() {
        SPUtil.remove(MainApplication.INSTANCE, "userinfo");
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
