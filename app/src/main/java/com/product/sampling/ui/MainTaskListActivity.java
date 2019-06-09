package com.product.sampling.ui;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.product.sampling.R;

/**
 * 任务列表
 */
public class MainTaskListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    static Fragment taskToDoFragment = TaskListFragment.newInstance("待办任务", "0");
    static Fragment taskWaitUpLoadedFragment = TaskListFragment.newInstance("未上传", "1");
    static Fragment taskHasUpLoadedFragment = TaskListFragment.newInstance("已上传", "2");
    static Fragment myinfoFragment = MyInfoFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

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
                            .replace(R.id.item_detail_container, taskWaitUpLoadedFragment)
                            .commit();
                } else if (group.getCheckedRadioButtonId() == R.id.rb3) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, taskHasUpLoadedFragment)
                            .commit();
                } else if (group.getCheckedRadioButtonId() == R.id.rb4) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, myinfoFragment)
                            .commit();
                } else {
                    TaskUnfindSampleFragment taskFragment = new TaskUnfindSampleFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, taskFragment)
                            .commit();
                }
            }
        });
        RadioButton radioButton = (RadioButton) rb.findViewById(R.id.rb1);
        radioButton.setChecked(true);
    }
}
