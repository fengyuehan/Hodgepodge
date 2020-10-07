package com.example.baselibrary.task;

import com.example.baselibrary.LibApplication;
import com.wxy.appstartfaster.task.AppStartTask;
import com.yc.toollib.BuildConfig;
import com.yc.toollib.network.utils.NetworkTool;

public class NetworkTask extends AppStartTask {
    @Override
    public void run() {
        initNetwork();
    }

    private void initNetwork() {
        NetworkTool.getInstance().init(LibApplication.getInstance());
        //建议只在debug环境下显示，点击去网络拦截列表页面查看网络请求数据
        if (BuildConfig.DEBUG){
            NetworkTool.getInstance().setFloat(LibApplication.getInstance());
        }
    }

    @Override
    public boolean isRunOnMainThread() {
        return true;
    }
}
