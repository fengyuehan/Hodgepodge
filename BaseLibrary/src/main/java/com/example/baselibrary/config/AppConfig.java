package com.example.baselibrary.config;

import android.app.Application;

import com.blankj.utilcode.util.SPUtils;
import com.example.baselibrary.constant.Constant;

public class AppConfig {

    private static AppConfig instance;

    private AppConfig(){

    }

    public static AppConfig getInstance(){
        if (instance == null){
            synchronized (AppConfig.class){
                if (instance == null){
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }


    private boolean isLogin;
    private boolean isShowListImg;
    private boolean isShowGirlImg;
    private boolean isProbabilityShowImg;
    private int thumbnailQuality;
    private String bannerUrl;
    private boolean isNight;

    public void initConfig(Application application){
        //1.是否是登录状态
        isLogin = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_IS_LOGIN, false);
        //2.列表是否显示图片
        isShowListImg = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_IS_SHOW_LIST_IMG, true);
        //3.启动页是否是妹子图
        isShowGirlImg = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_IS_SHOW_GIRL_IMG, false);
        //4.启动页是否是妹子图
        isProbabilityShowImg = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_IS_SHOW_IMG_RANDOM, false);
        //5.缩略图质量 0：原图 1：默认 2：省流
        thumbnailQuality = SPUtils.getInstance(Constant.SP_NAME).getInt(Constant.KEY_THUMBNAIL_QUALITY, 1);
        //6.Banner URL 用于加载页显示
        bannerUrl = SPUtils.getInstance(Constant.SP_NAME).getString(Constant.KEY_BANNER_URL, "");
        //7.初始化夜间模式
        isNight = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_NIGHT_STATE);
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isShowListImg() {
        return isShowListImg;
    }

    public void setShowListImg(boolean showListImg) {
        isShowListImg = showListImg;
    }

    public boolean isShowGirlImg() {
        return isShowGirlImg;
    }

    public void setShowGirlImg(boolean showGirlImg) {
        isShowGirlImg = showGirlImg;
    }

    public boolean isProbabilityShowImg() {
        return isProbabilityShowImg;
    }

    public void setProbabilityShowImg(boolean probabilityShowImg) {
        isProbabilityShowImg = probabilityShowImg;
    }

    public int getThumbnailQuality() {
        return thumbnailQuality;
    }

    public void setThumbnailQuality(int thumbnailQuality) {
        this.thumbnailQuality = thumbnailQuality;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        isNight = night;
    }
}
