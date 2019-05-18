package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.product.sampling.R;
import com.product.sampling.bean.ImageItem;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 待办任务详情
 */
public class TaskDetailActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    TaskEntity task = null;
    TaskDetailViewModel taskDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        task = getIntent().getParcelableExtra("task");
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        List list = new ArrayList();
        for (int i = 0; i < 3; i++) {
            list.add(createItem(i));
        }
        setupRecyclerView((RecyclerView) recyclerView, list);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, list, task));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final TaskDetailActivity mParentActivity;
        private final List<ImageItem> mValues;
        private TaskEntity task;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageItem imageItem = (ImageItem) view.getTag();
//                if (null != task) {

                Bundle arguments = new Bundle();
                arguments.putString(TaskListFragment.ARG_TASK_STATUS, imageItem.getTitle());
                Fragment fragment = new ItemDetailFragment();
                if (imageItem.getTitle().equalsIgnoreCase("任务信息")) {
                    mParentActivity.taskDetailViewModel.taskEntity = task;
                    fragment = TaskDetailFragment.newInstance(task);
                } else if (imageItem.getTitle().equalsIgnoreCase("现场信息")) {
                    mParentActivity.taskDetailViewModel.taskEntity = task;
                    fragment = TaskSceneFragment.newInstance(task);
                } else if (imageItem.getTitle().equalsIgnoreCase("样品信息")) {
                    mParentActivity.taskDetailViewModel.taskEntity = task;
                    fragment = TaskSampleFragment.newInstance(task);
                }
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
//                } else {
//                    Context context = view.getContext();
//                    Intent intent = new Intent(context, ItemDetailActivity.class);
//                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, imageItem.getTitle());
//
//                    context.startActivity(intent);
//                }
            }
        };

        SimpleItemRecyclerViewAdapter(TaskDetailActivity parent,
                                      List<ImageItem> items,
                                      TaskEntity task) {
            mValues = items;
            mParentActivity = parent;
            this.task = task;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
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
                text = "任务信息";
                res = R.mipmap.menu_icon5;
                break;
            case 1:
                text = "现场信息";
                res = R.mipmap.menu_icon6;
                break;
            case 2:
                text = "样品信息";
                res = R.mipmap.menu_icon7;
                break;
        }
        return new ImageItem(text, res);
    }

}