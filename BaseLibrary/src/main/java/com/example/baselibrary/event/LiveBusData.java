package com.example.baselibrary.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class LiveBusData<T> extends MutableLiveData<T> {
    public boolean isFirstSubscribe;

    public LiveBusData(boolean isFirstSubscribe){
        this.isFirstSubscribe = isFirstSubscribe;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, new ObserverWrapper<>(observer,isFirstSubscribe));
    }
}
