package com.product.sampling.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.product.sampling.net.interceptor.LogInterceptor;
import com.product.sampling.net.request.Request;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.product.sampling.net.request.Request.BASE_URL;

/**
 * 创建时间：2018/7/2
 * 描述：
 */
public class NetWorkManager {

    private static NetWorkManager mInstance;
    private static Retrofit retrofit;
    private static volatile Request request = null;
    private static Retrofit retrofitSample;

    public static NetWorkManager getInstance() {
        if (mInstance == null) {
            synchronized (NetWorkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化必要对象和参数
     */
    public void init() {
        // 初始化okhttp
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // 初始化Retrofit
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Request.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        retrofitSample = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Request getRequest() {
        if (request == null) {
            synchronized (Request.class) {
                request = retrofit.create(Request.class);
            }
        }
        return request;
    }

    public static Retrofit getSimpleRequset() {
        return retrofitSample;
    }

}
