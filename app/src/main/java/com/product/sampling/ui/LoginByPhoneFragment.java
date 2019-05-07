package com.product.sampling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.product.sampling.R;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.utils.KeyboardUtils;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginByPhoneFragment extends BaseFragment implements View.OnClickListener {

    private View mRootView;
    TextView mTextViewSendCode;
    private EditText mEditTextPhone;
    private EditText mEditTextCode;

    private MyCountDownTimer myCountDownTimer;

    public static LoginByPhoneFragment newInstance() {
        return new LoginByPhoneFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login_phone, container, false);
        initView();
        return mRootView;
    }

    protected void initView() {
        if (mRootView == null) return;
        mTextViewSendCode = mRootView.findViewById(R.id.tv_code);
        mTextViewSendCode.setOnClickListener(this);
        mEditTextPhone = mRootView.findViewById(R.id.et_phone_num);
        mEditTextCode = mRootView.findViewById(R.id.et_ver_code);


        mRootView.findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.tv_code:
                KeyboardUtils.closeKeyboard(getActivity());
                if (validatePhoneNum()) {
                    getSmsCode();
                } else {
                    ToastUtils.showToast("请输入正确手机号");
                }
                break;
            case R.id.sign_in_button:
                loginRequest();

                break;

        }
    }

    private void loginRequest() {
        NetWorkManager.getRequest().loginByPhone(mEditTextPhone.getText().toString().trim(), mEditTextCode.getText().toString().trim())
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(userbean -> {
                    saveUserData(userbean);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }, throwable -> {
                    String displayMessage = ((ApiException) throwable).getDisplayMessage();
                    ToastUtils.showToast(displayMessage);
                });
    }

    private void getSmsCode() {

        NetWorkManager.getRequest().sendCode(mEditTextPhone.getText().toString().trim())
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(smsBean -> {
                    ToastUtils.showToast("验证码已发送" + smsBean);
                    if (myCountDownTimer == null) {
                        myCountDownTimer = new MyCountDownTimer(90000, 1000);
                    }
                    myCountDownTimer.start();
                }, throwable -> {
                    String displayMessage = ((ApiException) throwable).getDisplayMessage();
                    ToastUtils.showToast(displayMessage);
                    if (myCountDownTimer != null) {
                        myCountDownTimer.cancel();
                    }
                    mTextViewSendCode.setText("重新获取");
                    mTextViewSendCode.setClickable(true);
                });
    }
    private void saveUserData(UserInfoBean userInfoBean) {
        AccountManager.getInstance().setUserInfoBean(userInfoBean);
    }

    //倒计时函数
    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            mTextViewSendCode.setClickable(false);
            mTextViewSendCode.setText(l / 1000 + "秒");
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            mTextViewSendCode.setText("重新获取");
            mTextViewSendCode.setClickable(true);
        }
    }

    /**
     * 验证手机号码是否合法
     */
    private boolean validatePhoneNum() {
        boolean result;
        String phoneNum = mEditTextPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() != 11) {
            ToastUtils.showToast("请输入正确手机号");
            result = false;
        } else {
            result = true;
        }
        return result;
    }
}
