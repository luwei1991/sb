package com.product.sampling.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.product.sampling.R;
import com.product.sampling.bean.News;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

public class NewsListAdapter extends BaseQuickAdapter<News, BaseViewHolder> {


    public NewsListAdapter(@LayoutRes int layoutResId, @Nullable List<News> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, News item) {
        helper.setText(R.id.tv_title, item.conent);

    }
}
