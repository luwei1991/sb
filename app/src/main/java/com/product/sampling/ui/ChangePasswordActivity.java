package com.product.sampling.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.product.sampling.R;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

/**
 * A login screen that offers login via phone.
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ProgressBar loginProgress;
    private ScrollView loginForm;
    private EditText etOldPassword;
    private EditText etPassword;
    private EditText etPasswordConfig;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        initView();
    }

    protected void initView() {
        loginProgress = findViewById(R.id.login_progress);
        loginForm = findViewById(R.id.login_form);
        etOldPassword = findViewById(R.id.et_old_password);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfig = findViewById(R.id.et_password_config);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmit.getId()) {
            attemptLogin();
        }
    }

    private void attemptLogin() {

        // Reset errors.
        etPassword.setError(null);
        etPasswordConfig.setError(null);
        etOldPassword.setError(null);
        // Store values at the time of the login attempt.
        String newPassword = etPassword.getText().toString();
        String configPassword = etPasswordConfig.getText().toString();
        String oldPassword = etOldPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(oldPassword) || !isPasswordValid(oldPassword)) {
            etOldPassword.setError(getString(R.string.error_invalid_password));
            focusView = etOldPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(newPassword) || !isPasswordValid(newPassword)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(configPassword) || !isPasswordValid(configPassword)) {
            etPasswordConfig.setError(getString(R.string.error_invalid_password));
            focusView = etPasswordConfig;
            cancel = true;
        }
        if (!newPassword.equals(configPassword)) {
            focusView = etPasswordConfig;
            etPasswordConfig.setError("新密码和确认密码不一致,请重试");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            changePwdRequest(oldPassword, configPassword);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
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

            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            loginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void changePwdRequest(String oldpwd, String pwd) {
        showProgress(true);
        RetrofitService.createApiService(Request.class)
                .changepassword(AccountManager.getInstance().getUserId(), oldpwd, pwd)
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        showProgress(false);
                        ToastUtils.showToast(message);
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {
                        showProgress(false);
                        ToastUtils.showToast(result.message);
                        finish();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
    }
}

