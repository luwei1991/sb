package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.product.sampling.R;
import com.product.sampling.bean.LocalMediaInfo;
import com.product.sampling.bean.TaskEntity;

import java.util.List;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class VideoServerRecyclerViewAdapter extends RecyclerView.Adapter<VideoServerRecyclerViewAdapter.ViewHolder> {

    private final List<TaskEntity.Videos> mValues;
    private boolean mTwoPane;
    private Fragment fragment;//当前图片列表所属样品id
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showListDialog(view.getContext(), (int) view.getTag());
        }
    };

    public VideoServerRecyclerViewAdapter(Context parent,
                                          List<TaskEntity.Videos> items,
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
        TaskEntity.Videos task = mValues.get(position);
        Glide.with(holder.itemView.getContext()).load(IMAGE_BASE_URL + task.getId()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
        holder.mTextViewTitle.setText(task.getRemarks());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);

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

    private void showListDialog(Context context, int taskPostion) {
        final String[] items = {"播放"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        PictureSelector.create(fragment).externalPictureVideo(IMAGE_BASE_URL + mValues.get(taskPostion).getId());
                        break;
                }
            }
        });
        listDialog.show();
    }
}
