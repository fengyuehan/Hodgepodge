package com.example.baselibrary.task;

import com.example.baselibrary.LibApplication;
import com.example.baselibrary.config.AppConfig;
import com.wxy.appstartfaster.task.AppStartTask;

import java.util.List;

public class ConfigTask extends AppStartTask {
    @Override
    public void run() {
        AppConfig.getInstance().initConfig(LibApplication.getInstance());
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
