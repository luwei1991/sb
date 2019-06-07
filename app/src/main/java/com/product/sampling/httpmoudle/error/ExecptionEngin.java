package com.product.sampling.httpmoudle.error;

import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.product.sampling.R;
import com.product.sampling.ui.MainApplication;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import retrofit2.HttpException;

public class ExecptionEngin {


    //业务返回数据成功
    public static final int RESPONSE_CODE_OK = 0;

    //业务返回数据错误
    public static final int RESPONSE_CODE_FAILED = -1;

    //对应HTTP的状态码
    public static final int ERROR_ACCOUNT_PSW = 201; // 登录信息
    public static final int UNAUTHORIZED = 401; // 登录信息过期
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVER_ERROR = 512;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;


    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络错误
     */
    public static final int SSL_ERROR = 1004;
    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = 1003;

    /**
     * 没有相关权限
     */
    public static final int ERROR_NO_PREMESSION = 1005;


    public static ApiException handleException(Throwable e) {
        if (e != null) {
            Log.e("error:", e.toString());
        }
        ApiException ex;
        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, httpException.code());
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.setDisplayMessage(MainApplication.INSTANCE.getResources().getString(R.string.auth_info_faild));
                case FORBIDDEN:
                case NOT_FOUND:
                    ex.setDisplayMessage("404:" + MainApplication.INSTANCE.getString(R.string.app_net_error_msg));
                    break;
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVER_ERROR:
                    ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.app_net_error_msg));
                    break;
                case SERVICE_UNAVAILABLE:
                default:
                    ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.net_error_msg));  //均视为网络错误
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            /***服务器返回的业务类型的错误**/
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.getCode());
            ex.setDisplayMessage(resultException.getMsg());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ApiException(e, PARSE_ERROR);
            ex.setDisplayMessage("解析错误");            //均视为解析错误
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, HTTP_ERROR);
            ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.error_network_later_try));  //均视为网络错误
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, HTTP_ERROR);
            ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.error_network_time_out));  //均视为网络错误
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, HTTP_ERROR);
            ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.net_error_msg));  //均视为网络错误
            return ex;
        } else if (e instanceof SecurityException) {
            ex = new ApiException(e, ERROR_NO_PREMESSION);
            return ex;
        } else if (e instanceof SSLException) {
            ex = new ApiException(e, SSL_ERROR);
            ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.error_network_later_try));  //均视为网络错误
            return ex;
        } else if (e instanceof IOException) {
            ex = new ApiException(e, SSL_ERROR);
            ex.setDisplayMessage(MainApplication.INSTANCE.getString(R.string.error_network_later_try));  //均视为网络错误
            return ex;
        } else {
            ex = new ApiException(e, UNKNOWN);
            ex.setDisplayMessage(e.getMessage());          //未知错误
            return ex;
        }
    }


    public static boolean isNetWorkError(int code) {
        return code == ExecptionEngin.NOT_FOUND ||
                code == ExecptionEngin.HTTP_ERROR ||
                code == ExecptionEngin.SSL_ERROR ||
                code == ExecptionEngin.GATEWAY_TIMEOUT;
    }

}
