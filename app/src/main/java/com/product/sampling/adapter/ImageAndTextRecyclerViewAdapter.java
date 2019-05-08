package com.product.sampling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.Task;
import com.product.sampling.ui.TaskDetailActivity;
import com.product.sampling.ui.TaskListFragment;

import java.util.List;

public class ImageAndTextRecyclerViewAdapter extends RecyclerView.Adapter<ImageAndTextRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mTwoPane) {
            }
            view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (Task) view.getTag()));
        }
    };

    public ImageAndTextRecyclerViewAdapter(Context parent,
                                           List<String> items,
                                           boolean twoPane) {
        mValues = items;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scene_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String task = mValues.get(position);
        holder.mTextViewTitle.setText(task + "");
        holder.itemView.setTag(task);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextViewTitle;
        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
        }
    }
}
