package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.config.PictureConfig;
import com.product.sampling.R;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Task;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.TaskDetailActivity;
import com.product.sampling.ui.eventmessage.CurItemMessage;
import com.product.sampling.utils.ActivityUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.product.sampling.Constants.IMAGE_BASE_URL;
import static com.product.sampling.ui.TaskSceneFragment.defaultRemarks;

public class ImageServerRecyclerViewAdapter extends RecyclerView.Adapter<ImageServerRecyclerViewAdapter.ViewHolder> {

    private  List<Pics> mValues;
    private boolean canEdit;
    private int taskPostion = -1;//当前图片列表所属样品id
    private Fragment fragment;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_task) {
                view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (Task) view.getTag()));
            } else {
                int taskPos = (int) view.getTag();
                if(taskPos == mValues.size()){
                    showSingleDialog(view.getContext(),taskPos);
                }else {
                    showListDialog(view.getContext(), taskPos);
                }
            }
        }
    };

    private void showListDialog(Context context, int taskPostion) {
        final String[] items = {"选择","查看详情"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(context);
        listDialog.setTitle("");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if(canEdit){
                            MediaHelper.startGallery(fragment, PictureConfig.SINGLE, MediaHelper.REQUEST_IMAGE_CODE);
                            EventBus.getDefault().postSticky(new CurItemMessage(taskPostion));
                        }else{
                            Toast.makeText(context,"已上传不能增加和修改图片！",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        String picId = mValues.get(taskPostion).getId();
                        String picPath = mValues.get(taskPostion).getOriginalPath();
                        if(TextUtils.isEmpty(picId) && TextUtils.isEmpty(picPath)){
                            Toast.makeText(context,"请先选择图片",Toast.LENGTH_LONG).show();
                        }else{
                            ActivityUtils.goPhotoViewActivity(context, mValues, taskPostion);
                        }

                        break;
                }
            }
        });
        listDialog.show();
    }
    private void showSingleDialog(Context context, int taskPostion) {
        final String[] items = {"选择"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case 0:
                        if(canEdit){
                            MediaHelper.startGallery(fragment, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE);
                            EventBus.getDefault().postSticky(new CurItemMessage(taskPostion));
                        }else{
                            Toast.makeText(context,"已上传不能增加和修改图片！",Toast.LENGTH_LONG).show();
                        }

                        break;
                }
            }
        });
        listDialog.show();
    }


    public ImageServerRecyclerViewAdapter(Context parent,
                                          List<Pics> items,
                                          boolean canEdit, Fragment fragment) {
        mValues = items;
        this.canEdit = canEdit;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scene_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == mValues.size()){
            holder.mImageView.setImageResource(R.mipmap.form_add);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mTextViewTitle.setText("其他");
            holder.delettView.setVisibility(View.GONE);
        }else {
            if(position >= defaultRemarks.length && canEdit){
                holder.delettView.setVisibility(View.VISIBLE);
                holder.delettView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mValues.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,1);
                    }
                });
            }

            Pics task = mValues.get(position);
            holder.mTextViewTitle.setText(task.getRemarks());
            if (taskPostion != -1) {
                holder.mImageView.setTag(taskPostion);
            }
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);
            if (TextUtils.isEmpty(task.getId())) {
                if(TextUtils.isEmpty(task.getOriginalPath())){
                    holder.mImageView.setImageResource(R.mipmap.form_add);
                }else{
                    Glide.with(holder.itemView.getContext()).load(task.getOriginalPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
                }

            } else {
                Glide.with(holder.itemView.getContext()).load(IMAGE_BASE_URL + task.getId()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
            }
        }


    }

    @Override
    public int getItemCount() {
        return mValues == null? 1: mValues.size() +1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextViewTitle;
        final ImageView mImageView;
        final ImageView delettView;

        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mImageView = view.findViewById(R.id.iv_task);
            delettView = view.findViewById(R.id.iv_delete);
        }
    }

    public void setData(List<Pics> list,boolean canEdit){
        mValues = (list != null) ? list : new ArrayList<Pics>();
        this.canEdit = canEdit;
        notifyDataSetChanged();
    }

    public List<Pics> getData(){
        return mValues;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
