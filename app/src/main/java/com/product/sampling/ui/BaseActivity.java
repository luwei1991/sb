package com.product.sampling.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amap.api.location.AMapLocationListener;
import com.jaeger.library.StatusBarUtil;
import com.product.sampling.R;
import com.product.sampling.agore.VideoMainActivity;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.utils.GdLocationUtil;
import com.product.sampling.utils.SPUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 创建时间：2018/5/26
 * 描述：
 */
public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    AMapLocationListener aMapLocationListener;
    private int LOACTION_REQUEST_CODE = 1001;
    RtmClient mRtmClient;
    Vibrator vibrator;
    private TextView textView;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
        findToolBar();
        initRtm();
        String rtcid=AccountManager.getInstance().getRtcid();

        mRtmClient.login(null, rtcid, new ResultCallback<Void>() {
            Boolean loginStatus=false;
            @Override
            public void onSuccess(Void responseInfo) {
                loginStatus = true;
                Log.d("loginStatus", "login success!");
            }
            @Override
            public void onFailure(ErrorInfo errorInfo) {
                loginStatus = false;
                Log.d("loginStatus", "login failure!");
            }
        });
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimaryDark), 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager am = ActivityManager.getInstance();
        am.pushActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager am = ActivityManager.getInstance();
        am.popActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager am = ActivityManager.getInstance();
        am.clearRecord(this);
    }

    public void popAllActivity() {
        ActivityManager am = ActivityManager.getInstance();
        am.popAllActivity();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 请求定位权限
     */
    public void requestLocation(AMapLocationListener aMapLocationListener) {
        this.aMapLocationListener = aMapLocationListener;
        if (EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission_group.LOCATION)) {
            initLocation();
        } else {
            EasyPermissions.requestPermissions(this, "请允许app使用定位功能", 1001, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发到EasyPermissions
        if (requestCode == LOACTION_REQUEST_CODE) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initLocation() {
        GdLocationUtil.getInstance().startContinueLocation(aMapLocationListener);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == LOACTION_REQUEST_CODE) {
            initLocation();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToast("权限被拒");
        if (requestCode == LOACTION_REQUEST_CODE) {
            aMapLocationListener.onLocationChanged(null);
            aMapLocationListener = null;
        }

    }

    /**
     * 显示进度框
     */
    ProgressDialog progDialog;

    public void showProgressDialog(String title) {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(title);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    public void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    public void showSimpleDialog(String title, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this).setTitle(title)
                .setPositiveButton("确定", listener).setNegativeButton("取消", null).show();
    }

    public void showToast(String toast) {
        com.product.sampling.maputil.ToastUtil.show(this, toast);
    }

    public void showShortToast(String toast) {
        com.product.sampling.maputil.ToastUtil.showShortToast(this, toast);
    }

    Toolbar toolbar;
    TextView tvUserName;
    TextView tvLoginOut;

    public void findToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            tvLoginOut = findViewById(R.id.tv_login_out);
            tvUserName = findViewById(R.id.tv_user_name);
            UserInfoBean userInfoBean = AccountManager.getInstance().getUserInfoBean();
            if (null != userInfoBean) {
                String name = userInfoBean.getName();
                if (null != name && !TextUtils.isEmpty(name)) {
                    tvUserName.setText(name);
                }
            }

            tvLoginOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSimpleDialog("确定退出登录吗,退出将会清除本地未上传数据!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mRtmClient.logout(null);
                            AccountManager.getInstance().clearUserInfo();
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                            popAllActivity();
                            SPUtil.clear(v.getContext());
                        }
                    });
                }
            });
        }
    }
    public void initRtm() {
        try {
            mRtmClient = RtmClient.createInstance(this, getString(R.string.agora_app_id),
                    new RtmClientListener() {
                        @Override
                        public void onConnectionStateChanged(int state, int reason) {
                            Log.d("videoReason", "Connection state changes to "
                                    + state + " reason: " + reason);
                        }

                        @Override
                        public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                            String msg = rtmMessage.getText();
                            Log.d("videoReason", "Message received " + " from " + peerId + msg);
                            String[]data=msg.split(",");
                            String type=data[0];
                     /*       vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

                            long[] patter = {1000, 1000, 1000, 1000, 1000};
                            vibrator.vibrate(patter, 0);*/
                            Bundle bundle = new Bundle();
                            if("0".equals(type)) {

                                String channel=data[1];
                                String token=data[2];
                                String contactname=data[3];

                                bundle.putString("channel",channel);
                                bundle.putString("contactname",contactname);
                                bundle.putString("token",token);

                                startActivity(new Intent(BaseActivity.this, VideoMainActivity.class).putExtras(bundle));
                            }else{

                            }
                            if("1".equals(type)){
                                textView= findViewById(R.id.conname);
                                textView.setText("对方已挂断请退出");
                                bundle.putString("type","1");
                                startActivity(new Intent(BaseActivity.this, VideoMainActivity.class).putExtras(bundle));
                            }

                        }
                        @Override
                        public  void onTokenExpired(){

                        }
                    });
        } catch (Exception e) {
            Log.d("videoReason", "RTM SDK init fatal error!");
            throw new RuntimeException("You need to check the RTM init process.");
        }
    }

}
