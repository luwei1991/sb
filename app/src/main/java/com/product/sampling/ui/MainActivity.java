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
        List list = new ArrayList();
        New n1 = new New();
        n1.conent = "“我以前开车回老家时，也经常堵在收费站，一堵就是20分钟。”谈到取消高速公路省界收费站这一新政的意义时，交通运输部副部长戴东昌在国新办5月10日举行的国务院政策例行吹风会上讲起了自己的经历，并称其为“比较痛苦的体验”。\n" +
                "\n";
        list.add(n1);
        n1 = new New();
        n1.conent = "临阵磨枪，不快也光？动画片《骄傲的将军》将这个成语巧妙诠释了一番，同时提醒我们，老本不能吃太久。这部1956年“中国动画学派”的开山之作，借鉴了中国传统戏曲尤其京剧的众多元素，人物造型也采用了京剧脸谱，可谓国漫的良心。片中，得胜后的将军沉湎花天酒地，有人说是“西楚霸王”项羽的映射，也有人认为是“闯王”李自成的写照，不管隐喻了什么，这部建国之初的动画可以看出一些忧患意识的反映。\n" +
                "\n";
        list.add(n1);
        n1 = new New();
        n1.conent = "贝索斯在向媒体和航天行业高管发表的演讲中表示:“这艘航天器将前往月球。我们收到了一份礼物，这颗地球附近的天体叫做月球。”他补充说，由于月球的重力比地球低，它是个在太空中开始制造业的好地方。从月球上获取资源所需能源要“比从地球上获取节省24倍”，“这是一个巨大的杠杆”。\n" +
                "\n";
        list.add(n1);
        initView(list);
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
