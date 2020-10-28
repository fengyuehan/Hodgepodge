package com.example.baselibrary.task;

import com.example.baselibrary.LibApplication;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.wxy.appstartfaster.task.AppStartTask;
import com.ycbjie.webviewlib.utils.X5WebUtils;

import java.util.HashMap;
import java.util.List;

public class WebviewTask extends AppStartTask {
    @Override
    public void run() {
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(true);
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
