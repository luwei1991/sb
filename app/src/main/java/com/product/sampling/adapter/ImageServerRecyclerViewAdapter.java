package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
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
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.ui.TaskDetailActivity;

import java.util.HashMap;
import java.util.List;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class ImageServerRecyclerViewAdapter extends RecyclerView.Adapter<ImageServerRecyclerViewAdapter.ViewHolder> {

    private final List<Pics> mValues;
    private boolean isLocal;
    private int taskPostion = -1;//当前图片列表所属样品id
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_task) {
                view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (Task) view.getTag()));
            } else {

                showListDialog(view.getContext(), (int) view.getTag());
            }
        }
    };

    private void showListDialog(Context context, int taskPostion) {
        final String[] items = {"编辑说明", "删除",};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        break;
                    case 1:
                        mValues.remove(taskPostion);
                        notifyDataSetChanged();
                        break;
                }
            }
        });
        listDialog.show();
    }

    public ImageServerRecyclerViewAdapter(Context parent,
                                          List<Pics> items,
                                          boolean isLocal) {
        mValues = items;
        this.isLocal = isLocal;
    }

    public ImageServerRecyclerViewAdapter(Context parent,
                                          List<Pics> items,
                                          int pos) {
        mValues = items;
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
        holder.mTextViewTitle.setText(task.getRemarks());
        if (taskPostion != -1) {
            holder.mImageView.setTag(taskPostion);
        }
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        Glide.with(holder.itemView.getContext()).load(IMAGE_BASE_URL + task.getId()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
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
