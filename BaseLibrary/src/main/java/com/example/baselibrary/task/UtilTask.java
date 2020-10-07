package com.example.baselibrary.task;

import com.blankj.utilcode.util.LogUtils;
import com.wxy.appstartfaster.task.AppStartTask;

public class UtilTask extends AppStartTask {
    @Override
    public void run() {
        initUtils();
    }

    private void initUtils() {
        LogUtils.Config config = LogUtils.getConfig();
        //边框开关，设置打开
        config.setBorderSwitch(true);
        //logcat 是否打印，设置打印
        config.setConsoleSwitch(true);
        //设置打印日志总开关，线上时关闭
        config.setLogSwitch(true);
    }

    @Override
    public boolean isRunOnMainThread() {
        return false;
    }
}
