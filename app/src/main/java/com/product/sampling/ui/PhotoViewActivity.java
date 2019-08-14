package com.product.sampling.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.product.sampling.R;
import com.product.sampling.view.PhotoViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

public class PhotoViewActivity extends Activity {
    @BindView(R.id.photoView_pager)
    PhotoViewPager viewPager;

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    private int position;
    private List<String> paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        paths = intent.getStringArrayListExtra("paths");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return paths == null ? 0 : paths.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View adView = LayoutInflater.from(PhotoViewActivity.this).inflate(R.layout.item_photo_view, null);
                PhotoView icon = adView.findViewById(R.id.photo_view);
                icon.setBackgroundColor(getResources().getColor(R.color.black));

                String task = paths.get(position);
                Intent intent = getIntent();
                String remote=intent.getStringExtra("remote");
                if("1".equals(remote)){
                    Glide.with(PhotoViewActivity.this).load(IMAGE_BASE_URL +task).into(icon);
                }else{
                    Glide.with(PhotoViewActivity.this).load(task).into(icon);
                }
                   //使图片具有放缩功能
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(icon);
                mAttacher.update();

                container.addView(adView);
                return adView;
            }


            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        viewPager.setCurrentItem(position, true);
    }
}
