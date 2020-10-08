package com.example.baselibrary.task;

import com.alibaba.android.arouter.BuildConfig;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.example.baselibrary.LibApplication;
import com.wxy.appstartfaster.task.AppStartTask;

import java.util.List;

public class ARouterTask extends AppStartTask {
    @Override
    public void run() {
        initARouter();
    }

    private void initARouter() {
        if (BuildConfig.DEBUG) {
            //打印日志
            ARouter.openLog();
            //开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
        //推荐在Application中初始化
        ARouter.init(LibApplication.getInstance());
    }

    @Override
    public boolean isRunOnMainThread() {
        return true;
    }

    @Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        return null;
    }
}
