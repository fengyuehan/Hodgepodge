package com.example.baselibrary.task;

import com.example.baselibrary.LibApplication;
import com.wxy.appstartfaster.task.AppStartTask;
import com.yc.toollib.crash.CrashHandler;
import com.yc.toollib.crash.CrashListener;
import com.yc.toollib.crash.CrashToolUtils;

import java.util.List;

public class CrashTask extends AppStartTask {
    @Override
    public void run() {
        initCrash();
    }

    private void initCrash() {
        CrashHandler.getInstance().init(LibApplication.getInstance(), new CrashListener() {
            /**
             * 重启app
             */
            @Override
            public void againStartApp() {
                System.out.println("崩溃重启----------againStartApp------");
                CrashToolUtils.reStartApp1(LibApplication.getInstance(),
                        2000);
                //CrashToolUtils.reStartApp2(App.this,2000, MainActivity.class);
                //CrashToolUtils.reStartApp3(App.this);
            }

            /**
             * 自定义上传crash，支持开发者上传自己捕获的crash数据
             * @param ex                        ex
             */
            @Override
            public void recordException(Throwable ex) {
                System.out.println("崩溃重启----------recordException------");
                //自定义上传crash，支持开发者上传自己捕获的crash数据
                //StatService.recordException(getApplication(), ex);
            }
        });
    }

    @Override
    public boolean isRunOnMainThread() {
        return true;
    }
    /**
     * 如果是相互依赖，则需要下面这样写
     */
    /*@Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        List<Class<? extends AppStartTask>> dependsTaskList = new ArrayList<>();
        dependsTaskList.add(TestAppStartTaskTwo.class);
        dependsTaskList.add(TestAppStartTaskThree.class);
        return dependsTaskList;
    }*/

    @Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        return null;
    }
}
