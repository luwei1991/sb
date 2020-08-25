package com.product.sampling.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.product.sampling.R;
import com.product.sampling.bean.New;

import java.util.List;

public class NewsListAdapter extends BaseQuickAdapter<New, BaseViewHolder> {


    public NewsListAdapter(@LayoutRes int layoutResId, @Nullable List<New> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, New item) {
        helper.setText(R.id.tv_title, item.title);
    }
}
