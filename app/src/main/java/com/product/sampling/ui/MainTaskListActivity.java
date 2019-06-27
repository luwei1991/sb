package com.product.sampling.ui;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.product.sampling.R;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 任务列表
 */
public class MainTaskListActivity extends BaseActivity implements AMapLocationListener {
    private int LOACTION_REQUEST_CODE = 1001;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    static Fragment taskToDoFragment = TaskListFragment.newInstance("待办任务", "0");//0待办 1退回 2已上传
    static Fragment taskBackFragment = TaskListFragment.newInstance("退回", "1");
    static Fragment taskHasUpLoadedFragment = TaskListFragment.newInstance("已上传", "2");
    static Fragment taskLocalFragment = TaskListFragment.newInstance("未上传", "-1");

    static Fragment myinfoFragment = MyInfoFragment.newInstance();
    TaskDetailViewModel taskDetailViewModel;

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        taskDetailViewModel = ViewModelProviders.of(MainTaskListActivity.this).get(TaskDetailViewModel.class);
        taskDetailViewModel.requestCityList();
        RadioGroup rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (group.getCheckedRadioButtonId() == R.id.rb1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, taskToDoFragment)
                            .commit();
                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, taskBackFragment)
                            .commit();
                } else if (group.getCheckedRadioButtonId() == R.id.rb3) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, taskLocalFragment)
                            .commit();

                } else if (group.getCheckedRadioButtonId() == R.id.rb4) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, taskHasUpLoadedFragment)
                            .commit();
                } else if (group.getCheckedRadioButtonId() == R.id.rb5) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, myinfoFragment)
                            .commit();
                }
            }
        });
        RadioButton radioButton = (RadioButton) rb.findViewById(R.id.rb1);
        radioButton.setChecked(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, taskToDoFragment)
                .commit();
        //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）

        if (EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission_group.LOCATION)) {
            locationSetting();
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
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == LOACTION_REQUEST_CODE) {
            locationSetting();
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

    private void locationSetting() {
        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(60*2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                Log.e("amapLocation", amapLocation.toString());
                MainApplication.INSTANCE.setMyLocation(amapLocation);

                taskDetailViewModel.uploadaddress(AccountManager.getInstance().getUserId(), amapLocation.getLatitude(), amapLocation.getLongitude());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                if (amapLocation.getErrorCode() == 12) {
                    Toast.makeText(MainTaskListActivity.this, "缺少定位权限,请到设置->安全和隐私->定位服务,打开允许访问我的位置信息", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
