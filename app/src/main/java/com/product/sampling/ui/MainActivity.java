package com.product.sampling.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.product.sampling.R;
import com.product.sampling.adapter.BannerViewPagerAdapter;
import com.product.sampling.adapter.NewsListAdapter;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.New;
import com.product.sampling.bean.UpdateEntity;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.ui.update.UpdateDialogFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.AppUtils;
import com.product.sampling.utils.GdLocationUtil;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.view.CardTransformer;
import com.product.sampling.view.MyViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.product.sampling.ui.H5WebViewActivity.Intent_Order;

public class MainActivity extends BaseActivity implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener {

    MyViewPager viewPager;
    Disposable disposable;
    BannerViewPagerAdapter adapter;
    RecyclerView mRecyclerView;
    private TextView tvTemperature;
    private ImageView ivWeather;
    private TextView tvLoginOut;
    private TextView tvUserName;
    private TextView evtitle;
    private long mExitTime = 0;
    TaskDetailViewModel taskDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        checkVersion(this, getSupportFragmentManager());

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        findViewById(R.id.rl_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MainTaskListActivity.class));
            }
        });
        findViewById(R.id.rl_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvTemperature = findViewById(R.id.tv_temperature);
        ivWeather = findViewById(R.id.iv_weather);
        getData();
        //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
        requestLocation(this);
    }

    private void initView(List<New> aNews) {
        viewPager = findViewById(R.id.viewPager);
        adapter = new BannerViewPagerAdapter(this, aNews);
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

        tvTemperature = findViewById(R.id.tv_temperature);
        ivWeather = findViewById(R.id.iv_weather);
        NewsListAdapter adapter = new NewsListAdapter(R.layout.item_news, aNews);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                New news = aNews.get(position);
                WebViewActivity.startWebView(MainActivity.this, news.conent, news.title,news.pubdate);
            }
        });
    }

    private void getData() {
        disposable = NetWorkManager.getRequest().getNewsList(AccountManager.getInstance().getUserId())
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(news -> {
                    Log.e("news", "" + news.toString());
                    initView(news);

                }, throwable -> {
                    ToastUtil.showShortToast(this, ((ApiException) throwable).getDisplayMessage());
                    Log.e("throwable", "" + ((ApiException) throwable).getDisplayMessage());
                });
    }
    public void checkVersion(Context context, FragmentManager fragmentManager) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }
           //versionCode和versionName这里是反过来的
        RetrofitService.createApiService(Request.class)
                .getAppVersion(userid, AppUtils.getVersionName(context))
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<UpdateEntity>() {
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }

                    @Override
                    public void onSuccess(UpdateEntity result) {
                        if (null != result && "1".equals(result.getIsnew())) {
                            UpdateDialogFragment.newInstance(result).show(fragmentManager, "update");
                        }
                    }
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
