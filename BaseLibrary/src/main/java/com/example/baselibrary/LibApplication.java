package com.example.baselibrary;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.example.baselibrary.task.CrashTask;
import com.example.baselibrary.task.NetworkTask;
import com.example.baselibrary.task.UtilTask;
import com.example.baselibrary.task.WebviewTask;
import com.wxy.appstartfaster.dispatcher.AppStartTaskDispatcher;

public class LibApplication extends Application {
    private static LibApplication instance;

    public static LibApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /**
         * 采用启动器来初始化SDK
         */
        AppStartTaskDispatcher.getInstance()
                .setContext(this)
                .setShowLog(true)
                .setAllTaskWaitTimeOut(1000)
                .addAppStartTask(new WebviewTask())
                .addAppStartTask(new UtilTask())
                .addAppStartTask(new CrashTask())
                .addAppStartTask(new NetworkTask())
                .start()
                .await();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN){
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }
}
