package com.product.sampling.ui.base;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 陆伟 on 2019/8/14.
 * Copyright (c) 2019 ${ORGANIZATION_NAME}. All rights reserved.
 */


public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    private <T extends View> T findViewById(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = itemView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    public View getView(int viewId){
        return findViewById(viewId);
    }

    public TextView getTextVIew(int viewId){
        return (TextView) getView(viewId);
    }

    public Button getButton(int viewId){
        return (Button) getView(viewId);
    }

    public ImageView getImageView(int viewId){
        return (ImageView) getView(viewId);
    }

    public ImageButton getImageButton(int viewId){
        return (ImageButton) getView(viewId);
    }
    public EditText getEditText(int viewId){
        return (EditText) getView(viewId);
    }

    public RecyclerViewHolder setText(int viewId, String value){
        TextView view = findViewById(viewId);
        view.setText(value);
        return this;
    }

    public RecyclerViewHolder setBackground(int viewId, int resId){
        View view = findViewById(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public RecyclerViewHolder setClickListener(int viewId, View.OnClickListener listener){
        View view = findViewById(viewId);
        view.setOnClickListener(listener);
        return this;
    }




}
