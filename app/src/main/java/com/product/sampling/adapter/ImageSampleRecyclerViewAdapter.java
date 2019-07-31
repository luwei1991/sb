package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.product.sampling.R;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.TaskDetailActivity;

import java.util.List;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class ImageSampleRecyclerViewAdapter extends RecyclerView.Adapter<ImageSampleRecyclerViewAdapter.ViewHolder> {

    private final List<Pics> mValues;
    private boolean isUploadTask;
    private int taskPostion = -1;//当前图片列表所属样品id
    BasePhotoFragment fragment;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_task) {
                view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (Task) view.getTag()));
            } else {
                if (!isUploadTask) {
                    showListDialog(view.getContext(), (int) view.getTag());
                }
            }
        }
    };

    private void showListDialog(Context context, int postion) {
        final String[] items = {"编辑说明", "删除",};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        EditText et = new EditText(context);
                        et.setText(mValues.get(postion).getRemarks() + "");
                        new AlertDialog.Builder(context).setTitle("请输入图片描述")
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //按下确定键后的事件
                                        String text = et.getText().toString();
                                        mValues.get(postion).setRemarks(text + "");
                                        fragment.onRefreshSampleImageTitle(taskPostion, postion, text);
                                        notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", null).show();

                        break;
                    case 1:
                        mValues.remove(postion);
                        fragment.onRemoveSampleImage(taskPostion, postion);
                        notifyDataSetChanged();
                        break;
                }
            }
        });
        listDialog.show();
    }

    public ImageSampleRecyclerViewAdapter(BasePhotoFragment parent,
                                          List<Pics> items,
                                          boolean isUploadTask, int pos) {
        fragment = parent;
        mValues = items;
        this.isUploadTask = isUploadTask;
        taskPostion = pos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scene_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pics task = mValues.get(position);
        holder.mTextViewTitle.setText(TextUtils.isEmpty(task.getRemarks()) ? "" : task.getRemarks());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        if (TextUtils.isEmpty(task.getId())) {
            Glide.with(holder.itemView.getContext()).load(task.getOriginalPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
        } else {
            Glide.with(holder.itemView.getContext()).load(IMAGE_BASE_URL + task.getId()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextViewTitle;
        final ImageView mImageView;

        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mImageView = view.findViewById(R.id.iv_task);
        }
    }
}
