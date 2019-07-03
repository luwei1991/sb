package com.product.sampling.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.product.sampling.R;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.TaskSampleFragment;
import com.product.sampling.ui.WebViewActivity;

import java.io.Serializable;
import java.util.List;

import static com.product.sampling.ui.TaskSampleFragment.Select_Check;
import static com.product.sampling.ui.TaskSampleFragment.Select_Handle;
import static com.product.sampling.ui.WebViewActivity.Intent_Order;

public class TaskSampleRecyclerViewAdapter extends BaseQuickAdapter<TaskSample, TaskSampleRecyclerViewAdapter.ViewHolder> {

    public static final int RequestCodePdf = 99;
    private TaskSampleFragment fragment;
    boolean isLocalData;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_add) {
                fragment.selectId = (int) view.getTag();
                MediaHelper.startGallery(fragment, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE);
            } else if (R.id.iv_reduce == view.getId() || R.id.iv_reduce_video == view.getId()) {
                int index = (int) view.getTag();
                new AlertDialog.Builder(fragment.getContext()).setTitle("确认删除" + mData.get(index).getSamplename() + "的样品信息吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                fragment.deleteId = mData.get(index).getId() + ",";
                                mData.remove(index);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null).show();

            } else if (R.id.btn_edit_check_sheet == view.getId()) {//检查单编辑

                int index = (int) view.getTag();
                Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                Bundle b = new Bundle();
                b.putInt(Intent_Order, 1);
                b.putInt("task", index);
                b.putSerializable("map", mData.get(index).samplingInfoMap);
                intent.putExtras(b);
                fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);

            } else if (R.id.btn_edit_handling_sheet == view.getId()) {//处置单编辑
                int index = (int) view.getTag();

                Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                Bundle b = new Bundle();
                b.putInt(Intent_Order, 2);
                b.putInt("task", index);
                b.putSerializable("map", mData.get(index).adviceInfoMap);
                intent.putExtras(b);
                fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);

            } else if (R.id.btn_upload_check_sheet == view.getId()) {//检查单上传
                fragment.selectId = (int) view.getTag();
                fragment.startGalleryForPdf((int) view.getTag(), Select_Check);

            } else if (R.id.btn_upload_handling_sheet == view.getId()) {//处置单上传
                fragment.selectId = (int) view.getTag();
                fragment.startGalleryForPdf((int) view.getTag(), Select_Handle);
            } else if (R.id.iv_add_video == view.getId()) {
                fragment.selectId = (int) view.getTag();
                MediaHelper.startVideo(fragment, PictureConfig.CHOOSE_REQUEST);
            }
        }
    };


    @Override
    protected void convert(ViewHolder holder, TaskSample task) {

        int position = holder.getAdapterPosition() - 1 >= 0 ? holder.getAdapterPosition() - 1 : 0;

        holder.mTextViewTitle.setText(TextUtils.isEmpty(task.getSamplename()) ? " " : task.getSamplename());
        holder.mTextViewHandleSheet.setText(task.disposalfile + "");
        holder.mTextViewCheckSheet.setText(task.samplingfile + "");

        if (TextUtils.isEmpty(task.samplingpicfile)) {
            holder.mBtnUploadCheck.setText("(拍照)上传");
        } else {
            holder.mBtnUploadCheck.setText("已拍照");
        }

        if (TextUtils.isEmpty(task.disposalpicfile)) {
            holder.mBtnUploadHandle.setText("(拍照)上传");
        } else {
            holder.mBtnUploadHandle.setText("已拍照");
        }


        holder.mImageViewAdd.setVisibility(View.VISIBLE);
        holder.mBtnEditCheck.setVisibility(View.VISIBLE);
        holder.mBtnEditHandle.setVisibility(View.VISIBLE);
        holder.mBtnUploadHandle.setVisibility(View.VISIBLE);
        holder.mBtnUploadCheck.setVisibility(View.VISIBLE);


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

        holder.mIVReduceVideo.setTag(position);
        holder.mIVReduceVideo.setOnClickListener(mOnClickListener);

        holder.mIVAddVideo.setTag(position);
        holder.mIVAddVideo.setOnClickListener(mOnClickListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerViewImage.setLayoutManager(linearLayoutManager);

        holder.mRecyclerViewImage.setAdapter(new ImageSampleRecyclerViewAdapter(fragment, task.getPics(), task.isLocalData, position));

        linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerViewVideo.setLayoutManager(linearLayoutManager);
//        holder.mRecyclerViewVideo.setAdapter(new VideoAndTextRecyclerViewAdapter(holder.itemView.getContext(), task.videoList, fragment,isLocalData));

    }

    public TaskSampleRecyclerViewAdapter(int layoutResId, @Nullable List<TaskSample> data, TaskSampleFragment fragment, boolean isLocalData) {
        super(layoutResId, data);
        this.fragment = fragment;
        this.isLocalData = isLocalData;
    }

    class ViewHolder extends BaseViewHolder {
        final TextView mTextViewTitle;
        final RecyclerView mRecyclerViewImage;
        final RecyclerView mRecyclerViewVideo;
        final ImageView mImageViewAdd;
        final ImageView mImageViewReduce;
        final Button mBtnEditCheck;//检查单编辑
        final Button mBtnUploadCheck;//检查单上传
        final Button mBtnEditHandle;//处置单编辑
        final Button mBtnUploadHandle;//处置单上传
        final TextView mTextViewHandleSheet;
        final TextView mTextViewCheckSheet;
        final ImageView mIVAddVideo;//添加视频
        final ImageView mIVReduceVideo;//删除视频

        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mRecyclerViewImage = view.findViewById(R.id.item_image_list);
            mImageViewAdd = view.findViewById(R.id.iv_add);
            mImageViewReduce = view.findViewById(R.id.iv_reduce);
            mBtnUploadCheck = view.findViewById(R.id.btn_upload_check_sheet);
            mBtnEditCheck = view.findViewById(R.id.btn_edit_check_sheet);
            mBtnEditHandle = view.findViewById(R.id.btn_edit_handling_sheet);
            mBtnUploadHandle = view.findViewById(R.id.btn_upload_handling_sheet);
            mTextViewHandleSheet = view.findViewById(R.id.tv_handle_sheet);
            mTextViewCheckSheet = view.findViewById(R.id.tv_check_sheet);
            mIVReduceVideo = view.findViewById(R.id.iv_reduce_video);
            mIVAddVideo = view.findViewById(R.id.iv_add_video);
            mRecyclerViewVideo = view.findViewById(R.id.item_video_list);

        }
    }

    private String getPath() {
        return "/storage/emulated/0/zip";
    }
}
