package com.example.baselibrary.base.mvp;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {
    private WeakReference<V> mViewWf;

    @Override
    public void attachView(V view) {
        mViewWf = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (mViewWf != null){
            mViewWf.clear();
            mViewWf = null;
        }
    }

    protected V getView(){
        return mViewWf == null ? null : mViewWf.get();
    }
}
