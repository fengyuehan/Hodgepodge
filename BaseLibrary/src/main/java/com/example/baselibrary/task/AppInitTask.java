package com.example.baselibrary.task;

import com.example.baselibrary.LibApplication;
import com.example.baselibrary.manager.BaseLifecycleCallback;
import com.wxy.appstartfaster.task.AppStartTask;

import java.util.List;

public class AppInitTask extends AppStartTask {
    @Override
    public void run() {
        BaseLifecycleCallback.getInstance().init(LibApplication.getInstance());
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
