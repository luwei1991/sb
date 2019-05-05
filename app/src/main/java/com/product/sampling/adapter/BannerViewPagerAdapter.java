package com.product.sampling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.product.sampling.R;
import com.product.sampling.utils.DensityUtil;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 创建时间：2018/5/28
 * 描述：
 */
public class BannerViewPagerAdapter extends PagerAdapter {
    private List<String> images;
    private Context context;

    public BannerViewPagerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images == null ? 0 : images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_viewpager, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        Glide.with(context)
                .load(images.get(position))
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(DensityUtil.dip2px(context, 5), 0, RoundedCornersTransformation.CornerType.ALL)).error(R.mipmap.ic_launcher))
                .into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
