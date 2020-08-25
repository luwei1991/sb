package com.product.sampling.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陆伟 on 2019/8/14.
 * Copyright (c) 2019 ${ORGANIZATION_NAME}. All rights reserved.
 */


public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<T> mData;
    private final Context mContext;
    private LayoutInflater mInflater;

    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public BaseRecyclerAdapter(Context ctx, List<T> list){
        mData = (list != null) ? list :new ArrayList<T>();
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
    }

    public void setData(List<T> list){
        mData = (list != null) ? list : new ArrayList<T>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RecyclerViewHolder holder = new RecyclerViewHolder(mInflater.inflate(getItemLayoutId(viewType),parent,false));
        if(mClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.itemView,holder.getLayoutPosition());
                }
            });
        }

        if(mLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView,holder.getLayoutPosition());
                    return true;
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        bindData(recyclerViewHolder,i,mData.get(i));
    }

    @Override
    public int getItemViewType(int position) {
        return  position;
    }

    public T getItem(int pos){
        return mData.get(pos);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(int pos, T iterm){
        mData.add(pos,iterm);
        notifyItemInserted(pos);
    }
    public void remove(int pos){
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setOnItermCLickListener(OnItemClickListener lickListener){
        mClickListener = lickListener;
    }

    public void setOnItermLongCLickListener(OnItemLongClickListener lickListener){
        mLongClickListener = lickListener;
    }

    abstract public int getItemLayoutId(int viewType);
    abstract public void bindData(RecyclerViewHolder holder, int position, T item );

    public interface OnItemClickListener{
        void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View itemView, int pos);
    }
}
