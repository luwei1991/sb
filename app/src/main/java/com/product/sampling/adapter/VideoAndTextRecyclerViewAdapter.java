package com.product.sampling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.ui.TaskDetailActivity;

import java.util.List;

public class VideoAndTextRecyclerViewAdapter extends RecyclerView.Adapter<VideoAndTextRecyclerViewAdapter.ViewHolder> {

    private final List<LocalMedia> mValues;
    private boolean mTwoPane;
    private Fragment fragment;//当前图片列表所属样品id
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_task) {
                LocalMedia media = (LocalMedia) view.getTag();
                PictureSelector.create(fragment).externalPictureVideo(media.getPath());
            }
        }
    };

    public VideoAndTextRecyclerViewAdapter(Context parent,
                                           List<LocalMedia> items,
                                           Fragment pos) {
        mValues = items;
        fragment = pos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scene_item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalMedia task = mValues.get(position);
        Glide.with(holder.itemView.getContext()).load(task.getPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
        holder.mImageView.setOnClickListener(mOnClickListener);
        holder.mImageView.setTag(task);
        holder.mTextViewTitle.setText("");
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
