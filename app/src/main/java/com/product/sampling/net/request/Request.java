package com.product.sampling.net.request;

import com.product.sampling.bean.New;
import com.product.sampling.bean.SmsBean;
import com.product.sampling.bean.Task;
import com.product.sampling.net.response.Response;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 创建时间：2018/7/2
 * 描述：
 */
public interface Request {
    /**
     * 服务器url
     */
    String BASE_URL = "http://185.38.15.46/";

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    @GET("yzm")
    Observable<Response<List<SmsBean>>> getSmsCode(@Query("user_phone") String phone);

    @GET("news")
    Observable<Response<List<New>>> getNewsList();

    @GET("tasklist")
    Observable<Response<List<Task>>> getTaskList();
}
