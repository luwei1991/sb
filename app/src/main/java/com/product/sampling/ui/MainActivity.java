package com.product.sampling.ui;

import android.Manifest;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.product.sampling.R;
import com.product.sampling.adapter.BannerViewPagerAdapter;
import com.product.sampling.adapter.NewsListAdapter;
import com.product.sampling.bean.New;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.personal.User;
import com.product.sampling.utils.GdLocationUtil;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.utils.ToastUtils;
import com.product.sampling.view.CardTransformer;
import com.product.sampling.view.MyViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener {

    MyViewPager viewPager;
    Disposable disposable;
    BannerViewPagerAdapter adapter;
    RecyclerView mRecyclerView;
    private TextView tvTemperature;
    private ImageView ivWeather;
    private TextView tvLoginOut;
    private TextView tvUserName;
    private long mExitTime = 0;

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
        //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
        requestLocation(this);
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

        tvTemperature = findViewById(R.id.tv_temperature);
        ivWeather = findViewById(R.id.iv_weather);
//        tvLoginOut = findViewById(R.id.tv_login_out);
//        tvUserName = findViewById(R.id.tv_user_name);
//        tvUserName.setText(AccountManager.getInstance().getUserInfoBean().getName());
//        tvLoginOut.setOnClickListener(this);
    }

    private void getData() {
        disposable = NetWorkManager.getRequest().getNewsList(AccountManager.getInstance().getUserId())
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(news -> {
                    Log.e("news", "" + news.toString());
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

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                tvTemperature.setText(amapLocation.getCity() + amapLocation.getDistrict());
                Log.e("amapLocation", amapLocation.toString());
                MainApplication.INSTANCE.setMyLocation(amapLocation);
                getWeather(amapLocation.getDistrict(), 0);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                if (amapLocation.getErrorCode() == 12) {
                    Toast.makeText(MainActivity.this, "缺少定位权限,请到设置->安全和隐私->定位服务,打开允许访问我的位置信息", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
     *
     * @param city
     * @param type
     */
    void getWeather(String city, int type) {
        //检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        WeatherSearchQuery mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive weatherlive = weatherLiveResult.getLiveResult();
                StringBuilder builder = new StringBuilder(tvTemperature.getText());
                builder.append(" " + weatherlive.getWeather() + " ");
                builder.append(" " + weatherlive.getTemperature() + "° ");
                builder.append(weatherlive.getWindDirection() + "风" + weatherlive.getWindPower() + "级");
                builder.append(" 湿度" + weatherlive.getHumidity() + "%");
                tvTemperature.setText(builder);
                setWeatherIcon(weatherlive.getWeather());
            } else {
                showShortToast("无天气数据");
            }
        } else {
            com.product.sampling.maputil.ToastUtil.showerror(this.getApplicationContext(), rCode);

        }

    }

    private void setWeatherIcon(String weather) {
        if (weather.contains("晴")) {
            ivWeather.setImageResource(R.mipmap.index_weather1);
        } else if (weather.contains("多云")) {
            ivWeather.setImageResource(R.mipmap.index_weather2);
        } else if (weather.contains("阴")) {
            ivWeather.setImageResource(R.mipmap.index_weather3);
        } else if (weather.contains("雨")) {
            ivWeather.setImageResource(R.mipmap.index_weather4);
        } else if (weather.contains("雪")) {
            ivWeather.setImageResource(R.mipmap.index_weather5);
        } else {
            ivWeather.setImageResource(0);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.tv_login_out) {
//            showSimpleDialog("确定退出登录吗", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                    finish();
//                }
//            });
//
//        }
//    }

    private void exitApp() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            showToast("再按一次退出程序");
            mExitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GdLocationUtil.getInstance().stopLoaction();
    }
}
