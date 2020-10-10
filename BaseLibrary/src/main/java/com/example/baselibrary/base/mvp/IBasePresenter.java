package com.example.baselibrary.base.mvp;

public interface IBasePresenter<V extends IBaseView> {
    void attachView(V view);

    void detachView();
}
