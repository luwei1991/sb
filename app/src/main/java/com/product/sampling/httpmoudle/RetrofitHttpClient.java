package com.product.sampling.httpmoudle;

import com.product.sampling.utils.HUtils;
import com.product.sampling.utils.HttpsUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class RetrofitHttpClient {


    private static final int DEFAULT_MILLISECONDS = 60000;      // 默认的超时时间
    private static final int DEFAULT_RETRY_COUNT = 2;           // 默认重试次数
    private static final int DEFAULT_RETRY_INCREASE_DELAY = 0;  // 默认重试叠加时间
    private static final int DEFAULT_RETRY_DELAY = 500;         // 默认重试延时
    private static final int DEFAULT_CACHE_NEVER_EXPIRE = -1;   // 缓存过期时间，默认永久缓存

    private long mCacheTime = -1;                                     // 缓存时间
    private long mCacheMaxSize;                                       // 缓存大小
    private String mBaseUrl;                                          // 全局BaseUrl
    private int mRetryCount = DEFAULT_RETRY_COUNT;                    // 重试次数默认3次
    private int mRetryDelay = DEFAULT_RETRY_DELAY;                    // 延迟xx ms重试
    private int mRetryIncreaseDelay = DEFAULT_RETRY_INCREASE_DELAY;   // 叠加延迟

    private OkHttpClient.Builder okHttpClientBuilder;                 // OkHttp请求的客户端

    private Retrofit.Builder retrofitBuilder;                         // Retrofit请求Builder

    private volatile static RetrofitHttpClient singleton = null;

    private RetrofitHttpClient() {
        DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        retrofitBuilder = new Retrofit.Builder();
    }

    public static RetrofitHttpClient getInstance() {
        if (singleton == null) {
            synchronized (RetrofitHttpClient.class) {
                if (singleton == null) {
                    singleton = new RetrofitHttpClient();
                }
            }
        }
        return singleton;
    }

    public static OkHttpClient getOkHttpClient() {
        return getInstance().okHttpClientBuilder
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
    }

    public static Retrofit getRetrofit() {
        return getInstance().retrofitBuilder.build();
    }


  /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    }

    /**
     * https的全局访问规则
     */
    public RetrofitHttpClient setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * https的全局自签名证书
     */
    public RetrofitHttpClient setCertificates(InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }



    /**
     * 全局连接超时时间
     */
    public RetrofitHttpClient setConnectTimeout(long connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 超时重试次数
     */
    public RetrofitHttpClient setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        mRetryCount = retryCount;
        return this;
    }
 /**
     * 超时重试延迟时间
     */
    public RetrofitHttpClient setRetryDelay(int retryDelay) {
        if (retryDelay < 0) throw new IllegalArgumentException("retryDelay must > 0");
        mRetryDelay = retryDelay;
        return this;
    }
   /**
     * 超时重试延迟叠加时间
     */
    public RetrofitHttpClient setRetryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0)
            throw new IllegalArgumentException("retryIncreaseDelay must > 0");
        mRetryIncreaseDelay = retryIncreaseDelay;
        return this;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public static int getRetryIncreaseDelay() {
        return getInstance().mRetryIncreaseDelay;
    }

    /**
     * 全局的缓存过期时间
     */
    public RetrofitHttpClient setCacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = DEFAULT_CACHE_NEVER_EXPIRE;
        mCacheTime = cacheTime;
        return this;
    }

    /**
     * 获取全局的缓存过期时间
     */
    public static long getCacheTime() {
        return getInstance().mCacheTime;
    }

    /**
     * 全局的缓存大小,默认50M
     */
    public RetrofitHttpClient setCacheMaxSize(long maxSize) {
        mCacheMaxSize = maxSize;
        return this;
    }

    /**
     * 获取全局的缓存大小
     */
    public static long getCacheMaxSize() {
        return getInstance().mCacheMaxSize;
    }

    /**
     * 添加全局拦截器
     */
    public RetrofitHttpClient addInterceptor(Interceptor interceptor) {
        interceptor = HUtils.checkNotNull(interceptor, "interceptor == null");
        okHttpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    /**
     * 添加全局网络拦截器
     */
    public RetrofitHttpClient addNetworkInterceptor(Interceptor interceptor) {
        interceptor = HUtils.checkNotNull(interceptor, "interceptor == null");
        okHttpClientBuilder.addNetworkInterceptor(interceptor);
        return this;
    }

    public RetrofitHttpClient setOkClient(OkHttpClient client) {
        client = HUtils.checkNotNull(client, "client == null");
        retrofitBuilder.client(client);
        return this;
    }

    public RetrofitHttpClient addConverterFactory(Converter.Factory factory) {
        factory = HUtils.checkNotNull(factory, "factory == null");
        retrofitBuilder.addConverterFactory(factory);
        return this;
    }

    public RetrofitHttpClient addCallAdapterFactory(CallAdapter.Factory factory) {
        factory = HUtils.checkNotNull(factory, "factory == null");
        retrofitBuilder.addCallAdapterFactory(factory);
        return this;
    }

    public RetrofitHttpClient setCallbackExecutor(Executor executor) {
        executor = HUtils.checkNotNull(executor, "executor == null");
        retrofitBuilder.callbackExecutor(executor);
        return this;
    }

    public RetrofitHttpClient setCallFactory(okhttp3.Call.Factory factory) {
        factory = HUtils.checkNotNull(factory, "factory == null");
        retrofitBuilder.callFactory(factory);
        return this;
    }

    public RetrofitHttpClient setBaseUrl(String baseUrl) {
        mBaseUrl = HUtils.checkNotNull(baseUrl, "baseUrl == null");
        retrofitBuilder.baseUrl(mBaseUrl);
        return this;
    }





}
