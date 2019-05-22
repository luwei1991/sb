package com.product.sampling.httpmoudle.interceptor;

import com.product.sampling.httpmoudle.Constants;
import com.product.sampling.httpmoudle.LocalManageUtil;
import com.product.sampling.ui.MainApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gongdongyang on 2018/10/6.
 */

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("content-type", "application/json;charset:utf-8");
        builder.addHeader("locale",
                LocalManageUtil.getSetLanguageLocale(MainApplication.INSTANCE).toString().replace("_", "-"));

        //https://www.jianshu.com/p/8753188b315c
        //IOException: unexpected end of stream on Connection
//        if (Build.VERSION.SDK_INT > 13) {
//            builder.addHeader("Connection", "close");
//        }

        builder.addHeader("src", "android");
        builder.addHeader("flavor", Constants.FLAVOR);
        {
            builder.addHeader("Cookie", "locale=" + LocalManageUtil.getSetLanguageLocale(MainApplication.INSTANCE).toString().replace("_", "-") + ";");
        }

        return chain.proceed(builder.build());
    }
}
