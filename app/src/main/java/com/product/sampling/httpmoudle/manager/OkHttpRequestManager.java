package com.product.sampling.httpmoudle.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.product.sampling.httpmoudle.body.ResponseProgressBody;
import com.product.sampling.httpmoudle.interfaces.IDownloadCallback;
import com.product.sampling.httpmoudle.interfaces.IRequestCallback;
import com.product.sampling.httpmoudle.interfaces.IRequestManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 创建时间：2018/5/26
 * 描述：
 */
public class OkHttpRequestManager implements IRequestManager {
    public static final String TAG = "network_request";
    private OkHttpClient okHttpClient;


    private Handler handler;

    public static OkHttpRequestManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private static class SingletonHolder {
        private static final OkHttpRequestManager INSTANCE = new OkHttpRequestManager();
    }


    public OkHttpRequestManager() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param requestCallback
     */
    @Override
    public void get(String url, Map<String, String> params, final IRequestCallback requestCallback) {
        //拼接url
        String get_url = url;
        if (params != null && params.size() > 0) {
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (i++ == 0) {
                    get_url = get_url + "?" + entry.getKey() + "=" + entry.getValue();
                } else {
                    get_url = get_url + "&" + entry.getKey() + "=" + entry.getValue();
                }
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .tag(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.i(TAG, "network failed. url is:" + call.request().url());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestCallback.onFailure(e);
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String response_body = response.body().string();
                    Log.i(TAG, response_body);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.onSuccess(response_body);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.onFailure(new Throwable("statusCode is:" + response.code()));
                        }
                    });

                }
            }
        });
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param requestCallback
     */
    @Override
    public void post(String url, Map<String, String> params, final IRequestCallback requestCallback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .tag(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.i(TAG, "network failed. url is:" + call.request().url());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestCallback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String response_body = response.body().string();
                    Log.i(TAG, response_body);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.onSuccess(response_body);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.onFailure(new Throwable("statusCode is:" + response.code()));
                        }
                    });

                }

            }
        });
    }

    @Override
    public void download(String url, final String fileDir, final String fileName, final IDownloadCallback downloadCallback) {
        Request request = new Request.Builder()
                .url(url)
                .tag(url)
                .build();

        okHttpClient.newBuilder()
                .addNetworkInterceptor(new Interceptor() {//设置拦截器
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ResponseProgressBody(originalResponse.body(), downloadCallback))
                                .build();
                    }
                })
                .build()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        downloadCallback.onFailure("onFailed:"+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response.isSuccessful()) {
                            File file = null;
                            try {
                                file = saveFile(response, fileDir, fileName);
                            } catch (final IOException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadCallback.onFailure("onResponse saveFile fail." + e.toString());
                                    }
                                });
                            }

                            final File newFile = file;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadCallback.onFinish(newFile);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadCallback.onFailure("fail status=" + response.code());
                                }
                            });
                        }

                    }
                });
    }

    //保存文件
    private File saveFile(Response response, String filedir, String filename) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            File dir = new File(filedir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return file;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
    }
}
