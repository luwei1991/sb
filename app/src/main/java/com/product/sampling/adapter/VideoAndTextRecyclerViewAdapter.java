package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
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
import com.luck.picture.lib.PictureSelector;
import com.product.sampling.R;
import com.product.sampling.bean.Videos;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.photo.BasePhotoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class VideoAndTextRecyclerViewAdapter extends RecyclerView.Adapter<VideoAndTextRecyclerViewAdapter.ViewHolder> {

    private List<Videos> mValues = new ArrayList<>();
    private boolean isUploadTask;
    private BasePhotoFragment fragment;//当前图片列表所属样品id
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isUploadTask) {
                int pos = (int) view.getTag();
                if (!TextUtils.isEmpty(mValues.get(pos).getPath())) {
                    PictureSelector.create(fragment).externalPictureVideo(mValues.get(pos).getPath());
                } else if (!TextUtils.isEmpty(mValues.get(pos).getId())) {
                    ToastUtil.show(view.getContext(), "视频加载中,请稍等");
                }
            } else {
                showListDialog(view.getContext(), (int) view.getTag());
            }
        }
    };

    public VideoAndTextRecyclerViewAdapter(Context parent,
                                           List<Videos> items,
                                           BasePhotoFragment pos, boolean isUploadTask) {
        mValues = items;
        fragment = pos;
        this.isUploadTask = isUploadTask;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scene_item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Videos task = mValues.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        if (TextUtils.isEmpty(task.getId())) {
            holder.mTextViewTitle.setText(task.getRemarks());
            Glide.with(holder.itemView.getContext()).load(task.getPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
        } else {
            holder.mTextViewTitle.setText(task.getRemarks() + "");
            createBitmapInThread(task.getId(), holder.mImageView);

            if (!TextUtils.isEmpty(task.getPath())) {
                File f = new File(task.getPath());
                if (f.exists()) {
                    Glide.with(holder.itemView.getContext()).load(task.getPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
                }
            }
        }
    }

    @Override
    public int getItemCount() {

        return mValues != null?mValues.size():0;
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
                        et.setText(mValues.get(taskPostion).getRemarks());
                        new AlertDialog.Builder(context).setTitle("请输入视频描述")
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //按下确定键后的事件
                                        String text = et.getText().toString();
                                        mValues.get(taskPostion).setRemarks(text);
                                        notifyDataSetChanged();
//                                        fragment.onRefreshTitle(false, taskPostion, text + "");
                                    }
                                }).setNegativeButton("取消", null).show();

                        break;
                    case 1:
                        if (mValues.size() > taskPostion) {
                            mValues.remove(taskPostion);
                            notifyDataSetChanged();
                        }
//                        fragment.onRemove(false, taskPostion);
                        break;
                    case 2:
                        if (!TextUtils.isEmpty(mValues.get(taskPostion).getPath())) {
                            PictureSelector.create(fragment).externalPictureVideo(mValues.get(taskPostion).getPath());
                        } else if (!TextUtils.isEmpty(mValues.get(taskPostion).getId())) {
                            ToastUtil.show(context, "视频加载中,请稍等");
                        }
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

    void createBitmapInThread(String taskid, ImageView imageView) {

        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws
                            Exception {
                        emitter.onNext(IMAGE_BASE_URL + taskid);
                    }
                })
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String videourl) throws Exception {
                        return createVideoThumbnail(videourl, 400, 300);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap s) {
                        imageView.setImageBitmap(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
