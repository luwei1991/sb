package com.product.sampling.net.request;

import com.product.sampling.bean.FastMail;
import com.product.sampling.bean.New;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.SmsBean;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskBean;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.bean.UpdateEntity;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.bean.VerifyTaskStatusVo;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.net.response.Response;
import com.product.sampling.ui.form.bean.CodeCompany;
import com.product.sampling.ui.masterplate.bean.MasterpleListBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 创建时间：2018/7/2
 * 描述：
 */
public interface Request {

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    @GET("yzm")
    Observable<Response<List<SmsBean>>> getSmsCode(@Query("user_phone") String phone);

    @GET("app/common/newslist")
    Observable<BaseHttpResult<List<New>>> getNewsList(@Query("userid") String userid);

    @GET("tasklist")
    Observable<Response<List<Task>>> getTaskList();

    @GET("app/user/sendcode")
    Observable<BaseHttpResult<String>> sendCode(@Query("persontel") String persontel);

    @GET("app/user/logintel")
    Observable<BaseHttpResult<UserInfoBean>> loginByPhone(@Query("persontel") String persontel, @Query("appcode") String appcode);

    @GET("app/user/login")
    Observable<BaseHttpResult<UserInfoBean>> loginByPwd(@Query("loginName") String loginName, @Query("password") String password,@Query("versionCode") String versionCode);

    @GET("app/user/changepassword")
    Observable<BaseHttpResult> changepassword(@Query("userid") String userid, @Query("password") String password, @Query("newPassword") String newPassword);

    @GET("app/task/tasklist")
    Observable<BaseHttpResult<TaskBean>> taskList(@Query("userid") String userid, @Query("taskstatus") String taskstatus, @Query("ordertype") String ordertype, @Query("areashi") String areashi, @Query("pageNo") int pageNo,@Query("tasktypecount") String tasktypecount,@Query("taskCode") String taskCode,@Query("companyname") String companyname);

//    @GET("app/task/getdict")
//    Observable<Response<TaskProvince>> taskMenu(@Query("type") String type, @Query("value") String value);
    //获取任务来源和任务类别接口以及抽样单位名称
    @GET("app/common/getjointtype")
    Observable<BaseHttpResult<List<TaskCompany>>> getTypeContet(@Query("type") String type);
    /**获取模版列表*/
    @GET("app/report/mouldlist")
    Observable<BaseHttpResult<List<MasterpleListBean>>> getMouldList(@Query("userid") String userid,@Query("reporttype")String reporttype);
    /**增加模版列表*/
    @GET("app/report/savemould")
    Observable<BaseHttpResult<MasterpleListBean>> addMould(@Query("userid") String userid,@Query("reporttype")String reporttype,@Query("mouldtitle")String mouldtitle);
    /**更新模版列表*/
    @GET("app/report/savemould")
    Observable<BaseHttpResult<MasterpleListBean>> updateMould(@Query("userid") String userid,@Query("reporttype")String reporttype,@Query("mouldtitle")String mouldtitle,@Query("id")String id);

    /**删除模版列表*/
    @GET("app/report/deletemould")
    Observable<BaseHttpResult<MasterpleListBean>> deleteMould(@Query("userid") String userid,@Query("id")String id);


    @GET("app/report/getmouldInfo")
    Observable<BaseHttpResult<Sampling>> getMouldInfo(@Query("userid") String userid, @Query("id")String id, @Query("reporttype")String reporttype);

    @POST("app/report/savemouldInfo")
    Observable<BaseHttpResult<String>> savesMoudleInfonByBody(@Body MultipartBody file);
    /**删除样品信息接口*/
    @GET("app/report/deleteSamplingCode")
    Observable<BaseHttpResult<String>> deleteSamplingCode(@Query("userid") String userid, @Query("sampleid")String sampleid, @Query("reporttype")String reporttype);
//    /**
//     * 获取省市接口
//     */
//    @GET("app/task/getarea")
//    Observable<BaseHttpResult<List<TaskProvince>>> getArea();

    /**
     * 修改图像接口
     * MultipartBody.Part
     *
     * @param file * @param userid 用户唯一id 必传 @Part("userid") RequestBody userid, * @param photo  头像图片流 必传
     */
    @POST("app/user/changephoto")
    Observable<BaseHttpResult<String>> setPhotoRequestBody(@Body RequestBody file);

    /**
     * 上传和更新现场信息和异常信息
     *
     * @param file
     * @return
     */
    @POST("app/task/uploadtaskinfo")
    Observable<BaseHttpResult<String>> uploadtaskinfo(@Body MultipartBody file);

    @POST("app/task/savesample")
    Observable<BaseHttpResult<String>> savesampleByBody(@Body MultipartBody file);

    @GET("app/user/uploadaddress")
    Observable<BaseHttpResult> uploadaddress(@Query("userid") String userid, @Query("longitude") String longitude, @Query("latitude") String latitude);

    @GET("app/task/taskdetail")
    Observable<BaseHttpResult<TaskEntity>> taskdetail(@Query("userid") String userid, @Query("id") String id);

    /**
     * 获取抽样单接口
     * @param userid
     * @param id
     * @return
     */
    @GET("app/task/tasksamples")
    Observable<BaseHttpResult<List<TaskSample>>> tasksamples(@Query("userid") String userid, @Query("id") String id);

    @GET("base/tBaFile/showImage")
    Observable<ResponseBody> downloadVideo(@Query("id") String id);

    @POST("http://ip.taobao.com/service/getIpInfo2.php")
    Observable<ResponseBody> getIPCountry(@Query("ip") String ip);


    @POST("app/common/newversion")
    Observable<BaseHttpResult<UpdateEntity>> getAppVersion(@Query("userid") String userid, @Query("versioncode") String versioncode);

    @POST("app/common/reportcode")
    Observable<BaseHttpResult<String>> getChecFormTaskCode(@Query("userid") String userid, @Query("taskid") String taskid,@Query("sampleid") String sampleid,@Query("reporttype") String reporttype);

    @POST("app/common/codeCompanyByode")
    Observable<BaseHttpResult<List<CodeCompany>>> getCompanyIfonByComID(@Query("userid") String userid, @Query("code") String code);

    @GET("app/task/uploadaddress")
    Observable<BaseHttpResult> updateCompanyAddress(@Query("userid") String userid, @Query("companyaddress") String companyaddress, @Query("taskid") String taskid, @Query("remark") String remark);

    @FormUrlEncoded
    @POST("app/task/samplefastmail")
    Observable<BaseHttpResult> updateFastMail(@FieldMap Map<String, String> params);

    @GET("app/task/getsamplefastmail")
    Observable<BaseHttpResult<FastMail>> getFastMail(@Query("userid") String userid, @Query("sampleid") String sampleid, @Query("taskid") String taskid);

    @GET("app/common/rememberReportCount")
    Observable<BaseHttpResult> sendReportRecord(@Query("userid") String userid, @Query("sampleid") String sampleid, @Query("taskid") String taskid, @Query("reporttype") String reporttype);

    //确认执行任务
    @GET("app/common/startdotask")
    Observable<BaseHttpResult> uploadtaskinfo(@Query("userid") String userid, @Query("id") String id,@Query("suredoaddress") String suredoaddress);

    /**电子签章接口*/
    @POST("app/common/pdfcode")
    Observable<BaseHttpResult<String>> pdfSignElc(@Body MultipartBody file);

    /**抽样单*/
    @POST("app/pdf/expSampleSheetPdfNew")
    Observable<BaseHttpResult<String>> checForm(@Body MultipartBody file);


    /**处置单*/
    @POST("app/pdf/createCZTZDPdf")
    Observable<BaseHttpResult<String>> handleForm(@Body MultipartBody file);

    /**未抽到样单*/
    @POST("app/pdf/createWCDYPPdf")
    Observable<BaseHttpResult<String>> notCheckForm(@Body MultipartBody file);
    /**拒检单*/
    @POST("app/pdf/createjjpdf")
    Observable<BaseHttpResult<String>> refusekForm(@Body MultipartBody file);

    /**意见单*/
    @POST("app/pdf/createfeedbackpdf")
    Observable<BaseHttpResult<String>> feedBackkForm(@Body MultipartBody file);

    @Streaming
    @GET
    Call<ResponseBody> downloadPDF(@Url String url);

    /**获取该企业已经抽样的产品名称*/
    @GET("app/report/checkProduceName")
    Observable<BaseHttpResult<String>> checkProduceName(@Query("planId") String planId,@Query("produceName") String produceName);

    /**获取该任务已经生成PDF的抽样单号*/
    @GET("app/report/getCreatedPdfSampleId")
    Observable<BaseHttpResult<String>> getSuccesPDFSampleId(@Query("taskid") String taksId,@Query("codenum") String sampleCode);

    /**根据抽样单号获取任务编号*/
    @GET("app/common/taskCode")
    Observable<BaseHttpResult<VerifyTaskStatusVo>> getTaskCodeBySampleCode(@Query("userid") String userId, @Query("codenum") String sampldCode);
}