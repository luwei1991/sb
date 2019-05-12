package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.TaskDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskSampleRecyclerViewAdapter extends RecyclerView.Adapter<TaskSampleRecyclerViewAdapter.ViewHolder> {

    private final List<TaskSample> mValues;
    private final boolean mTwoPane;
    private BasePhotoFragment fragment;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_add) {
                fragment.selectPhoto(10, false, false, false, (int) view.getTag());
            } else if (R.id.iv_reduce == view.getId()) {
                int index = (int) view.getTag();
                new AlertDialog.Builder(fragment.getContext()).setTitle("确认删除" + mValues.get(index).title + "的样品信息吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                mValues.remove(index);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null).show();

            }
        }
    };

    public TaskSampleRecyclerViewAdapter(Context parent, BasePhotoFragment fragment,
                                         List<TaskSample> items,
                                         boolean twoPane) {
        mValues = items;
        mTwoPane = twoPane;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sample_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskSample task = mValues.get(position);
        holder.mTextViewTitle.setText(task.title + "");
        holder.mImageViewAdd.setTag(position);
        holder.mImageViewAdd.setOnClickListener(mOnClickListener);
        holder.mImageViewReduce.setTag(position);
        holder.mImageViewReduce.setOnClickListener(mOnClickListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerView.setLayoutManager(linearLayoutManager);

        holder.mRecyclerView.setAdapter(new ImageAndTextRecyclerViewAdapter(holder.itemView.getContext(), task.list, false));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextViewTitle;
        final RecyclerView mRecyclerView;
        final ImageView mImageViewAdd;
        final ImageView mImageViewReduce;


        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mRecyclerView = view.findViewById(R.id.item_list);
            mImageViewAdd = view.findViewById(R.id.iv_add);
            mImageViewReduce = view.findViewById(R.id.iv_reduce);
        }
    }
}
