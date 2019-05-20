package com.product.sampling.net.request;

import com.product.sampling.bean.New;
import com.product.sampling.bean.SmsBean;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskMenu;
import com.product.sampling.bean.TaskResultBean;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.net.response.Response;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
    //    String BASE_URL = "http://nfxypf.natappfree.cc/";
    String BASE_URL = "http://lw123.longwi.com:9080/exa/";
    public static String IMAGE_BASE_URL = BASE_URL + "base/tBaFile/showImage?id=";

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

    @GET("app/user/changepassword")
    Observable<Response<String>> changepassword(@Query("userid") String userid, @Query("password") String password, @Query("newPassword") String newPassword);

    @GET("app/task/tasklist")
    Observable<Response<TaskResultBean>> taskList(@Query("userid") String userid, @Query("taskstatus") String taskstatus, @Query("ordertype") String ordertype);

    @GET("app/task/getdict")
    Observable<Response<TaskMenu>> taskMenu(@Query("type") String type, @Query("value") String value);

    /**
     * 获取省市接口
     *
     * @param areaid 省市唯一id 非必须
     * @param type   获取类型2获取省3获取市4获取区和县 非必须 默认获取省获取市区县要带areaid
     * @return
     */
    @GET("app/task/getarea")
    Observable<Response<TaskMenu>> getArea(@Query("areaid") String areaid, @Query("type") String type);

    /**
     * 修改图像接口
     * MultipartBody.Part
     *
     * @param userid 用户唯一id 必传 @Part("userid") RequestBody userid,
     * @param photo  头像图片流 必传
     * @return
     */
    @Multipart
    @POST("app/user/changephoto")
    Call<ResponseBody> setPhotoRequestBody(@Part MultipartBody.Part file);
}