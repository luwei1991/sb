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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.bean.LocalMediaInfo;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.ui.PlayerActivity;
import com.product.sampling.ui.TaskDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class VideoAndTextRecyclerViewAdapter extends RecyclerView.Adapter<VideoAndTextRecyclerViewAdapter.ViewHolder> {

    private List<TaskEntity.Videos> mValues = new ArrayList<>();
    private boolean isLocal;
    private Fragment fragment;//当前图片列表所属样品id
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isLocal) {
                showListDialog(view.getContext(), (int) view.getTag());
            } else {
                TaskEntity.Videos videos = mValues.get((int) view.getTag());
                view.getContext().startActivity(new Intent(view.getContext(), PlayerActivity.class).putExtra("title", videos.getRemarks() + "").putExtra("url", IMAGE_BASE_URL + videos.getId()));
            }
        }
    };

    public VideoAndTextRecyclerViewAdapter(Context parent,
                                           List<TaskEntity.Videos> items,
                                           Fragment pos, boolean isLocal) {
        mValues = items;
        fragment = pos;
        this.isLocal = isLocal;
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
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        if (isLocal) {
            holder.mTextViewTitle.setText(task.title);
            Glide.with(holder.itemView.getContext()).load(task.getPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
        } else {
            holder.mTextViewTitle.setText(task.getRemarks() + "");
            holder.mImageView.setImageBitmap(createVideoThumbnail(IMAGE_BASE_URL + task.getId(), 400, 300));
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

    private void showListDialog(Context context, int taskPostion) {
        final String[] items = {"编辑说明", "删除", "播放"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        EditText et = new EditText(context);
                        new AlertDialog.Builder(context).setTitle("请输入视频描述")
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //按下确定键后的事件
                                        String text = et.getText().toString();
                                        mValues.get(taskPostion).title = text;
                                        notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", null).show();

                        break;
                    case 1:
                        mValues.remove(taskPostion);
                        notifyDataSetChanged();
                        break;
                    case 2:
                        PictureSelector.create(fragment).externalPictureVideo(mValues.get(taskPostion).getPath());
                        break;
                }
            }
        });
        listDialog.show();
    }

    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
}
