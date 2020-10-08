package com.example.httplibrary.util;

import android.net.ParseException;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;


/**
 * Created by yf on 2017/11/29.
 * Email：yunfei10306@163.com
 * 描述：异常处理，和服务器约定的异常由于每个公司返回的Response结构都不一样这里就没有统一封装,可在回调onSuccess前处理
 */

public class ExceptionHandle {
    //未知错误
    private static final int UNKNOWN = 1000;
    //解析错误
    private static final int PARSE_ERROR = 1001;
    //网络错误
    private static final int NETWORD_ERROR = 1002;
    //证书出错
    private static final int SSL_ERROR = 1003;
    //网络无连接
    private static final int NO_NET = 1004;
    //未登录
    public static final int NO_LOGIN = 1005;
    //设备号不一致，登录被挤
    public static final int NO_SAME_DEVICE = 1006;
    //服务器返回超时
    public static final int TIMEOUT = 1007;

    private static ResponseThrowable ex;

    public static ResponseThrowable handleException(Throwable e) {
        if (ex == null) {
            ex = new ResponseThrowable();
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("请确认网络连接");
            ex.code = NO_NET;
            ex.message = "请确认网络连接";
            return ex;
        }

        //协议HTTP异常
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex.code = httpException.code();
            ex.message = httpException.message();
        } else if (e instanceof SocketTimeoutException) {
            ex.code = TIMEOUT;
            ex.message = "请求超时";
        } else if (e instanceof JSONException || e instanceof JsonParseException || e instanceof ParseException) {
            ex.code = PARSE_ERROR;
            ex.message = "数据解析错误";
        } else if (e instanceof ConnectException) {
            ex.code = NETWORD_ERROR;
            ex.message = "连接失败";
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex.code = SSL_ERROR;
            ex.message = "证书验证失败";
        } else {
            ex.message = "请检查网络再试";
            ex.code = UNKNOWN;
        }
        return ex;
    }

    public static class ResponseThrowable extends Exception {
        public int code;
        public String message;
    }
}
