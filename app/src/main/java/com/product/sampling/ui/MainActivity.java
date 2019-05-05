package com.product.sampling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.product.sampling.R;
import com.product.sampling.adapter.BannerViewPagerAdapter;
import com.product.sampling.adapter.NewsListAdapter;
import com.product.sampling.bean.New;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.view.CardTransformer;
import com.product.sampling.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    MyViewPager viewPager;
    Disposable disposable;
    BannerViewPagerAdapter adapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        findViewById(R.id.rl_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ItemListActivity.class));

            }
        });
//        initView();
        getData();
    }

    private void initView(List<New> aNews) {
        viewPager = findViewById(R.id.viewPager);
        ArrayList list = new ArrayList();
        list.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1911150647,1930869014&fm=27&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1690699292,1481547313&fm=27&gp=0.jpg");
        list.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3207781657,3460758070&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=144388917,3393541021&fm=27&gp=0.jpg");
        adapter = new BannerViewPagerAdapter(this, list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);//预加载2个
        viewPager.setPageMargin(30);//设置viewpage之间的间距
        viewPager.setClipChildren(false);
        viewPager.setPageTransformer(true, new CardTransformer());
        mRecyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.color.blue_color_30));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(new NewsListAdapter(R.layout.item_news, aNews));

    }

    private void getData() {
        disposable = NetWorkManager.getRequest().getNewsList()
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(news -> {
                    Log.e("myPageBeans", "" + news.toString());
                    initView(news);

                }, throwable -> {
                    ToastUtil.showToast(this, ((ApiException) throwable).getDisplayMessage());

                    Log.e("throwable", "" + ((ApiException) throwable).getDisplayMessage());
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }
}
