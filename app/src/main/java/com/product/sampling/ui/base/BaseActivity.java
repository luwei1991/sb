package com.product.sampling.ui.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;
import com.product.sampling.R;
import com.product.sampling.agore.VideoMainActivity;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.ui.ActivityManager;
import com.product.sampling.ui.LocalTaskListManager;
import com.product.sampling.ui.LoginActivity;
import com.product.sampling.ui.MainActivity;
import com.product.sampling.utils.AMapHelper;
import com.product.sampling.utils.SPUtil;
import com.product.sampling.utils.Utils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

/**
 * 创建时间：2018/5/26
 * 描述：
 */
public abstract class BaseActivity<T> extends QMUIActivity{
    protected int LOCATION_REQUEST_CODE = 1001;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    protected static String BACKGROUND_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    protected String [] needPermissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            BACKGROUND_LOCATION_PERMISSION
    };
    protected RtmClient mRtmClient;
    private TextView textView;




    @Override
    protected boolean canDragBack() {
        return false;
    }


//    @Override
//    public void setContentView(int layoutResID) {
////        super.setContentView(layoutResID);
//
//        //
//    }
//


    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimaryDark), 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    abstract public void setUIController(T sc);



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager am = ActivityManager.getInstance();
        am.pushActivity(this);
        setStatusBar();
        initRtm();
        String rtcid = AccountManager.getInstance().getRtcid();

        //链接rtm
        mRtmClient.login(null, rtcid, new ResultCallback<Void>() {
            Boolean loginStatus=false;
            @Override
            public void onSuccess(Void responseInfo) {
                loginStatus = true;
            }
            @Override
            public void onFailure(ErrorInfo errorInfo) {
                loginStatus = false;
            }
        });
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
    protected void onStop() {
        super.onStop();
//        //如果app已经切入到后台，启动后台定位功能
//        boolean isBackground = ((MainApplication)getApplication()).isBackground();
//        if(isBackground){
//            AMapLocationClient locationClient = AMapHelper.getInstance(this.getApplicationContext()).getLocationClient();
//            if(null != locationClient) {
//                locationClient.enableBackgroundLocation(2001, buildNotification());
//            }
//        }
    }

    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    @SuppressLint("NewApi")
    private Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Utils.getAppName(this))
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }



    @Override
    protected void onResume() {
        super.onResume();
//        //切入前台后关闭后台定位功能
//        AMapLocationClient locationClient = AMapHelper.getInstance(this.getApplicationContext()).getLocationClient();
//        if(null != locationClient) {
//            locationClient.disableBackgroundLocation(true);
//        }

        AMapHelper.getInstance(this.getApplicationContext());
    }


    @Override
    protected void onPause() {
        super.onPause();
    }




//    @Override
//    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        if (requestCode == LOCATION_REQUEST_CODE) {
//
//        }
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        showToast("权限被拒");
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            finish();
//        }
//
//    }








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



//    public void findToolBarMain() {
//        toolbar = findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            tvLoginOut = findViewById(R.id.tv_login_out);
//            tvUserName = findViewById(R.id.tv_user_name);
//            tvVersion = findViewById(R.id.tv_version);
//            tvVersion.setText("v:"+QMUIPackageHelper.getAppVersion(this));
//            UserInfoBean userInfoBean = AccountManager.getInstance().getUserInfoBean();
//            if (null != userInfoBean) {
//                String name = userInfoBean.getName();
//                if (null != name && !TextUtils.isEmpty(name)) {
//                    tvUserName.setText(name);
//                }
//            }
//
//            tvLoginOut.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showLoginOutDialog();
//                }
//            });
//        }
//    }



//    public void findToolBar() {
//        toolbar = findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            tvLoginOut = findViewById(R.id.tv_login_out);
//            tvUserName = findViewById(R.id.tv_user_name);
//            llBack = findViewById(R.id.ll_back);
//            llBack.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//            UserInfoBean userInfoBean = AccountManager.getInstance().getUserInfoBean();
//            if (null != userInfoBean) {
//                String name = userInfoBean.getName();
//                if (null != name && !TextUtils.isEmpty(name)) {
//                    tvUserName.setText(name);
//                }
//            }
//
//            tvLoginOut.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showSimpleDialog("确定退出登录吗,退出将会清除本地未上传数据!!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            mRtmClient.logout(null);
//                            AccountManager.getInstance().clearUserInfo();
//                            popAllActivity();
//                            SPUtil.clear(v.getContext());
//                            LocalTaskListManager.getInstance().clear();
//                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
//
//                        }
//                    });
//                }
//            });
//        }
//    }
    public void initRtm() {
        try {
            mRtmClient = RtmClient.createInstance(this.getApplicationContext(), getString(R.string.agora_app_id),
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
