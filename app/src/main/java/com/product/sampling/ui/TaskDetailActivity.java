package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.product.sampling.R;
import com.product.sampling.bean.ImageItem;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 待办任务详情
 */
public class TaskDetailActivity extends BaseActivity {

    TaskEntity task = null;
    TaskDetailViewModel taskDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);

        Bundle bundle = getIntent().getExtras();    //得到传过来的bundle
        task = (TaskEntity) bundle.getSerializable("task");//读出数据
        taskDetailViewModel.taskEntity = task;

        RadioGroup rb = findViewById(R.id.rg1);
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
}