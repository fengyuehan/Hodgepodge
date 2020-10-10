package com.example.baselibrary.http.intercepter;


import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上传错误信息到bugly
 */
public class ErrorInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!response.isSuccessful()) {
            String msg = request.url() + "  " + request.method() + "  " + response.code() + "  " + response.message();
            CrashReport.postCatchedException(new Throwable(msg));
        }
        return null;
    }
}
