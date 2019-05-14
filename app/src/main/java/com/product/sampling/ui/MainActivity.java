package com.product.sampling.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.view.CardTransformer;
import com.product.sampling.view.MyViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener {
    private static final int LOCATION_CODE = 1;
    MyViewPager viewPager;
    Disposable disposable;
    BannerViewPagerAdapter adapter;
    RecyclerView mRecyclerView;
    private TextView tvTemperature;
    private ImageView ivWeather;

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
        } else {
            initLocation();
        }
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
    }

    private void initLocation() {

        AMapLocationClient mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
        //获取一次定位结果：
//该方法默认为false。
        mLocationOption.setOnceLocation(true);

//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();

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
                Toast.makeText(MainActivity.this, amapLocation.toString(), Toast.LENGTH_LONG).show();
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。
                    // 执形我们想要的操作
                    initLocation();
                } else {
                    Toast.makeText(MainActivity.this, "拒绝了定位权限,无法定位", Toast.LENGTH_LONG).show();
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
//                reporttime1.setText(weatherlive.getReportTime()+"发布");
//                weather.setText(weatherlive.getWeather());
//                Temperature.setText(weatherlive.getTemperature()+"°");
//                wind.setText(weatherlive.getWindDirection()+"风     "+weatherlive.getWindPower()+"级");
//                humidity.setText("湿度         "+weatherlive.getHumidity()+"%");
                StringBuilder builder = new StringBuilder(tvTemperature.getText());
                builder.append(" " + weatherlive.getWeather() + " ");
                builder.append(" " + weatherlive.getTemperature() + "° ");
                builder.append(weatherlive.getWindDirection() + "风" + weatherlive.getWindPower() + "级");
                builder.append(" 湿度" + weatherlive.getHumidity() + "%");
                tvTemperature.setText(builder);
                setWeatherIcon(weatherlive.getWeather());


            } else {
                com.product.sampling.maputil.ToastUtil.showShortToast(MainActivity.this, "无天气数据");
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
}
