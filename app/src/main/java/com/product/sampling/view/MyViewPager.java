package com.product.sampling.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**

 * 创建时间：2018/6/6
 * 描述：
 */
public class MyViewPager extends ViewPager {

    private MyViewPagerCallBack myViewPagerCallBack;

    public interface MyViewPagerCallBack {
        void onAttacth();

        void onDetached();
    }


    public void setMyViewPagerCallBack(MyViewPagerCallBack myViewPagerCallBack) {
        this.myViewPagerCallBack = myViewPagerCallBack;
    }

    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (myViewPagerCallBack != null) {
            myViewPagerCallBack.onDetached();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        try {
//            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
//            mFirstLayout.setAccessible(true);
//            mFirstLayout.set(this, false);
//            getAdapter().notifyDataSetChanged();
//            setCurrentItem(getCurrentItem());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (myViewPagerCallBack != null) {
            myViewPagerCallBack.onAttacth();
        }
    }
}
