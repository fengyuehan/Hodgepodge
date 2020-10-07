package com.example.baselibrary.task;

import com.example.baselibrary.LibApplication;
import com.wxy.appstartfaster.task.AppStartTask;
import com.ycbjie.webviewlib.utils.X5WebUtils;

import java.util.List;

public class WebviewTask extends AppStartTask {
    @Override
    public void run() {
        X5WebUtils.init(LibApplication.getInstance());
    }

    @Override
    public boolean isRunOnMainThread() {
        return false;
    }

    @Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        return null;
    }
}
