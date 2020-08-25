package com.product.sampling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.product.sampling.MainApplication;
import com.product.sampling.R;
import com.product.sampling.adapter.BannerViewPagerAdapter;
import com.product.sampling.adapter.NewsListAdapter;
import com.product.sampling.bean.New;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.thread.CallBack;
import com.product.sampling.thread.LWThread;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.masterplate.MasterplterMainActivity;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;
import com.product.sampling.view.CardTransformer;
import com.product.sampling.view.MyViewPager;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import butterknife.ButterKnife;
import io.agora.rtm.RtmClient;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements WeatherSearch.OnWeatherSearchListener,AMapLocationListener{

    MyViewPager viewPager;
    Disposable disposable;
    BannerViewPagerAdapter adapter;
    RecyclerView mRecyclerView;
    private TextView tvTemperature;
    private ImageView ivWeather;
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;
    Toolbar toolbar;
    LinearLayout llBack;
    TextView tvUserName;
    TextView tvLoginOut;
    TextView tvVersion;
    NewsListAdapter newSAdapter;


    @Override
    public void setUIController(Object sc) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        View root = LayoutInflater.from(this).inflate(R.layout.main_activity, null);
        setContentView(root);
        findToolBarMain();
        findViewById(R.id.rl_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long nowTime = System.currentTimeMillis();
                if (nowTime - mLastClickTime > TIME_INTERVAL) {
                    // do something
                    startActivity(new Intent(MainActivity.this, MainTaskListActivity.class));
                    mLastClickTime = nowTime;
                }


            }
        });
        findViewById(R.id.rl_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* startActivity(new Intent( MainActivity.this,ScanMainActivity.class));*/
            }
        });
        findViewById(R.id.rl_masterplate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long nowTime = System.currentTimeMillis();
                if (nowTime - mLastClickTime > TIME_INTERVAL) {
                    // do something
                    startActivity(new Intent( MainActivity.this, MasterplterMainActivity.class));
                    mLastClickTime = nowTime;
                }

            }
        });


        tvTemperature = findViewById(R.id.tv_temperature);
        ivWeather = findViewById(R.id.iv_weather);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.color.blue_color_30));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        newSAdapter = new NewsListAdapter(R.layout.item_news, null);
        mRecyclerView.setAdapter(newSAdapter);
        getData();
        //设置日志报告user
        CrashReport.setUserId(AccountManager.getInstance().getUserInfoBean().getName());
    }




    public void findToolBarMain() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            tvLoginOut = findViewById(R.id.tv_login_out);
            tvUserName = findViewById(R.id.tv_user_name);
            tvVersion = findViewById(R.id.tv_version);
            tvVersion.setText("v:"+ QMUIPackageHelper.getAppVersion(this));
            UserInfoBean userInfoBean = AccountManager.getInstance().getUserInfoBean();
            if (null != userInfoBean) {
                String name = userInfoBean.getName();
                if (null != name && !TextUtils.isEmpty(name)) {
                    tvUserName.setText(name);
                }
            }

            tvLoginOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginOutDialog();
                }
            });
        }
    }

    private void showLoginOutDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？退出后将会清除本地未上传数据！")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        AccountManager.getInstance().clearUserInfo();
                        mRtmClient.logout(null);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        popAllActivity();
                        LocalTaskListManager.getInstance().clear();
                        SPUtil.clear(tvLoginOut.getContext());
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainApplication.getMyLocation() != null){
            tvTemperature.setText(MainApplication.getMyLocation().getCity() + MainApplication.getMyLocation().getDistrict());
            getWeather(MainApplication.getMyLocation().getDistrict(), 0);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    private void initView(List<New> aNews) {
        viewPager = findViewById(R.id.viewPager);
        adapter = new BannerViewPagerAdapter(this, aNews);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);//预加载2个
        viewPager.setPageMargin(30);//设置viewpage之间的间距
        viewPager.setClipChildren(false);
        viewPager.setPageTransformer(true, new CardTransformer());
        tvTemperature = findViewById(R.id.tv_temperature);
        ivWeather = findViewById(R.id.iv_weather);
        newSAdapter.setNewData(aNews);
//        mRecyclerView.setAdapter(newSAdapter);
        newSAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                New news = aNews.get(position);
                WebViewActivity.startWebView(MainActivity.this, news.conent, news.title, news.pubdate);
            }
        });
    }

    private void getData() {

        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getNewsList(AccountManager.getInstance().getUserId())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<New>>() {
                    @Override
                    public void onSuccess(List<New> news) {
                        initView(news);
                    }
                });
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
        if (TextUtils.isEmpty(weather)) {
            ivWeather.setImageResource(0);
            return;
        }
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


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
    }
}


