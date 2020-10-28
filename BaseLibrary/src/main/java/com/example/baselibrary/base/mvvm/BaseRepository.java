package com.example.baselibrary.base.mvvm;

import com.example.baselibrary.config.AppApi;
import com.example.baselibrary.event.LiveBus;
import com.example.baselibrary.http.RetrofitEngine;

public abstract class BaseRepository extends AbsRepository {
    private AppApi mAppApi;

    public BaseRepository(){
        if (mAppApi == null){
            mAppApi = RetrofitEngine.getInstance().create(AppApi.class);
        }
    }

    protected void postData(Object eventKey, Object t) {
        postData(eventKey, null, t);
    }


    protected void showPageState(Object eventKey, String state) {
        postData(eventKey, state);
    }

    protected void showPageState(Object eventKey, String tag, String state) {
        postData(eventKey, tag, state);
    }

    protected void postData(Object eventKey, String tag, Object t) {
        LiveBus.getInstance().postEvent(eventKey, tag, t);
    }
}
