package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.product.sampling.R;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.utils.AMapHelper;
import com.product.sampling.utils.ActivityUtils;
import com.product.sampling.view.LeverageLockHintDialog;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{


    ViewPager mViewPager;
    LoginByPasswordFragment loginByPasswordFragment;
    LoginByPhoneFragment loginByPhoneFragment;

    @Override
    public void setUIController(Object sc) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
        setContentView(root);
//        setContentView(R.layout.activity_login);
//        requestLocation();
        UserInfoBean userInfoBean = AccountManager.getInstance().getUserInfoBean();
        if (null != userInfoBean) {
            ActivityUtils.goMainTaskActivity(LoginActivity.this);
            finish();
        }
        initView();
        requestLocation();
    }

    protected void initView() {

        mViewPager = findViewById(R.id.view_pager);
        loginByPasswordFragment = LoginByPasswordFragment.newInstance();
        loginByPhoneFragment = LoginByPhoneFragment.newInstance();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(loginByPasswordFragment);
        fragmentList.add(loginByPhoneFragment);

        List<String> titles = new ArrayList<>();
        titles.add("用户名密码登录");
        titles.add("手机验证码登录");

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(tabPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab tab = tabLayout.newTab().setText("用户名密码登录");
        tabLayout.addTab(tab);
        tab = tabLayout.newTab().setText("手机验证码登录");
        tabLayout.addTab(tab);
        tabLayout.setupWithViewPager(mViewPager);

        // ViewPager设置预加载个数
        mViewPager.setOffscreenPageLimit(4);
    }

    /**
     * 请求定位权限
     */
    public void requestLocation() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), needPermissions)) {
            AMapHelper.getInstance(this.getApplicationContext());
        } else {
            EasyPermissions.requestPermissions(this, "请允许app使用定位功能", LOCATION_REQUEST_CODE, needPermissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            AMapHelper.getInstance(this.getApplicationContext());
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发到EasyPermissions
        if (requestCode == LOCATION_REQUEST_CODE) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    private class TabPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> titles;
        private List<Fragment> list;

        TabPagerAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
            super(fm);
            this.list = list;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

            @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String resId = titles.get(position);
            return resId;
        }

    }


    private LeverageLockHintDialog mLockHintDialog;

    public void showLockHintDialog() {
        if (mLockHintDialog == null) {
            mLockHintDialog = new LeverageLockHintDialog(this);
        }
        try {
            mLockHintDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissLockHintDialog() {
        try {
            if (mLockHintDialog != null) {
                mLockHintDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

