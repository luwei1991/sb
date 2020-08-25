package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.product.sampling.R;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.eventmessage.CurFragmentMessage;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.view.SingleClick;

import org.greenrobot.eventbus.EventBus;

/**
 * 任务列表
 */
public class MainTaskListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    Fragment taskToDoFragment = TaskListFragment.getInstance();//0待办 1退回 2已上传
    Fragment taskBackFragment = TaskListFragment.getInstance();
    Fragment taskHasUpLoadedFragment = TaskListFragment.getInstance();
    Fragment taskLocalFragment = TaskListFragment.getInstance();

    Fragment myinfoFragment = MyInfoFragment.newInstance();
    TaskDetailViewModel taskDetailViewModel;
    Toolbar toolbar;
    LinearLayout llBack;
    /**
     * 常量代办
     */
    public static final String TASK_TO_DO = "taskToDoFragment";
    /**
     * 常量信息符合
     */
    private static final String TASK_BACK = "taskBackFragment";
    /**
     * 常量已上传
     */
    private static final String TASK_HAS_UPLOAD = "taskHasUpLoadedFragment";
    /**
     * 常量未上传
     */
    private static final String TASK_LOCAL = "taskLocalFragment";
    /**
     * 常量我的
     */
    private static final String MY_INFO = "myinfoFragment";

    private FragmentManager mFragmentManager;


    @Override
    public void setUIController(Object sc) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //解决重叠问题
        mFragmentManager = this.getSupportFragmentManager();
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
            savedInstanceState.remove("android:fragments");
        }
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_task_list, null);
        setContentView(root);
//        setContentView(R.layout.activity_task_list);
        findToolBar();
        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        RadioGroup rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SingleClick(500)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb1) {
                    EventBus.getDefault().postSticky(new CurFragmentMessage(TASK_TO_DO));
                    switchFragment(taskToDoFragment == null ? TaskListFragment.getInstance():taskToDoFragment).commit();//TaskListFragment.newInstance("待办任务", "0"):taskToDoFragment).commit();
                    taskDetailViewModel.taskListTag.setValue("0");
                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {
                    EventBus.getDefault().postSticky(new CurFragmentMessage(TASK_BACK));
                    switchFragment(taskBackFragment == null?TaskListFragment.getInstance():taskBackFragment).commit();//TaskListFragment.newInstance("信息复核", "1"):taskBackFragment).commit();
                    taskDetailViewModel.taskListTag.setValue("1");
                } else if (group.getCheckedRadioButtonId() == R.id.rb3) {
                    EventBus.getDefault().postSticky(new CurFragmentMessage(TASK_LOCAL));
                    switchFragment(taskLocalFragment == null? TaskListFragment.getInstance() :taskLocalFragment).commit();//TaskListFragment.newInstance("未上传", "-1") :taskLocalFragment).commit();
                    taskDetailViewModel.taskListTag.setValue("-1");
                } else if (group.getCheckedRadioButtonId() == R.id.rb4) {
                    EventBus.getDefault().postSticky(new CurFragmentMessage(TASK_HAS_UPLOAD));
                    switchFragment(taskHasUpLoadedFragment == null?TaskListFragment.getInstance():taskHasUpLoadedFragment).commit();//TaskListFragment.newInstance("已上传", "2"):taskHasUpLoadedFragment).commit();
                    taskDetailViewModel.taskListTag.setValue("2");
                } else if (group.getCheckedRadioButtonId() == R.id.rb5) {
                    EventBus.getDefault().postSticky(new CurFragmentMessage(MY_INFO));
                    switchFragment(myinfoFragment== null?MyInfoFragment.newInstance():myinfoFragment).commit();
                }
            }
        });
        if(savedInstanceState == null){
            RadioButton radioButton = (RadioButton) rb.findViewById(R.id.rb1);
            radioButton.setChecked(true);
        }
        if(savedInstanceState != null){
            String tag = savedInstanceState.getString("tag");
            if(tag != null){
                switch (tag){
                    case "0":
                        RadioButton radioButton = findViewById(R.id.rb1);
                        radioButton.setChecked(true);
                        break;
                    case "1":
                        RadioButton radioButton2 = findViewById(R.id.rb2);
                        radioButton2.setChecked(true);
                        break;
                    case "2":
                        RadioButton radioButton4 = findViewById(R.id.rb4);
                        radioButton4.setChecked(true);
                        break;
                    case "-1":
                        RadioButton radioButton3 = findViewById(R.id.rb3);
                        radioButton3.setChecked(true);
                        break;
                }
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public void findToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            llBack = findViewById(R.id.ll_back);
            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.item_detail_container);
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null && currentFragment.isAdded()) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.item_detail_container, targetFragment, targetFragment.getClass().getName());

        } else {

            if(currentFragment.isAdded()){
                transaction.hide(currentFragment).show(targetFragment);
            }

        }
        return transaction;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tag",taskDetailViewModel.taskListTag.getValue());
    }

}
