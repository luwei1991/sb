package com.product.sampling.net.interceptor;

import android.util.Log;

import com.product.sampling.MainApplication;
import com.product.sampling.utils.AppUtils;
import com.product.sampling.utils.DeviceUtils;
import com.product.sampling.utils.NetWorkUtils;
import com.product.sampling.utils.PreferenceUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * 创建时间：2018/7/4
 * 描述：
 */
public class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuild;
        String method = oldRequest.method();
        String postBodyString = "";
        if ("POST".equals(method)) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            formBodyBuilder.add("phone_tag", DeviceUtils.getUniqueId(MainApplication.INSTANCE));
            formBodyBuilder.add("user_city", PreferenceUtils.getInstance().getStringValue(PreferenceUtils.USER_CITY));
            formBodyBuilder.add("login_addr", PreferenceUtils.getInstance().getStringValue(PreferenceUtils.USER_ADDRESS));
            formBodyBuilder.add("login_ip", NetWorkUtils.getIpAddress(MainApplication.INSTANCE));
            formBodyBuilder.add("phone_info", DeviceUtils.getPhoneInfo());
            formBodyBuilder.add("phone_net", NetWorkUtils.getNetworkStateName(MainApplication.INSTANCE));
            formBodyBuilder.add("remark", AppUtils.getChannelCode(MainApplication.INSTANCE));
            formBodyBuilder.add("app_version", AppUtils.getVersionCode(MainApplication.INSTANCE) + "");
            newRequestBuild = oldRequest.newBuilder();

            RequestBody formBody = formBodyBuilder.build();
            postBodyString = bodyToString(oldRequest.body());
            postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
            newRequestBuild.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
        } else {
            // 添加新的参数
            HttpUrl.Builder commonParamsUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
                    .addQueryParameter("phone_tag", DeviceUtils.getUniqueId(MainApplication.INSTANCE))
                    .addQueryParameter("user_city", PreferenceUtils.getInstance().getStringValue(PreferenceUtils.USER_CITY))
                    .addQueryParameter("login_addr", PreferenceUtils.getInstance().getStringValue(PreferenceUtils.USER_ADDRESS))
                    .addQueryParameter("login_ip", NetWorkUtils.getIpAddress(MainApplication.INSTANCE))
                    .addQueryParameter("phone_info", DeviceUtils.getPhoneInfo())
                    .addQueryParameter("phone_net", NetWorkUtils.getNetworkStateName(MainApplication.INSTANCE))
                    .addQueryParameter("remark", AppUtils.getChannelCode(MainApplication.INSTANCE))
                    .addQueryParameter("app_version", AppUtils.getVersionCode(MainApplication.INSTANCE) + "");
            newRequestBuild = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(commonParamsUrlBuilder.build());
        }
        Request newRequest = newRequestBuild
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "zh")
                .build();

        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(newRequest);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        int httpStatus = response.code();
        StringBuilder logSB = new StringBuilder();
        logSB.append("-------start:" + method + "|");
        logSB.append(newRequest.toString() + "\n|");
        logSB.append(method.equalsIgnoreCase("POST") ? "post参数{" + postBodyString + "}\n|" : "");
        logSB.append("httpCode=" + httpStatus + ";Response:" + content + "\n|");
        logSB.append("----------End:" + duration + "毫秒----------");
        Log.d(TAG, logSB.toString());
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
