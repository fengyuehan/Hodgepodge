package com.example.baselibrary;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.example.baselibrary.task.ARouterTask;
import com.example.baselibrary.task.AppInitTask;
import com.example.baselibrary.task.ConfigTask;
import com.example.baselibrary.task.CrashTask;
import com.example.baselibrary.task.NetworkTask;
import com.example.baselibrary.task.UtilTask;
import com.example.baselibrary.task.WebviewTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.wxy.appstartfaster.dispatcher.AppStartTaskDispatcher;

public class LibApplication extends Application {
    private static final String MAIN_PROCESS_NAME = "com.example.hodgepodge";
    private static LibApplication instance;

    public static LibApplication getInstance(){
        return instance;
    }

    static {
        //下拉刷新，上拉加载头尾全局设置
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context).setTextSizeTitle(12));
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setTextSizeTitle(12));
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
                .addAppStartTask(new AppInitTask())
                .addAppStartTask(new ARouterTask())
                .addAppStartTask(new ConfigTask())
                .start()
                .await();
        initWebView();
    }

    /**
     * Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
     * 为其它进程webView设置目录
     * 解决Using WebView from more than one process at once with the same data directory is not supported报错
     */
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcess();
            if (TextUtils.isEmpty(processName)) {
                return;
            }

            //判断不等于默认进程名称
            if (!MAIN_PROCESS_NAME.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private String getProcess() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
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
