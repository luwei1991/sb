package com.product.sampling.httpmoudle;

import com.product.sampling.httpmoudle.interceptor.AuthInterceptor;
import com.product.sampling.httpmoudle.interceptor.CookieInterceptor;
import com.product.sampling.net.request.Request;
import com.product.sampling.utils.UnSafeHostnameVerifier;

import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit
 * Created by gongdongyang on 2018/9/25.
 */

public class RetrofitService {

    private static Request httpApi;

    public static void init() {

        //控制台一直输出 isSBSettingEnabled false
        final Logger httpLogger = Logger.getLogger(HttpURLConnection.class.getName());
        httpLogger.setLevel(Level.OFF);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(Constants.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        RetrofitHttpClient.getInstance()
                .setConnectTimeout(10 * 1000)
                .setRetryDelay(500)//每次延时500ms重试
                .setRetryIncreaseDelay(500)//每次延时叠加500ms
                .setCertificates()//信任所有证书
                .setHostnameVerifier(new UnSafeHostnameVerifier(Constants.BASE_URL))//全局访问规则
                .setBaseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new CookieInterceptor())
                .addInterceptor(new AuthInterceptor())
                .setOkClient(RetrofitHttpClient.getOkHttpClient());


        httpApi = RetrofitHttpClient.getRetrofit().create(Request.class);



    }


    public static <T> T createApiService(Class<T> tClass) {
        return RetrofitHttpClient.getRetrofit().create(tClass);
    }

}
