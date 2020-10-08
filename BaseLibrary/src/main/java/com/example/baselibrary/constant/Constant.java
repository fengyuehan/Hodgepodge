package com.example.baselibrary.constant;

import android.os.Environment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constant {

    public static final int REALM_VERSION = 1;
    public static final String REALM_NAME = "hodgepodge";
    public static final String SP_NAME = "zzf";
    public static final String EXTERNAL_STORAGE_DIRECTORY =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    public static final String GITHUB = "https://github.com/fengyuehan";
    public static final String HODGEPODGE = "https://github.com/fengyuehan/Hodgepodge";
    public static final String JIAN_SHU = "https://www.jianshu.com/u/8fe2d81fb8b2";
    public static final String JUE_JIN = "https://juejin.im/user/1398234521022567/posts";
    public static final String ANDROID_NOTE = "https://github.com/fengyuehan/Java_Android_Note";


    /**
     * 配合CoordinatorLayout使用
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface STATES{
        int EXPANDED = 3;
        int COLLAPSED = 2;
        int INTERMEDIATE = 1;
    }


    public interface viewType{
        int typeBanner = 1;         //轮播图
        int typeGv = 2;             //九宫格
        int typeTitle = 3;          //标题
        int typeList = 4;           //list
        int typeNews = 5;           //新闻
        int typeMarquee = 6;        //跑马灯
        int typePlus = 7 ;          //不规则视图
        int typeSticky = 8;         //指示器
        int typeFooter = 9;         //底部
        int typeGvSecond = 10;      //九宫格
    }

    public class status{
        public static final int success=200;
        public static final int error=-1;
    }


    /**-------------------------------------键-------------------------------------------------**/
    //Sp键
    public static final String KEY_FIRST_SPLASH = "first_splash";                 //是否第一次启动
    public static final String KEY_IS_LOGIN = "is_login";                         //登录
    public static final String KEY_IS_SHOW_LIST_IMG = "is_show_list_img";         //是否展示list页面图片
    public static final String KEY_IS_SHOW_GIRL_IMG = "is_show_girl_img";         //启动页是否是妹子图
    public static final String KEY_IS_SHOW_IMG_RANDOM = "is_show_girl_random";    //启动页是否概率出现
    public static final String KEY_THUMBNAIL_QUALITY = "thumbnail_quality";       //启动页是否概率出现
    public static final String KEY_BANNER_URL = "banner_url";                     //启动页是否概率出现
    public static final String KEY_NIGHT_STATE = "night_state";                   //启动页夜间模式


    /**-------------------------------------集合-------------------------------------------------**/
    //
    //public static List<HomeBlogEntity> findNews = new ArrayList<>();
    //public static List<HomeBlogEntity> findBottomNews = new ArrayList<>();

    /**-------------------------------------集合-------------------------------------------------**/
    public static final String URL = "url";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";


    /**-------------------------------------music-------------------------------------------------**/
    public static final String EXTRA_NOTIFICATION = "extra_notification";
    public static final String LOCK_SCREEN = "lock_screen";
    public static final String LOCK_SCREEN_ACTION = "cn.ycbjie.lock";
    public static final String FILTER_SIZE = "filter_size";
    public static final String FILTER_TIME = "filter_time";
    public static final String MUSIC_ID = "music_id";
    public static final String PLAY_MODE = "play_mode";
    public static final String IS_SCREEN_LOCK = "is_screen_lock";
    public static final String APP_OPEN_COUNT = "app_open_count";
    public static final String PLAY_POSITION = "play_position";

}
