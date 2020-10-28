package com.example.baselibrary.base.mvvm;

import androidx.lifecycle.MutableLiveData;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class AbsRepository {
    private CompositeDisposable compositeDisposable;

    public MutableLiveData<String> mState;

    public AbsRepository(){
        mState = new MutableLiveData<>();
    }

    protected void postState(String state){
        if (mState != null){
            mState.postValue(state);
        }
    }

    protected void addDisposable(Disposable disposable){
        if (compositeDisposable != null && !compositeDisposable.isDisposed()){
            compositeDisposable.add(disposable);
        }else {
            compositeDisposable = new CompositeDisposable();
        }
    }

    public void unDisposable(){
        if (compositeDisposable != null && !compositeDisposable.isDisposed()){
            compositeDisposable.dispose();
        }
    }
}
