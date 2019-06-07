package com.product.sampling.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.product.sampling.R;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.utils.ActivityUtils;
import com.product.sampling.view.LeverageLockHintDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    ViewPager mViewPager;
    LoginByPasswordFragment loginByPasswordFragment;
    LoginByPhoneFragment loginByPhoneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserInfoBean userInfoBean = AccountManager.getInstance().getUserInfoBean();
        if (null == userInfoBean) {
            initView();
        } else {
            ActivityUtils.goMainTaskActivity(LoginActivity.this);
            finish();
        }
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

