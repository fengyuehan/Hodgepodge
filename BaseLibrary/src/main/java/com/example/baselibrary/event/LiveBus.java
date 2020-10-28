package com.example.baselibrary.event;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ConcurrentHashMap;

public class LiveBus {
    private static volatile LiveBus instance;

    private final ConcurrentHashMap<Object,LiveBusData<Object>> mLiveBus;


    public LiveBus() {
        this.mLiveBus = new ConcurrentHashMap<>();
    }

    public static LiveBus getInstance(){
        if (instance == null){
            synchronized (LiveBus.class){
                if (instance == null){
                    instance = new LiveBus();
                }
            }
        }
        return instance;
    }

    public <T> MutableLiveData<T> subscribe(Object eventKey){
        return subscribe(eventKey);
    }

    public <T> MutableLiveData<T> subscribe(Object eventKey, String tag) {
        return (MutableLiveData<T>) subscribe(eventKey, tag, Object.class);
    }

    public <T> MutableLiveData<T> subscribe(Object eventKey, Class<T> tClass) {
        return subscribe(eventKey, null, tClass);
    }

    public <T> MutableLiveData<T> subscribe(Object eventKey, String tag, Class<T> tClass) {
        String key = mergeEventKey(eventKey, tag);
        if (!mLiveBus.containsKey(key)) {
            mLiveBus.put(key, new LiveBusData<>(true));
        } else {
            LiveBusData liveBusData = mLiveBus.get(key);
            liveBusData.isFirstSubscribe = false;
        }

        return (MutableLiveData<T>) mLiveBus.get(key);
    }

    private String mergeEventKey(Object eventKey, String tag) {
        String mEventKey;
        if (!TextUtils.isEmpty(tag)){
            mEventKey = eventKey + tag;
        }else {
            mEventKey = (String) eventKey;
        }
        return mEventKey;
    }

    public <T> MutableLiveData<T> postEvent(Object eventKey, T value) {
        return postEvent(eventKey, null, value);
    }

    public <T> MutableLiveData<T> postEvent(Object eventKey, String tag, T value) {
        MutableLiveData<T> mutableLiveData = subscribe(mergeEventKey(eventKey, tag));
        mutableLiveData.postValue(value);
        return mutableLiveData;
    }

    public void clear(Object eventKey) {
        clear(eventKey, null);
    }

    public void clear(Object eventKey, String tag) {
        if (mLiveBus != null && mLiveBus.size() > 0) {
            String mEventKey = mergeEventKey(eventKey, tag);
            mLiveBus.remove(mEventKey);

        }

    }
}
