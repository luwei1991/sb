package com.product.sampling.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.product.sampling.R;
import com.product.sampling.bean.Advice;
import com.product.sampling.bean.Feed;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Risk;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.bean.Work;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtm.jni.LOGIN_ERR_CODE;

/**
 * 待办任务详情
 */
public class TaskDetailActivity extends BaseActivity {

    TaskEntity task = null;
    TaskDetailViewModel taskDetailViewModel;
    RadioGroup rb;
    TaskDetailFragment taskDetailFragment;
    TaskSceneFragment taskSceneFragment;
    TaskSampleFragment taskSampleFragment;
    Fragment currentFragment;
    QMUITipDialog loadingDialog;
    //任务状态-----代办
    public static final String TASK_TODO = "0";
    //任务状态-----未上传（本地）
    public static final String TASK_NOT_UPLOAD = "1";
    //任务状态-----已上传
    public static final String TASK_UPLOADED = "2";
    //任务状态-----信息复核
    public static final String TASK_CHECK = "3";
    //任务状态-----已确认
    public static final String TASK_CONFIM = "4";
    //任务状态-----停用
    public static final String TASK_STOP = "5";

    private FragmentManager mFragmentManager;
    Toolbar toolbar;
    LinearLayout llBack;

    @Override
    public void setUIController(Object sc) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //解决重叠问题
        mFragmentManager = this.getSupportFragmentManager();
        if(savedInstanceState != null){
            savedInstanceState.remove("android:support:fragments");
            savedInstanceState.remove("android:fragments");
        }
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_task_menu, null);
        setContentView(root);
//        setContentView(R.layout.activity_task_menu);
        findToolBar();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb1) {
                    switchFragment(taskDetailFragment == null ? TaskDetailFragment.newInstance(task):taskDetailFragment).commit();
                    taskDetailViewModel.taskDetailTag.setValue("0");
                    taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(),task.id);
                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {
                    switchFragment(taskSceneFragment == null ? TaskSceneFragment.newInstance(task):taskSceneFragment).commit();
                    taskDetailViewModel.taskDetailTag.setValue("1");
                } else if (group.getCheckedRadioButtonId() == R.id.rb3) {
                    switchFragment(taskSampleFragment == null ? TaskSampleFragment.newInstance(task):taskSampleFragment).commit();
                    taskDetailViewModel.taskDetailTag.setValue("2");
                }

            }
        });
        if(savedInstanceState != null){
           TaskEntity taskEntity = (TaskEntity) savedInstanceState.getSerializable("taskEntity");
           if(taskEntity != null){
               taskDetailViewModel.taskEntity = task = taskEntity;
           }
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
                        RadioButton radioButton3 = findViewById(R.id.rb3);
                        radioButton3.setChecked(true);
                        break;

                }
            }
        }else{
            Bundle bundle = getIntent().getExtras();    //得到传过来的bundle
            task = (TaskEntity) bundle.getSerializable("task");//读出数据
            taskDetailViewModel.taskEntity = task;
            //请求详细taskDetail
            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(),task.id);
            taskDetailFragment = TaskDetailFragment.newInstance(task);
            taskSceneFragment = TaskSceneFragment.newInstance(task);
            taskSampleFragment = TaskSampleFragment.newInstance(task);
        }



        //如果没有被系统回收，初始化执行
        if(savedInstanceState == null){
            RadioButton radioButton = (RadioButton) rb.findViewById(R.id.rb1);
            radioButton.setChecked(true);
        }

        //这里根据任务的状态进行判断是否要请求整个数据
        if(isRequestAllData()){
            showLoadingDialog();
            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
            taskDetailViewModel.orderDetailLiveData.observe(this,new Observer<LoadDataModel<TaskEntity>>() {
                @Override
                public void onChanged(LoadDataModel<TaskEntity> taskEntityLoadDataModel) {
                    //如果服务端有数据，本地数据为空，则显示服务端数据
                    if (taskEntityLoadDataModel.isSuccess()) {
                        taskDetailViewModel.taskEntity = taskEntityLoadDataModel.getData();
                        taskDetailViewModel.requestTasksamples(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
                    }
                }
            });

            taskDetailViewModel.sampleDetailLiveData.observe(this,new Observer<LoadDataModel<List<TaskSample>>>() {
                @Override
                public void onChanged(LoadDataModel<List<TaskSample>> taskEntityLoadDataModel) {
                    if (taskEntityLoadDataModel.isSuccess()) {
                        taskDetailViewModel.taskEntity.taskSamples = taskEntityLoadDataModel.getData();
                        taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
                        LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
                        EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id,false));
                        dismissLoadingDialog();
                    }
                }
            });

        }

    }
    public void findToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            llBack = findViewById(R.id.ll_back);
            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(taskDetailFragment != null){
                            mFragmentManager.beginTransaction().remove(taskDetailFragment).commit();
                        }
                        if(taskSceneFragment != null){
                            mFragmentManager.beginTransaction().remove(taskSceneFragment).commit();
                        }
                        if(taskSampleFragment != null){
                            mFragmentManager.beginTransaction().remove(taskSampleFragment).commit();
                        }
                    }catch (IllegalStateException e){

                    }
                    finish();
                }
            });
        }
    }

    /**
     * 显示loading
     */
    private void showLoadingDialog(){
        loadingDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中...")
                .create(true);
        loadingDialog.show();
    }

    /**
     * dismissloading
     */
    private void dismissLoadingDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }
    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction =  mFragmentManager.beginTransaction();
        if (!targetFragment.isAdded() && transaction != null) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null  && currentFragment.isAdded()) {
                transaction.hide(currentFragment);

            }
            transaction.add(R.id.item_detail_container, targetFragment, targetFragment.getClass().getName());

        } else {
            if(transaction != null && currentFragment != null && currentFragment.isAdded()){
                transaction.hide(currentFragment).show(targetFragment);
            }

        }
        currentFragment = targetFragment;
        return transaction;
    }

    /**
     * 是否需要请求所有数据（任务状态为信息复核并且本地没有该任务时候）
     * @return true 需要请求数据，  false :不需要请求数据
     */
    private boolean isRequestAllData(){
        boolean isCan = false;
        TaskEntity taskEntity = LocalTaskListManager.getInstance().query(task);
        if(taskEntity == null && task.taskstatus.equals(TASK_CHECK)){
            isCan = true;
        }
        return isCan;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("taskEntity",taskDetailViewModel.taskEntity);
        outState.putString("tag",taskDetailViewModel.taskDetailTag.getValue());
    }

    public static void GoTaskDetailActivity(Context context, TaskEntity taskEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", taskEntity);
        context.startActivity(new Intent(context, TaskDetailActivity.class).putExtras(bundle));
    }


}