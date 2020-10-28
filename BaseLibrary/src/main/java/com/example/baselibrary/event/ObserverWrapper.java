package com.example.baselibrary.event;

import androidx.lifecycle.Observer;

public class ObserverWrapper<T> implements Observer<T> {
    private Observer<T> observer;
    private boolean isChange;

    public ObserverWrapper(Observer<T> observer,boolean isChange){
        this.observer = observer;
        this.isChange = isChange;
    }

    @Override
    public void onChanged(T t) {
        if (isChange){
            if (observer != null){
                observer.onChanged(t);
            }
        }else {
            isChange = true;
        }
    }
}
