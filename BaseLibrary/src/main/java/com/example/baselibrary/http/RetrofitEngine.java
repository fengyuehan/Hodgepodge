package com.example.baselibrary.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.blankj.utilcode.util.NetworkUtils;
import com.example.baselibrary.LibApplication;
import com.example.baselibrary.http.constants.HttpConstants;
import com.example.baselibrary.http.convert.GsonConverterFactory;
import com.example.baselibrary.http.cookie.PersistentCookieJar;
import com.example.baselibrary.http.cookie.cache.SetCookieCache;
import com.example.baselibrary.http.cookie.persistence.SharedPrefsCookiePersistor;
import com.example.baselibrary.http.intercepter.ErrorInterceptor;
import com.example.baselibrary.http.intercepter.HeadInterceptor;
import com.example.baselibrary.http.intercepter.LogInterceptor;
import com.example.baselibrary.http.util.HttpsUtil;
import com.trello.rxlifecycle2.android.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 特点：
 * ① 支持无网络时数据缓存，无需服务器的支持(仅限GET请求)
 * ② 支持过滤恶意频繁网络请求，减轻服务器压力
 * ③ 支持cookie头数据的自动加载及持久化
 * ④ 支持版本更新
 */
public class RetrofitEngine {

    //缓存的默认大小 5M 根据需要修改
    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 100;

    //缓存的默认文件夹
    private static final String DEFAULT_CACHE_FILE = "HttpCache";

    //缓存cookie及加载cookie的工具类，内存中缓存，初始化时，会利用SharedPrefsCookiePersistor将持久化的cookie加载到内存
    private static PersistentCookieJar cookieJar;

    //持久化cookie的工具类，利用sp实现
    private static SharedPrefsCookiePersistor cookiePersistor;

    private static OkHttpClient okHttpClient;
    private static OkHttpClient okHttpClientForDownload;
    private static Retrofit retrofit;
    private static Retrofit retrofitForDownload;

    private RetrofitEngine() {
    }

    /**
     * 获取retrofit 实例
     *
     * @return retrofit实例
     */
    public static Retrofit getInstance() {
        if (okHttpClient == null) {
            initOkhttpClient();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(HttpConstants.HTTP_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }



    /**
     * 初始化 okhttpClient
     */
    private static void initOkhttpClient() {
        cookiePersistor = new SharedPrefsCookiePersistor(LibApplication.getInstance());
        cookieJar = new PersistentCookieJar(new SetCookieCache(), cookiePersistor);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                //支持自动持久化cookie和自动添加cookie
                //.cookieJar(cookieJar)
                //错误重连
                .retryOnConnectionFailure(true)
                //没有网络，加载缓存(仅限GET)
                //.addInterceptor(new ForceCacheInterceptor(context))
                //添加请求头(用时注意打开)
                .addInterceptor(new HeadInterceptor())
                //过滤频繁请求，5s为缓存时间，单位秒,5秒之内反复请求，取缓存，超出5秒，取服务器数据
                //.addNetworkInterceptor(new FilterFastRequestInterceptor(5))
                //.addInterceptor(new LanguageInterceptor())
                .addInterceptor(new ErrorInterceptor())
                .addInterceptor(mRewriteCacheControlInterceptor)
                .addNetworkInterceptor(mRewriteCacheControlInterceptor);
                //缓存
                //.cache(new Cache(new File(AntApplication.getInstance().getCacheDir(), DEFAULT_CACHE_FILE), DEFAULT_CACHE_SIZE));
        //打印请求日志
        if (BuildConfig.DEBUG) {
            //builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            builder.addInterceptor(new LogInterceptor());
        }
        HttpsUtil.SSLParams sslParams = HttpsUtil.getSslSocketFactory(null, null, null);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);


        okHttpClient = builder.build();
    }


    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private static final Interceptor mRewriteCacheControlInterceptor = chain -> {
        //拿到请求体
        Request request = chain.request();
        //读接口上的@Headers里的注解配置
        String cacheControl = request.cacheControl().toString();

        //判断没有网络并且添加了@Headers注解,才使用网络缓存.
        if (!NetworkUtils.isConnected() && !TextUtils.isEmpty(cacheControl)) {
            //重置请求体;
            request = request.newBuilder()
                    //强制使用缓存
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        //如果没有添加注解,则不缓存
        if (TextUtils.isEmpty(cacheControl) || "no-store".contains(cacheControl)) {
            //响应头设置成无缓存
            cacheControl = "no-store";
        } else if (NetworkUtils.isConnected()) {
            //如果有网络,则将缓存的过期时间,设置为0,获取最新数据
            cacheControl = "public, " + HttpConstants.sCACHE_CONTROL_AGE;
        } else {
            //...如果无网络,则根据@headers注解的设置进行缓存.
            cacheControl = "public, only-if-cached, max-stale=" + HttpConstants.sCACHE_STALE_SEC;
        }
        Response response = chain.proceed(request);
        return response.newBuilder()
                .header("Cache-Control", cacheControl)
                .removeHeader("Pragma")
                .build();
    };

    /**
     * 退出登录时，清除cookie数据
     */
    public void logout() {
        cookieJar.clear();
    }

    /**
     * 判断是否处于登录状态
     *
     * @param loginUrl
     * @return
     */
    public boolean isLogin(String loginUrl) {
        return cookiePersistor.isLogin(loginUrl);
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
