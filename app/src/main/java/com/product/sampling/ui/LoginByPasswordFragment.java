package com.product.sampling.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
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
import com.product.sampling.utils.SPUtil;
import com.product.sampling.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.disposables.Disposable;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class LoginByPasswordFragment extends BaseFragment implements View.OnClickListener {

    private View mRootView;
    private EditText mAccountView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    CheckBox checkBox;

    public static LoginByPasswordFragment newInstance() {
        return new LoginByPasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login_password, container, false);
        initView();
        KeyboardUtils.closeKeyboard(getActivity());
        return mRootView;
    }

    protected void initView() {
        if (mRootView == null) return;
        mAccountView = mRootView.findViewById(R.id.account);
        populateAutoComplete();

        mPasswordView = mRootView.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = mRootView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = mRootView.findViewById(R.id.login_form);
        mProgressView = mRootView.findViewById(R.id.login_progress);
        KeyboardUtils.closeKeyboard(getActivity());
        checkBox = mRootView.findViewById(R.id.checkBox);
        if (AccountManager.getInstance().getUserSaveAccount()) {
            checkBox.setChecked(true);
            String account = AccountManager.getInstance().getUserAccount();
            if (!TextUtils.isEmpty(account)) {
                mAccountView.setText(account + "");
            }
            String password = AccountManager.getInstance().getUserPassword();
            if (!TextUtils.isEmpty(password)) {
                mPasswordView.setText(password + "");
            }
        } else {
            checkBox.setChecked(false);
        }
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
        }
    }

    private void attemptLogin() {

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            loginRequest(email, password);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: 密码规则logic
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

    private void populateAutoComplete() {

    }

    private void loginRequest(String account, String pwd) {

        RetrofitService.createApiService(Request.class)
                .loginByPwd(account, pwd)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<UserInfoBean>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        showProgress(false);
                        if (!ExecptionEngin.isNetWorkError(code)) {
                            ToastUtil.showShortToast(getActivity(), message);
                        }
                    }

                    @Override
                    public void onSuccess(UserInfoBean userbean) {
                        if (checkBox.isChecked()) {
                            AccountManager.getInstance().setUserAccount(account);
                            AccountManager.getInstance().setUserPassword(pwd);
                            AccountManager.getInstance().setUserSaveAccount(true);
                        } else {
                            AccountManager.getInstance().setUserAccount("");
                            AccountManager.getInstance().setUserPassword("");
                            AccountManager.getInstance().setUserSaveAccount(false);
                        }
                        AccountManager.getInstance().setUserInfoBean(userbean);
                        ActivityUtils.goMainTaskActivity(getActivity());
                        getActivity().finish();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        KeyboardUtils.closeKeyboard(getActivity());
    }
}
