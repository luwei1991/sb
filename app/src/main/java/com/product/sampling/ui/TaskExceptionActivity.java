package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.ImageItem;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.ui.viewmodel.TaskExecptionViewModel;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_exception);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        Bundle bundle = getIntent().getExtras();    //得到传过来的bundle
        task = (TaskEntity) bundle.getSerializable("task");//读出数据

        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        taskDetailViewModel.taskEntity = task;

//        View recyclerView = findViewById(R.id.item_image_list);
//        assert recyclerView != null;
//        List list = new ArrayList();
//        for (int i = 0; i < 2; i++) {
//            list.add(createItem(i));
//        }
        taskUnfindSampleFragment = TaskUnfindSampleFragment.newInstance(task);
        taskCompanyRefusedFragment = TaskCompanyRefusedFragment.newInstance(task);
//        currentFragment = taskUnfindSampleFragment;

        RadioGroup rb = findViewById(R.id.rg1);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb1) {

                    switchContent(currentFragment, taskUnfindSampleFragment);
                    currentFragment = taskUnfindSampleFragment;

                } else if (group.getCheckedRadioButtonId() == R.id.rb2) {

                    switchContent(currentFragment, taskCompanyRefusedFragment);
                    currentFragment = taskCompanyRefusedFragment;
                }
            }
        });
        RadioButton radioButton = rb.findViewById(R.id.rb1);
        radioButton.setChecked(true);
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
}
