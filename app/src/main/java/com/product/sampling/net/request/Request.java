package com.product.sampling.net.request;

import com.product.sampling.bean.New;
import com.product.sampling.bean.SmsBean;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskMenu;
import com.product.sampling.bean.TaskResultBean;
import com.product.sampling.bean.UserInfoBean;
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
    String BASE_URL_DEBUG = "http://185.38.15.46/";
    String BASE_URL = "http://lw123.longwi.com:9080/exa/";


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

    @GET("app/user/sendcode")
    Observable<Response<String>> sendCode(@Query("persontel") String persontel);

    @GET("app/user/logintel")
    Observable<Response<UserInfoBean>> loginByPhone(@Query("persontel") String persontel, @Query("appcode") String appcode);

    @GET("app/user/login")
    Observable<Response<UserInfoBean>> loginByPwd(@Query("loginName") String loginName, @Query("password") String password);

    @GET("app/task/tasklist")
    Observable<Response<TaskResultBean>> taskList(@Query("userid") String userid, @Query("taskstatus") String taskstatus, @Query("ordertype") String ordertype);

    @GET("app/task/getdict")
    Observable<Response<TaskMenu>> taskMenu(@Query("type") String type, @Query("value") String value);

}
