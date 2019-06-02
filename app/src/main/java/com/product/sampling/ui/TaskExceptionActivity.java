package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.ImageItem;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.viewmodel.TaskExecptionViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常信息
 */
public class TaskExceptionActivity extends BaseActivity {
    TaskExecptionViewModel taskExecptionViewModel;
    TaskEntity task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_exception);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        task = getIntent().getParcelableExtra("task");
        taskExecptionViewModel = ViewModelProviders.of(this).get(TaskExecptionViewModel.class);

        View recyclerView = findViewById(R.id.item_image_list);
        assert recyclerView != null;
        List list = new ArrayList();
        for (int i = 0; i < 2; i++) {
            list.add(createItem(i));
        }
        setupRecyclerView((RecyclerView) recyclerView, list);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list) {
        recyclerView.setAdapter(new TaskExceptionActivity.SimpleItemRecyclerViewAdapter(this, list, task));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<TaskExceptionActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final TaskExceptionActivity mParentActivity;
        private final List<ImageItem> mValues;
        private TaskEntity task;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageItem imageItem = (ImageItem) view.getTag();
                Bundle arguments = new Bundle();
                arguments.putString(TaskListFragment.ARG_TASK_STATUS, imageItem.getTitle());
                Fragment fragment = new TaskUnfindSampleFragment();
                if (imageItem.getTitle().equalsIgnoreCase("未抽到样品")) {
                    mParentActivity.taskExecptionViewModel.taskEntity = task;
                    fragment = TaskUnfindSampleFragment.newInstance(task);
                } else if (imageItem.getTitle().equalsIgnoreCase("企业拒检")) {
                    mParentActivity.taskExecptionViewModel.taskEntity = task;
                    fragment = TaskCompanyRefusedFragment.newInstance(task);
                }
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
//                } else {
//                    Context context = view.getContext();
//                    Intent intent = new Intent(context, ItemDetailActivity.class);
//                    intent.putExtra(TaskUnfindSampleFragment.ARG_ITEM_ID, imageItem.getTitle());
//
//                    context.startActivity(intent);
//                }
            }
        };

        SimpleItemRecyclerViewAdapter(TaskExceptionActivity parent,
                                      List<ImageItem> items,
                                      TaskEntity task) {
            mValues = items;
            mParentActivity = parent;
            this.task = task;
        }

        @Override
        public TaskExceptionActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new TaskExceptionActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ImageItem imageItem = mValues.get(position);
            holder.mImageView.setImageResource(imageItem.getRes());
            holder.mContentView.setText(imageItem.getTitle());

            holder.itemView.setTag(imageItem);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView mImageView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.iv_task);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
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
