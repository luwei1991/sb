package com.product.sampling.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.PdfDocumentActivity;

import java.util.List;

public class TaskSampleRecyclerViewAdapter extends RecyclerView.Adapter<TaskSampleRecyclerViewAdapter.ViewHolder> {

    private final List<TaskSample> mValues;
    private final Activity activity;
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

            } else if (R.id.btn_edit_check_sheet == view.getId()) {//检查单编辑
                fragment.startActivityForResult(new Intent(view.getContext(), PdfDocumentActivity.class).putExtra("task", (int) view.getTag()), 1);
            } else if (R.id.btn_edit_handling_sheet == view.getId()) {//处置单编辑
                view.getContext().startActivity(new Intent(view.getContext(), PdfDocumentActivity.class));
            } else if (R.id.btn_upload_check_sheet == view.getId()) {//检查单上传

            } else if (R.id.btn_upload_handling_sheet == view.getId()) {//检查单上传

            }
        }
    };

    public TaskSampleRecyclerViewAdapter(Activity parent, BasePhotoFragment fragment,
                                         List<TaskSample> items,
                                         boolean twoPane) {
        mValues = items;
        activity = parent;
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

        holder.mTextViewHandleSheet.setText(task.handleSheet + "");
        holder.mTextViewCheckSheet.setText(task.checkSheet + "");

        holder.mImageViewAdd.setTag(position);
        holder.mImageViewAdd.setOnClickListener(mOnClickListener);
        holder.mImageViewReduce.setTag(position);
        holder.mImageViewReduce.setOnClickListener(mOnClickListener);

        holder.mBtnEditCheck.setTag(position);
        holder.mBtnEditCheck.setOnClickListener(mOnClickListener);

        holder.mBtnUploadCheck.setTag(position);
        holder.mBtnUploadCheck.setOnClickListener(mOnClickListener);

        holder.mBtnEditHandle.setTag(position);
        holder.mBtnEditHandle.setOnClickListener(mOnClickListener);

        holder.mBtnUploadHandle.setTag(position);
        holder.mBtnUploadHandle.setOnClickListener(mOnClickListener);

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
        final Button mBtnEditCheck;//检查单编辑
        final Button mBtnUploadCheck;//检查单上传
        final Button mBtnEditHandle;//处置单编辑
        final Button mBtnUploadHandle;//处置单上传
        final TextView mTextViewHandleSheet;
        final TextView mTextViewCheckSheet;

        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mRecyclerView = view.findViewById(R.id.item_image_list);
            mImageViewAdd = view.findViewById(R.id.iv_add);
            mImageViewReduce = view.findViewById(R.id.iv_reduce);
            mBtnUploadCheck = view.findViewById(R.id.btn_upload_check_sheet);
            mBtnEditCheck = view.findViewById(R.id.btn_edit_check_sheet);
            mBtnEditHandle = view.findViewById(R.id.btn_edit_handling_sheet);
            mBtnUploadHandle = view.findViewById(R.id.btn_upload_handling_sheet);
            mTextViewHandleSheet = view.findViewById(R.id.tv_handle_sheet);
            mTextViewCheckSheet = view.findViewById(R.id.tv_check_sheet);
        }
    }
}
