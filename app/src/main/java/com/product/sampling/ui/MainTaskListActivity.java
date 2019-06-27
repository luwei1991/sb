package com.product.sampling.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.product.sampling.R;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 任务列表
 */
public class MainTaskListActivity extends BaseActivity implements AMapLocationListener {

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
        requestLocation(this);
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

                taskDetailViewModel.uploadaddress(AccountManager.getInstance().getUserId(),amapLocation.getLatitude(),amapLocation.getLongitude());
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
