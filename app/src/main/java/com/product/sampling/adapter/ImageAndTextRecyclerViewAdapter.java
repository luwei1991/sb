package com.product.sampling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.product.sampling.R;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.ui.TaskDetailActivity;
import com.product.sampling.ui.TaskListFragment;

import org.devio.takephoto.model.TImage;

import java.util.List;

public class ImageAndTextRecyclerViewAdapter extends RecyclerView.Adapter<ImageAndTextRecyclerViewAdapter.ViewHolder> {

    private final List<TaskImageEntity> mValues;
    private boolean mTwoPane;
    private int taskPostion = -1;//当前图片列表所属样品id
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.iv_task) {
                view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (Task) view.getTag()));
            }
        }
    };

    public ImageAndTextRecyclerViewAdapter(Context parent,
                                           List<TaskImageEntity> items,
                                           boolean twoPane) {
        mValues = items;
        mTwoPane = twoPane;
    }

    public ImageAndTextRecyclerViewAdapter(Context parent,
                                           List<TaskImageEntity> items,
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
        TaskImageEntity task = mValues.get(position);
        holder.mTextViewTitle.setText(task.title + "");
        if (taskPostion != -1) {
            holder.mImageView.setTag(taskPostion);
        }
        holder.itemView.setOnClickListener(mOnClickListener);
        Glide.with(holder.itemView.getContext()).load(task.getOriginalPath()).apply(RequestOptions.centerCropTransform()).into(holder.mImageView);
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
