package com.product.sampling.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
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
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.httpmoudle.error.ExecptionEngin;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.utils.ActivityUtils;
import com.product.sampling.utils.KeyboardUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.disposables.Disposable;

public class LoginByPhoneFragment extends BaseFragment implements View.OnClickListener {

    private View mRootView;
    TextView mTextViewSendCode;
    private EditText mEditTextPhone;
    private EditText mEditTextCode;
    View mLoginFormView;
    View mProgressView;

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

        mLoginFormView = mRootView.findViewById(R.id.login_form);
        mProgressView = mRootView.findViewById(R.id.login_progress);
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
//                startActivity(new Intent(getActivity(), MainActivity.class));
                break;

        }
    }

    private void loginRequest() {
        showProgress(true);
        RetrofitService.createApiService(Request.class)
                .loginByPhone(mEditTextPhone.getText().toString().trim(), mEditTextCode.getText().toString().trim())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<UserInfoBean>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        showProgress(false);
                        ToastUtils.showToast(message);
                    }

                    @Override
                    public void onSuccess(UserInfoBean userbean) {
                        showProgress(false);
                        saveUserData(userbean);
                        ActivityUtils.goMainTaskActivity(getActivity());
                        getActivity().finish();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
    }

    private void getSmsCode() {
        showProgress(true);
        RetrofitService.createApiService(Request.class)
                .sendCode(mEditTextPhone.getText().toString().trim())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        showProgress(false);
                        ToastUtils.showToast(message);
                        if (myCountDownTimer != null) {
                            myCountDownTimer.cancel();
                        }
                        mTextViewSendCode.setText("重新获取");
                        mTextViewSendCode.setClickable(true);
                    }

                    @Override
                    public void onSuccess(String code) {
                        showProgress(false);
                        ToastUtils.showToast("验证码已发送"+code);
                        if (myCountDownTimer == null) {
                            myCountDownTimer = new MyCountDownTimer(90000, 1000);
                        }
                        myCountDownTimer.start();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
