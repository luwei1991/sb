package com.product.sampling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.Task;
import com.product.sampling.ui.TaskDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskSampleRecyclerViewAdapter extends RecyclerView.Adapter<TaskSampleRecyclerViewAdapter.ViewHolder> {

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

    public TaskSampleRecyclerViewAdapter(Context parent,
                                         List<String> items,
                                         boolean twoPane) {
        mValues = items;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sample_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String task = mValues.get(position);
        holder.mTextViewTitle.setText(task + "");
        holder.itemView.setTag(task);
//        holder.itemView.setOnClickListener(mOnClickListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerView.setLayoutManager(linearLayoutManager);
        List list = new ArrayList();
        list.add("北京");
        list.add("商户");
        list.add("南京");

        holder.mRecyclerView.setAdapter(new ImageAndTextRecyclerViewAdapter(holder.itemView.getContext(), list, false));

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextViewTitle;
        final RecyclerView mRecyclerView;

        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mRecyclerView = view.findViewById(R.id.item_list);
        }
    }
}
