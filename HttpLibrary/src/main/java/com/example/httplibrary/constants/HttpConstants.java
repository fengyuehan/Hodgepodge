package com.example.httplibrary.constants;

public class HttpConstants {

    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
    //(假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
    public static final String sCACHE_CONTROL_AGE = "max-age=0";
    //设缓存有效期为两天
    public static final long sCACHE_STALE_SEC = 60 * 60 * 24 * 2;

    //服务器地址
    public static String HTTP_URL;
}
