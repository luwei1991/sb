package com.product.sampling.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.product.sampling.Constants;
import com.product.sampling.MainApplication;
import com.product.sampling.R;
import com.product.sampling.bean.New;
import com.product.sampling.ui.WebViewActivity;

import java.util.List;

/**
 * 创建时间：2018/5/28
 * 描述：
 */
public class BannerViewPagerAdapter extends PagerAdapter {
    private List<New> images;
    private Context context;

    public BannerViewPagerAdapter(Context context, List<New> images) {
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
        String title=images.get(position).title;
        TextView textView=view.findViewById(R.id.newTitle);
        textView.setText(title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                New news =  images.get(position);
                WebViewActivity.startWebView((Activity) context, news.conent, news.title,news.pubdate);
            }
        });
        Glide.with(MainApplication.INSTANCE.getApplicationContext()).load(Constants.IMAGE_BASE_URL + images.get(position).imgurl).apply(RequestOptions.centerCropTransform()).into(imageView);

//        GlideManager.getInstance().ImageLoad(context,,imageView,true);
//        Glide.with(context)
//                .load(Constants.IMAGE_BASE_URL + images.get(position).imgurl)
//                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(DensityUtil.dip2px(context, 5), 0, RoundedCornersTransformation.CornerType.ALL)).error(R.mipmap.ic_launcher))
//                .into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
