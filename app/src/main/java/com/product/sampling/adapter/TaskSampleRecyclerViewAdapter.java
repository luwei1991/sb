package com.product.sampling.adapter;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.ui.TaskSampleFragment;
import com.product.sampling.ui.WebViewActivity;

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
                fragment.selectPhoto(10, false, false, false, (int) view.getTag());
            } else if (R.id.iv_reduce == view.getId() || R.id.iv_reduce_video == view.getId()) {
                int index = (int) view.getTag();
                new AlertDialog.Builder(fragment.getContext()).setTitle("确认删除" + mData.get(index).getRemarks() + "的样品信息吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                mData.remove(index);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null).show();

            } else if (R.id.btn_edit_check_sheet == view.getId()) {//检查单编辑
                fragment.startActivityForResult(new Intent(view.getContext(), WebViewActivity.class)
                        .putExtra("task", (int) view.getTag())
                        .putExtra(Intent_Order, 1), RequestCodePdf);
            } else if (R.id.btn_edit_handling_sheet == view.getId()) {//处置单编辑
                fragment.startActivityForResult(new Intent(view.getContext(), WebViewActivity.class)
                        .putExtra("task", (int) view.getTag())
                        .putExtra(Intent_Order, 2), RequestCodePdf);
            } else if (R.id.btn_upload_check_sheet == view.getId()) {//检查单上传
                fragment.selectId = (int) view.getTag();
                fragment.startGallery((int) view.getTag(), Select_Check);
            } else if (R.id.btn_upload_handling_sheet == view.getId()) {//检查单上传
                fragment.selectId = (int) view.getTag();
                fragment.startGallery((int) view.getTag(), Select_Handle);
            } else if (R.id.iv_add_video == view.getId()) {
                fragment.selectId = (int) view.getTag();
                PictureSelector.create(fragment)
                        .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                        .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(9)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(false)// 是否裁剪 true or false
                        .compress(true)// 是否压缩 true or false
                        .glideOverride(200, 200)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(3, 4)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                        .isGif(true)// 是否显示gif图片 true or false
                        .compressSavePath(getPath())//压缩图片保存地址
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
//                        .selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .cropCompressQuality(80)// 裁剪压缩质量 默认90 int
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .cropWH(200, 200)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                        .videoQuality(1)// 视频录制质量 0 or 1 int
                        .videoMaxSecond(6000)// 显示多少秒以内的视频or音频也可适用 int
                        .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                        .recordVideoSecond(600)//视频秒数录制 默认60s int
                        .isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        }
    };


    @Override
    protected void convert(ViewHolder holder, TaskSample task) {

        int position = holder.getAdapterPosition() - 1 >= 0 ? holder.getAdapterPosition() - 1 : 0;

        holder.mTextViewTitle.setText(TextUtils.isEmpty(task.getRemarks()) ? "" : task.getRemarks());
        if (task.isLocalData) {
            holder.mTextViewHandleSheet.setText(task.handleSheet + "");
            holder.mTextViewCheckSheet.setText(task.checkSheet + "");
            holder.mImageViewAdd.setVisibility(View.VISIBLE);
            holder.mBtnEditCheck.setVisibility(View.VISIBLE);
            holder.mBtnEditHandle.setVisibility(View.VISIBLE);
            holder.mBtnUploadHandle.setVisibility(View.VISIBLE);
            holder.mBtnUploadCheck.setVisibility(View.VISIBLE);
        } else {
            if (null == task.getAdvice()) {
                holder.mTextViewHandleSheet.setText("");
            } else {
                holder.mTextViewHandleSheet.setText(task.getAdvice().getId() + "");
            }
            if (null == task.getSampling()) {
                holder.mTextViewCheckSheet.setText("");
            } else {
                holder.mTextViewCheckSheet.setText(task.getSampling().getId() + "");
            }
            holder.mImageViewAdd.setVisibility(View.INVISIBLE);
            holder.mBtnEditCheck.setVisibility(View.INVISIBLE);
            holder.mBtnEditHandle.setVisibility(View.INVISIBLE);
            holder.mBtnUploadHandle.setVisibility(View.INVISIBLE);
            holder.mBtnUploadCheck.setVisibility(View.INVISIBLE);
        }


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

        holder.mRecyclerViewImage.setAdapter(new ImageSampleRecyclerViewAdapter(holder.itemView.getContext(), task.getPics(),isLocalData));

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
