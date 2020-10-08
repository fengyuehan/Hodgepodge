package com.example.baselibrary.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class BaseLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    public static BaseLifecycleCallback getInstance(){
        return HolderClass.INSTANCE;
    }

    private static class HolderClass{
        private static BaseLifecycleCallback INSTANCE = new BaseLifecycleCallback();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        AppManager.getInstance().addActivity(activity);
    }
    /**
     * 必须在 Application 的 onCreate 方法中调用
     */
    public void init(Application application){
        application.registerActivityLifecycleCallbacks(this);
    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.getInstance().finishActivity(activity);
    }
}
