package com.product.sampling.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        Bundle bundle = getIntent().getExtras();    //得到传过来的bundle
        task = (TaskEntity) bundle.getSerializable("task");//读出数据
        taskDetailViewModel.taskEntity = task;

        taskDetailFragment = TaskDetailFragment.newInstance(task);
        taskSceneFragment = TaskSceneFragment.newInstance(task);
        taskSampleFragment = TaskSampleFragment.newInstance(task);

        rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb1) {
                    switchContent(currentFragment, taskDetailFragment);
                    currentFragment = taskDetailFragment;
                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {
                    switchContent(currentFragment, taskSceneFragment);
                    currentFragment = taskSceneFragment;

                } else if (group.getCheckedRadioButtonId() == R.id.rb3) {
                    switchContent(currentFragment, taskSampleFragment);
                    currentFragment = taskSampleFragment;
                }
            }
        });
        RadioButton radioButton = rb.findViewById(R.id.rb1);
        radioButton.setChecked(true);
        taskDetailViewModel.requestCityList(false);
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

    public void checkSelectMenu(int pos) {
        RadioButton radioButton = rb.findViewById(R.id.rb1);
        if (pos == 1) {
            radioButton = rb.findViewById(R.id.rb1);
            switchContent(currentFragment, taskDetailFragment);
            currentFragment = taskDetailFragment;
        } else if (pos == 2) {
            radioButton = rb.findViewById(R.id.rb2);
            switchContent(currentFragment, taskSceneFragment);
            currentFragment = taskSceneFragment;
        } else if (pos == 3) {
            radioButton = rb.findViewById(R.id.rb3);
            switchContent(currentFragment, taskSampleFragment);
            currentFragment = taskSampleFragment;
        }
        radioButton.setChecked(true);
    }
    public static void GoTaskDetailActivity(Context context, TaskEntity taskEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", taskEntity);
        context.startActivity(new Intent(context, TaskDetailActivity.class).putExtras(bundle));
    }
}