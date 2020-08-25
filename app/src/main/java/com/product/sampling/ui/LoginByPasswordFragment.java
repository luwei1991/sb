package com.product.sampling.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.product.sampling.R;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.base.BaseFragment;
import com.product.sampling.utils.ActivityUtils;
import com.product.sampling.utils.KeyboardUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import io.reactivex.disposables.Disposable;

public class LoginByPasswordFragment extends BaseFragment implements View.OnClickListener {

    private View mRootView;
    private EditText mAccountView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    CheckBox checkBox;
    QMUITipDialog loginDialog;
    Button mEmailSignInButton;

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
                    return false;
                }
                return false;
            }
        });

        mEmailSignInButton = mRootView.findViewById(R.id.email_sign_in_button);
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
    public void onDestroyView() {
        super.onDestroyView();
        if(loginDialog != null){
            loginDialog.dismiss();
        }
    }



    private void attemptLogin() {
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
//            showProgress(true);
//            showLoginDialog();
            loginRequest(email, password);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

    private void showLoginDialog(){
        if(getActivity() != null && !getActivity().isFinishing()){
            loginDialog = new QMUITipDialog.Builder(getActivity())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("登录中...")
                    .create(true);
            loginDialog.show();
        }

    }

    public void showLoginFail(String failString){
        Dialog failDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(failString)
                .create();
        failDialog.show();


        mEmailSignInButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

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
        showLoginDialog();
        String verCode = QMUIPackageHelper.getAppVersion(getContext());
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .loginByPwd(account, pwd,verCode)
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .compose(RxSchedulersHelper.io_main())
                .subscribe(new ZBaseObserver<UserInfoBean>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        loginDialog.dismiss();
//                        if (!ExecptionEngin.isNetWorkError(code)) {
//                           showLoginFail(message);
//                        }
                    }

                    @Override
                    public void onSuccess(UserInfoBean userbean) {
                        loginDialog.dismiss();
//                        showLoginSuccess();
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

    @Override
    public void onClick(View v) {

    }
}
