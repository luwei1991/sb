package com.product.sampling.ui;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * 待办任务详情
 */
public class TaskDetailActivity extends BaseActivity {

    TaskEntity task = null;
    TaskDetailViewModel taskDetailViewModel;
    RadioGroup rb;

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

        rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = new TaskUnfindSampleFragment();
                if (group.getCheckedRadioButtonId() == R.id.rb1) {
                    fragment = TaskDetailFragment.newInstance(task);
                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {
                    fragment = TaskSceneFragment.newInstance(task);
                } else if (group.getCheckedRadioButtonId() == R.id.rb3) {
                    fragment = TaskSampleFragment.newInstance(task);
                }
                Bundle arguments = new Bundle();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            }
        });
        RadioButton radioButton = rb.findViewById(R.id.rb1);
        radioButton.setChecked(true);
    }

    public void checkSelectMenu(int pos) {
        RadioButton radioButton = rb.findViewById(R.id.rb1);
        if (pos == 1) {
            radioButton = rb.findViewById(R.id.rb1);
        } else if (pos == 2) {
            radioButton = rb.findViewById(R.id.rb2);
        } else if (pos == 3) {
            radioButton = rb.findViewById(R.id.rb3);
        }
        radioButton.setChecked(true);
    }
}