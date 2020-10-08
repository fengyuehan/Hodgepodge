package com.example.baselibrary.manager;

import android.app.Activity;

import java.util.Stack;

public class AppManager {
    /**
     * 如果在BaseActivity中添加入栈和出栈，将会导致activityStack内存泄漏
     * 建议：在Application中用registerActivityLifecycleCallbacks进行Activity生命周期的栈管理
     */

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager(){

    }

    public static AppManager getInstance(){
        if (instance == null){
            synchronized (AppManager.class){
                if (instance == null){
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * 把activity添加到堆栈
     */
    public void addActivity(Activity activity){
        if (activityStack == null){
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity(){
        if (activityStack == null || activityStack.size() == 0){
            return  null;
        }
        return activityStack.lastElement();
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity){
        if (activityStack == null){
            return;
        }
        if (activity != null && !activity.isFinishing()){
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrentActivity(){
        if (activityStack == null || activityStack.size() == 0){
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 移除指定的Activity
     * @param activity
     */
    public void removeActivity(Activity activity){
        if (activityStack == null){
            return;
        }
        if (activity != null){
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls){
        if (activityStack == null ||activityStack.size() == 0){
            return;
        }
        for (Activity activity:activityStack){
            if (activity.getClass().equals(cls)){
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        if (activityStack == null || activityStack.size() == 0){
            return;
        }
        for (Activity activity:activityStack){
            finishActivity(activity);
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit(Boolean isBackground){
        try {
            finishAllActivity();
            //杀死进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (!isBackground){
                System.exit(0);
            }
        }
    }
}
