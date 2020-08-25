package com.product.sampling.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.product.sampling.R;
import com.product.sampling.bean.ImageItem;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.view.SingleClick;

import java.util.List;

/**
 * 异常信息
 */
public class TaskExceptionActivity extends BaseActivity {
    TaskDetailViewModel taskDetailViewModel;
    TaskEntity task;
    TaskUnfindSampleFragment taskUnfindSampleFragment;
    TaskCompanyRefusedFragment taskCompanyRefusedFragment;
    Fragment currentFragment;
    Toolbar toolbar;
    LinearLayout llBack;

    @Override
    public void setUIController(Object sc) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_task_exception);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_task_exception, null);
        setContentView(root);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        findToolBar();
        Bundle bundle = getIntent().getExtras();    //得到传过来的bundle
        task = (TaskEntity) bundle.getSerializable("task");//读出数据

        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        taskDetailViewModel.taskEntity = task;
//        findTaskInLocalFile();
        taskUnfindSampleFragment = TaskUnfindSampleFragment.newInstance(taskDetailViewModel.taskEntity);
        taskCompanyRefusedFragment = TaskCompanyRefusedFragment.newInstance(taskDetailViewModel.taskEntity);
        RadioGroup rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SingleClick(500)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb1) {
                    switchFragment(taskUnfindSampleFragment == null ?  TaskUnfindSampleFragment.newInstance(taskDetailViewModel.taskEntity):taskUnfindSampleFragment).commit();
//                    switchContent(currentFragment, taskUnfindSampleFragment);
//                    currentFragment = taskUnfindSampleFragment;

                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {
                    switchFragment(taskCompanyRefusedFragment == null ?  TaskCompanyRefusedFragment.newInstance(taskDetailViewModel.taskEntity):taskCompanyRefusedFragment).commit();

//                    switchContent(currentFragment, taskCompanyRefusedFragment);
//                    currentFragment = taskCompanyRefusedFragment;
                }
            }
        });
        RadioButton radioButton = rb.findViewById(R.id.rb1);
        radioButton.setChecked(true);
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
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.item_detail_container, targetFragment, targetFragment.getClass().getName());

        } else {
            transaction.hide(currentFragment)
                    .show(targetFragment);


        }
        currentFragment = targetFragment;
        return transaction;
    }

    public void switchContent(Fragment from, Fragment to) {
        FragmentTransaction transactio = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().executePendingTransactions();
        if (!to.isAdded()) { // 先判断是否被add过,没有添加就添加
            if (from != null) {
                transactio.hide(from);
                transactio.addToBackStack(null);
            }
            transactio.add(R.id.item_detail_container, to);
            transactio.commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            if (from != null) {
                transactio.hide(from);
            }
//            to.onResume();//这里用户更新数据，不加这句就可以实现单例了，但是不会调用任何生命周期里的方法，不会更新数据
            transactio.show(to).commit(); // 隐藏当前的fragment，显示下一个
        }


    }

    private static ImageItem createItem(int position) {
        String text = "";
        int res = 0;
        switch (position) {
            case 0:
                text = "未抽到样品";
                res = R.mipmap.menu_icon5;
                break;
            case 1:
                text = "企业拒检";
                res = R.mipmap.menu_icon6;
                break;
        }
        return new ImageItem(text, res);
    }

    List<TaskEntity> listTask;

//    void findTaskInLocalFile() {
//        Gson gson = new Gson();
//        String taskListStr = (String) SPUtil.get(this, "tasklist", "");
//        if (!TextUtils.isEmpty(taskListStr)) {
//            Type listType = new TypeToken<List<TaskEntity>>() {
//            }.getType();
//            listTask = gson.fromJson(taskListStr, listType);
//            if (null != listTask && !listTask.isEmpty()) {
//                for (int i = 0; i < listTask.size(); i++) {
//                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
//                        taskDetailViewModel.taskEntity = listTask.get(i);
//                    }
//                }
//                taskDetailViewModel.taskEntity.isLoadLocalData = true;
//            }
//        }
//
//    }

    @Override
    protected void doOnBackPressed() {
        super.doOnBackPressed();
        finish();
    }

    public static void GoExceptionActivity(Context context, TaskEntity taskEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", taskEntity);
        context.startActivity(new Intent(context, TaskExceptionActivity.class).putExtras(bundle));
    }
}
